package io.github.itokagimaru.itokagimaru_daw.gui.menu.daw;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.SheetMusicManager;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class MusicSheetExportHolder extends BaseGuiHolder {
    public MusicSheetExportHolder(){
        inv = Bukkit.createInventory(this, 9, Component.text("MusicSheetSave"));
        setup();
    }

    public void setup(){
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
        if (Objects.equals(ItemData.ITEM_ID.get(clicked), "BLANK SHEET")){
            onClose(player);
            setup();
            ItemStack exportedSheet = clicked.clone();
            event.setCurrentItem(null);
            ItemData.ITEM_ID.set(exportedSheet,"SELECT SHEET");
            inv.setItem(4, exportedSheet);
            ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            MakeItem.setItemMetaByColor(green,"決定",NamedTextColor.GREEN,null, ItemData.BUTTON_ID,"DECISION");
            inv.setItem(8, green);
            ItemStack red =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
            MakeItem.setItemMetaByColor(red,"キャンセル", NamedTextColor.RED,null, ItemData.BUTTON_ID,"CANCEL");
            inv.setItem(0, red);
        } else if (Objects.equals(ItemData.ITEM_ID.get(clicked), "SELECT SHEET")) {
            onClose(player);
            setup();
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "DECISION")) {
            ItemStack check = inv.getItem(4);
            if (Objects.equals(ItemData.ITEM_ID.get(check), "SELECT SHEET")) {
                SheetMusicManager sheetMusicManager = new SheetMusicManager();
                player.getInventory().addItem(sheetMusicManager.makeSheetMusic(player));
                check.setAmount(0);
                player.closeInventory();
            }
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "CANCEL")) {
            setup();
        }
    }
    @Override
    public void onClose(Player player){
        ItemStack check = inv.getItem(4);
        if (check == null) return;
        if (Objects.equals(ItemData.ITEM_ID.get(check), "SELECT SHEET")) {
            ItemStack sheetMusic = new ItemStack(Material.PAPER);
            MakeItem.setItemMeta(sheetMusic,"白紙の楽譜",null,"blank_sheet_music", ItemData.ITEM_ID,"BLANK SHEET");
            player.getInventory().addItem(sheetMusic);
        }
    }


}
