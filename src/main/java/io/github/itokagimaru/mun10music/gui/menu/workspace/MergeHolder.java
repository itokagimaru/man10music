package io.github.itokagimaru.mun10music.gui.menu.workspace;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.manager.CassetteManager;
import io.github.itokagimaru.mun10music.manager.music.PublishedMusic;
import io.github.itokagimaru.mun10music.manager.music.PublishedMusicManager;
import io.github.itokagimaru.mun10music.util.FakeEnchant;
import io.github.itokagimaru.mun10music.util.MakeItem;
import io.github.itokagimaru.mun10music.util.PlaySound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MergeHolder extends BaseGuiHolder {
    int NUM_OF_ICONS = 5;
    ItemStack[] icons = new ItemStack[NUM_OF_ICONS];
    int SLOT_OFFSET = 1;

    UUID frameUuid;

    ItemStack NULL_ICON = makeNullIcon();
    public ItemStack makeNullIcon() {
        ItemStack nullIcon = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(nullIcon,"未選択", NamedTextColor.RED, null, null, null);
        nullIcon.lore(List.of(Component.text("結合したいカセットテープを選択してください")));
        return nullIcon;
    }

    public MergeHolder(UUID frameUuid) {
        closeFlag = true;
        inv = Bukkit.createInventory(this, 9, "Merge Menu");
        setup();
    }

    public void setup(){
        for(int i = 0; i < NUM_OF_ICONS; i++){
            inv.setItem(i + SLOT_OFFSET, NULL_ICON);
            icons[i] = NULL_ICON;
        }
        ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        MakeItem.setItemMetaByColor(green, "結合！！", NamedTextColor.GREEN, null, ItemData.BUTTON_ID, "decision");
        inv.setItem(8,green);
    }

    public void setCassetteIcon(ItemStack cassetteItem, int slot){
        if(slot > NUM_OF_ICONS|| slot < 0)return;
        if (!(("recordCassette").equals(ItemData.ITEM_ID.get(cassetteItem)))) return;
        ItemData.ITEM_ID.set(cassetteItem,"cassetteIcon");
        inv.setItem(slot + SLOT_OFFSET,cassetteItem);
        icons[slot] = cassetteItem;
    }

    public void removeCassetteIcon(Player player, int slot){
        if(slot > NUM_OF_ICONS|| slot < 0)return;
        ItemStack returnCassette = inv.getItem(slot + SLOT_OFFSET).clone();
        if (!(("cassetteIcon").equals(ItemData.ITEM_ID.get(returnCassette)))) return;
        ItemData.ITEM_ID.set(returnCassette,"recordCassette");
        inv.setItem(slot + SLOT_OFFSET,NULL_ICON);
        icons[slot] = NULL_ICON;
        player.give(returnCassette);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if(clicked == null) return;
        Player player = (Player) event.getWhoClicked();
        String itemID = ItemData.ITEM_ID.get(clicked);
        String buttonID = ItemData.BUTTON_ID.get(clicked);
        if(("recordCassette").equals(itemID)){
            if (ItemData.IS_MERGED.get(clicked) == (byte) 1) return;
            for (int i = 0; i < NUM_OF_ICONS; i++) {
                if(icons[i] != NULL_ICON)continue;
                setCassetteIcon(clicked.clone(),i);
                clicked.setAmount(0);
                break;
            }
        } else if (("cassetteIcon").equals(itemID)) {
            int slot = event.getRawSlot() - SLOT_OFFSET;
            removeCassetteIcon(player,slot);
        } else if (("decision").equals(buttonID)){
            int len = getLength();
            if (len < 2) {
                player.sendMessage(Component.text(len).color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
                return;
            }
            int[] mergeMusicsID = merge();
            player.sendMessage(Component.text("結合中..." + Arrays.toString(mergeMusicsID)).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            List<CompletableFuture<PublishedMusic>> futures = new ArrayList<>();
            for (int publicId : mergeMusicsID) {
                if (publicId <= 0) continue;
                futures.add(PublishedMusicManager.loadPublishedByPublicId(Man10Music.getInstance().getMySQLManager(), publicId)
                        .thenApply(music -> music == null ? null : new PublishedMusic(publicId, music)));
            }
            CompletableFuture
                    .allOf(futures.toArray(new CompletableFuture[0]))
                    .thenAccept(v -> {
                        List<PublishedMusic> mergeMusics = new ArrayList<>();
                        for (CompletableFuture<PublishedMusic> future : futures) {
                            PublishedMusic published = future.join();
                            if (published != null) mergeMusics.add(published);
                        }

                        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                            if (!player.isOnline()) return;
                            if (mergeMusics.isEmpty()) return;
                            ItemStack mergedCassette = CassetteManager.makeMergedCassetteItem(mergeMusics);
                            FakeEnchant.addFakeEnchant(mergedCassette);
                            player.give(mergedCassette);
                            PlaySound.playAnvilUse(player);
                            setup();
                        });
                    });
        }
    }

    public int getLength(){
        int len = 0;
        for (ItemStack stack : icons){
            if (stack.equals(NULL_ICON))continue;
            len++;
        }
        return len;
    }

    public int[] merge(){
        int[] musicIDs = new int[NUM_OF_ICONS];
        int i = 0;
        for (ItemStack stack : icons) {
            if (stack == NULL_ICON || ItemData.IS_MERGED.get(stack) == (byte) 1)continue;
            for (int publishedID : ItemData.PUBLISHED_MUSIC_IDS.get(stack)) {
                if(i >= NUM_OF_ICONS) continue;
                musicIDs[i] = publishedID;
                Man10Music.getInstance().getLogger().info("merge musics id: " + publishedID);
                i++;
            }
        }
        return musicIDs;
    }

    @Override
    public void onClose(Player  player) {
        if (!closeFlag)return;
        closeFlag = false;
        WorkspacesMenuHolder workspacesMenuHolder = new WorkspacesMenuHolder(frameUuid);
        for (ItemStack stack : icons){
            if(stack == NULL_ICON)continue;
            player.give(stack);
        }
        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
            player.openInventory(workspacesMenuHolder.getInventory());
        });
    }
}
