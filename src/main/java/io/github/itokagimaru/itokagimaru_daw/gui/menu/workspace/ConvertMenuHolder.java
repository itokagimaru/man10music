package io.github.itokagimaru.itokagimaru_daw.gui.menu.workspace;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.MusicManager;
import io.github.itokagimaru.itokagimaru_daw.util.FakeEnchant;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import io.github.itokagimaru.itokagimaru_daw.util.PlaySound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ConvertMenuHolder extends BaseGuiHolder {
    int bpm;

    UUID frameUuid;
    public void setUuid(UUID uuid){
        frameUuid = uuid;
    }

    public ConvertMenuHolder(int bpm){
        closeFlag = true;
        inv = Bukkit.createInventory(this,18, Component.text("Converted Music "));
        this.bpm = bpm;
        setup();
    }
    public void setup() {
        ItemStack bar = new ItemStack(Material.BARRIER);
        MakeItem.setItemMeta(bar, "未選択", null, null, null, null);
        bar.lore(List.of(Component.text("楽譜を選択してください")));
        inv.setItem(2, bar);
        bar.lore(List.of(Component.text("カセットテープを選択してください")));
        inv.setItem(5, bar);
        ItemStack clock = new ItemStack(Material.CLOCK);
        MakeItem.setItemMeta(clock, "bpm/" + String.valueOf(bpm), null, null, ItemData.BUTTON_ID, "option");
        ItemData.BPM.set(clock, bpm);
        inv.setItem(7, clock);
        ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        MakeItem.setItemMeta(green, "Convert!!", null, null, ItemData.BUTTON_ID, "decision");
        inv.setItem(13, green);
    }

    public void setMusicIcon(ItemStack musicItem){
        if(("WRITTEN MUSIC").equals(ItemData.ITEM_ID.get(musicItem)) || ("musicIcon").equals(ItemData.ITEM_ID.get(musicItem))){
            ItemData.ITEM_ID.set(musicItem,"musicIcon");
            inv.setItem(2,musicItem);
        }

    }

    public void setCassetteIcon(ItemStack cassetteItem){
        if(("CASSETTE TAPE").equals(ItemData.ITEM_ID.get(cassetteItem)) || ("cassetteIcon").equals(ItemData.ITEM_ID.get(cassetteItem))) {
            ItemData.ITEM_ID.set(cassetteItem,"cassetteIcon");
            inv.setItem(5,cassetteItem);
        }
    }

    public ItemStack returnMusic(ItemStack musicIcon){
        ItemStack returnItem = musicIcon.clone();
        ItemData.ITEM_ID.set(returnItem,"WRITTEN MUSIC");
        return returnItem;
    }

    public ItemStack returnCassette(ItemStack cassetteIcon){
        ItemStack returnItem = cassetteIcon.clone();
        ItemData.ITEM_ID.set(returnItem,"CASSETTE TAPE");
        return returnItem;
    }
    @Override
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        String itemId = ItemData.ITEM_ID.get(clicked);

        if(itemId.equals("WRITTEN MUSIC")){
            setMusicIcon(clicked.clone());
            clicked.setAmount(0);
        } else if (itemId.equals("musicIcon")) {
            player.give(returnMusic(clicked.clone()));
            clicked.setAmount(0);
            ItemStack bar = new ItemStack(Material.BARRIER);
            MakeItem.setItemMeta(bar,"未選択",null,null,null,null);
            bar.lore(List.of(Component.text("楽譜を選択してください")));
            inv.setItem(2,bar);
        } else if (("CASSETTE TAPE").equals(itemId)){
            setCassetteIcon(clicked.clone());
            clicked.setAmount(0);
        } else if (("cassetteIcon").equals(itemId)) {
            player.give(returnCassette(clicked.clone()));
            clicked.setAmount(0);
            ItemStack bar = new ItemStack(Material.BARRIER);
            MakeItem.setItemMeta(bar,"未選択",null,null,null,null);
            bar.lore(List.of(Component.text("カセットテープを選択してください")));
            inv.setItem(5, bar);
        } else if (("decision").equals(buttonId)) {
            ItemStack sourceItem = inv.getItem(2).clone();
            if(!(ItemData.ITEM_ID.get(sourceItem).equals("musicIcon")))return;
            ItemStack destinationItem = inv.getItem(5).clone();
            if(!(ItemData.ITEM_ID.get(destinationItem).equals("cassetteIcon")))return;
            ItemStack item = new ItemStack(Material.PAPER);
            MakeItem.setItemMeta(item, "記録済みのカセットテープ", null, "cassette_tape", ItemData.BPM, bpm);
            ItemData.BUTTON_ID.set(item, "RECORD ITEM");
            ItemData.ITEM_ID.set(item, "recordCassette");
            int[] musicList = MusicManager.loadMusicForPdc(sourceItem);
            ItemData.MUSIC_SAVED_RED.set(item, musicList);
            ItemData.RECORDER.set(item,player.getName());
            item.lore(List.of(Component.text("BPM:" + bpm), Component.text("recorded by " + player.getName())));
            FakeEnchant.addFakeEnchant(item);
            ItemMeta meta = item.getItemMeta();
            meta.setMaxStackSize(1);
            item.setItemMeta(meta);
            player.give(item);
            PlaySound.playCompassLock(player);
            inv.setItem(2,null);
            inv.setItem(5,null);
            setup();
        } else if (("option").equals(buttonId)) {
            int bpm = ItemData.BPM.get(clicked);
            OptionSetBpmHolder optionSetBpmHolder = new OptionSetBpmHolder();
            optionSetBpmHolder.updateBpmIcons(bpm);
            optionSetBpmHolder.setReturnItem(inv.getItem(2).clone(),inv.getItem(5).clone());
            optionSetBpmHolder.setUuid(frameUuid);
            closeFlag = false;
            player.openInventory(optionSetBpmHolder.getInventory());
        }
    }

    @Override
    public void onClose(Player player){
        WorkspacesMenuHolder workspacesMenuHolder = new WorkspacesMenuHolder();
        workspacesMenuHolder.setUuid(frameUuid);
        if(!closeFlag)return;
        closeFlag = false;
        ItemStack checkMusic = inv.getItem(2);
        ItemStack checkCassette = inv.getItem(5);
        if(checkMusic != null){ //先にnullチェックを通したかったtry でやった方がきれいかも...
            if (ItemData.ITEM_ID.get(checkMusic).equals("musicIcon")){
                player.getInventory().addItem(returnMusic(checkMusic));
            }
        }
        if(checkCassette != null){
            if (ItemData.ITEM_ID.get(checkCassette).equals("cassetteIcon")){
                player.getInventory().addItem(returnCassette(checkCassette));
            }
        }
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(), () -> {
            player.openInventory(workspacesMenuHolder.getInventory());
        });
    }
}
