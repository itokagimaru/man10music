package io.github.itokagimaru.itokagimaru_daw.gui.menu.workspace;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.config.Icons;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.util.FakeEnchant;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import io.github.itokagimaru.itokagimaru_daw.util.PlaySound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MergeHolder extends BaseGuiHolder {
    int NUM_OF_ICONS = 5;
    ItemStack[] icons = new ItemStack[NUM_OF_ICONS];
    int SLOT_OFFSET = 1;
    boolean itemReturnFlag = true;

    UUID frameUuid;
    public void setUuid(UUID uuid){
        frameUuid = uuid;
    }

    ItemStack NULL_ICON = makeNullIcon();
    public ItemStack makeNullIcon() {
        ItemStack nullIcon = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(nullIcon,"未選択", NamedTextColor.RED, null, null, null);
        nullIcon.lore(List.of(Component.text("結合したいカセットテープを選択してください")));
        return nullIcon;
    }

    public MergeHolder() {
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
            if (getLength() < 2)return;
            int bpm = getBpm();
            if(bpm == 0)return;
            int[] mergeMusic = merge();
            Icons iconData = iconsData();
            ItemStack mergedCassette = new ItemStack(iconData.getBaseMaterial());
            // TODO: 結合済みカセットの専用cmdはIcons未定義のため、blank cmdを利用する。
            MakeItem.setItemMeta(mergedCassette, "記録済みのカセットテープ", null, iconData.getNoteBlank().getCmd(),ItemData.ITEM_ID, "recordCassette");
            ItemData.IS_NAMED.set(mergedCassette, (byte) 1);
            ItemData.IS_MERGED.set(mergedCassette, (byte) 1);
            ItemData.BPM.set(mergedCassette, bpm);
            ItemData.MUSIC_SAVED_RED.set(mergedCassette, mergeMusic);
            mergedCassette.lore(makeLore(bpm, player));
            ItemMeta meta = mergedCassette.getItemMeta();
            meta.setMaxStackSize(1);
            mergedCassette.setItemMeta(meta);
            FakeEnchant.addFakeEnchant(mergedCassette);
            player.give(mergedCassette);
            PlaySound.playAnvilUse(player);
            setup();
        }
    }

    public int getLength(){
        int len = 0;
        for (ItemStack stack : icons){
            if (stack == NULL_ICON)continue;
            len++;
        }
        return len;
    }

    public int getBpm(){
        int bpm = 0;
        for (ItemStack stack : icons){
            if(stack == NULL_ICON)continue;
            if(bpm == 0){
                bpm = ItemData.BPM.get(stack);
            }else if (bpm != ItemData.BPM.get(stack)){
                bpm = 0;
                break;
            }
        }
        return bpm;
    }

    public int[] merge(){
        int[] music;
        int[] mergeMusic = new int[Itokagimaru_daw.MUSIC_LENGTH];
        int writePos = 0;
        outer: for (ItemStack stack : icons){
            if(stack == NULL_ICON)continue;
            music = ItemData.MUSIC_SAVED_RED.get(stack);
            inner: for (int note : music){
                if (writePos >= Itokagimaru_daw.MUSIC_LENGTH)break outer;
                if (note == -1)break inner;
                mergeMusic[writePos] = note;
                writePos++;
            }
        }
        mergeMusic[writePos] = -1;
        return mergeMusic;
    }

    public List<Component> makeLore(int bpm, Player player){
        @Nullable List<Component> lore = new ArrayList<>();
        int i = 0;
        String recorder;
        String musicName;

        for (ItemStack stack : icons){
            if(stack == NULL_ICON)continue;
            recorder = ItemData.RECORDER.get(stack);
            musicName = ItemData.MUSIC_NAME.get(stack);
            i++;
            lore.addLast(Component.text(String.valueOf(i) + ".\"").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                    .append(Component.text(musicName).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true)
                            .append(Component.text("\"").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)))
            );
            lore.addLast(Component.text("    recorded by ").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                    .append(Component.text(recorder).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true)
                            .append(Component.text(".").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)))
            );
        }
        lore.addLast(Component.text("BPM: ").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                .append(Component.text(bpm).color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true))
        );
        lore.addLast(Component.text("merged by ").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                .append(Component.text(player.getName()).color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true)
                        .append(Component.text(".").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)))
        );
        return lore;
    }

    @Override
    public void onClose(Player  player) {
        if (!closeFlag)return;
        closeFlag = false;
        WorkspacesMenuHolder workspacesMenuHolder = new WorkspacesMenuHolder();
        workspacesMenuHolder.setUuid(frameUuid);
        for (ItemStack stack : icons){
            if(stack == NULL_ICON)continue;
            player.give(stack);
        }
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(), () -> {
            player.openInventory(workspacesMenuHolder.getInventory());
        });
    }
}
