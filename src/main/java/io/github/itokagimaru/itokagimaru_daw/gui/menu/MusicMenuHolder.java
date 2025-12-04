package io.github.itokagimaru.itokagimaru_daw.gui.menu;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;


public class MusicMenuHolder extends BaseGuiHolder {
    public MusicMenuHolder() {
        inv = Bukkit.createInventory(this, 9, "MusicMenuHolder");
        setup();
    }
    public void setup() {
        MakeItem makeItem = new MakeItem();
        ItemStack exportMusic = new ItemStack(Material.PAPER);
        MakeItem.setItemMeta(exportMusic,"save",null,"blank_sheet_music", ItemData.BUTTON_ID,"EXPORT");
        inv.setItem(3,exportMusic);
        ItemStack importMusic = new ItemStack(Material.PAPER);
        MakeItem.setItemMeta(importMusic,"lode",null,"written_sheet_music", ItemData.BUTTON_ID,"IMPORT");
        inv.setItem(5,importMusic);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "EXPORT")) {
            MusicSheetExportHolder sheetExportHolder = new MusicSheetExportHolder();
            player.openInventory(sheetExportHolder.getInventory());
        }else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "IMPORT")) {
            MusicSheetImportHolder sheetImportHolder = new MusicSheetImportHolder();
            player.openInventory(sheetImportHolder.getInventory());
        }
    }
    @Override
    public void onClose(Player player) {}
}
