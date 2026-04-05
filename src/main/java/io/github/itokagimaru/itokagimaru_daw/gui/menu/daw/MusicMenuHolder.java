package io.github.itokagimaru.itokagimaru_daw.gui.menu.daw;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.format.NamedTextColor;
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
        ItemStack exportMusic = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(exportMusic,"save", NamedTextColor.YELLOW,"blank_sheet_music", ItemData.BUTTON_ID,"EXPORT");
        inv.setItem(3,exportMusic);
        ItemStack importMusic = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(importMusic,"load",NamedTextColor.YELLOW,"written_sheet_music", ItemData.BUTTON_ID,"IMPORT");
        inv.setItem(5,importMusic);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "EXPORT")) {
            closeFlag = false;
            MusicSheetExportHolder sheetExportHolder = new MusicSheetExportHolder();
            player.openInventory(sheetExportHolder.getInventory());
        }else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "IMPORT")) {
            closeFlag = false;
            MusicSheetImportHolder sheetImportHolder = new MusicSheetImportHolder();
            player.openInventory(sheetImportHolder.getInventory());
        }
    }
    @Override
    public void onClose(Player player) {
        if (!closeFlag) return;
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(), () -> {
           MainMenuHolder mainMenuHolder = new MainMenuHolder();
           player.openInventory(mainMenuHolder.getInventory());
        });
    }
}
