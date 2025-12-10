package io.github.itokagimaru.itokagimaru_daw.gui.menu.daw;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import io.github.itokagimaru.itokagimaru_daw.manager.SheetMusicManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class MusicSheetImportHolder extends BaseGuiHolder {
    public MusicSheetImportHolder(){
        inv = Bukkit.createInventory(this, 9, Component.text("MusicSheetLoad"));
        setup();
    }
    public void setup(){
        MakeItem makeItem = new MakeItem();
        ItemStack air = new ItemStack(Material.AIR);
        for (int i = 0; i < 9;i++){
            inv.setItem(i, air);
        }
        ItemStack bar = new ItemStack(Material.BARRIER);
        MakeItem.setItemMeta(bar,"未選択",null,null, null,null);
        inv.setItem(4, bar);
    }

    @Override
    public void onClick(InventoryClickEvent event){
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (Objects.equals(ItemData.ITEM_ID.get(clicked), "WRITTEN MUSIC")) {
            ItemStack importedSheet = clicked.clone();
            ItemData.ITEM_ID.set(importedSheet,"SELECT MUSIC");
            inv.setItem(4, importedSheet);
            ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            MakeItem.setItemMeta(green,"決定",null,null, ItemData.BUTTON_ID,"DECISION");
            inv.setItem(7, green);
            ItemStack red =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
            MakeItem.setItemMeta(red,"キャンセル",null,null, ItemData.BUTTON_ID,"CANCEL");
            inv.setItem(1, red);
        } else if (Objects.equals(ItemData.ITEM_ID.get(clicked), "SELECT SHEET")) {
            setup();
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "DECISION")) {
            SheetMusicManager.loadSheetMusic(player,inv.getItem(4));
            player.closeInventory();
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "CANCEL")) {
            player.closeInventory();
        }
    }
    @Override
    public void onClose(Player player) {}
}


