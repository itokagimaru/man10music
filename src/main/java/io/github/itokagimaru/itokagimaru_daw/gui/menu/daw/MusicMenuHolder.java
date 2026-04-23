package io.github.itokagimaru.itokagimaru_daw.gui.menu.daw;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.config.Items;
import io.github.itokagimaru.itokagimaru_daw.config.PluginConfigData;
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
    ItemStack daw;

    public MusicMenuHolder(ItemStack daw) {
        this.daw = daw;
        inv = Bukkit.createInventory(this, 9, "MusicMenuHolder");
        setup();
    }
    public void setup() {
        PluginConfigData config = Itokagimaru_daw.getInstance().getPluginConfigData();
        Items items = config.getItems();
        ItemStack exportMusic = new ItemStack(items.getSheetMusicBlank().getMaterial());
        MakeItem.setItemMetaByColor(exportMusic,"save", NamedTextColor.YELLOW,items.getSheetMusicBlank().getCmd(), ItemData.BUTTON_ID,"EXPORT");
        inv.setItem(3,exportMusic);
        ItemStack importMusic = new ItemStack(items.getSheetMusicWritten().getMaterial());
        MakeItem.setItemMetaByColor(importMusic,"load",NamedTextColor.YELLOW,items.getSheetMusicWritten().getCmd(), ItemData.BUTTON_ID,"IMPORT");
        inv.setItem(5,importMusic);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "EXPORT")) {
            closeFlag = false;
            MusicSheetExportHolder sheetExportHolder = new MusicSheetExportHolder(daw);
            player.openInventory(sheetExportHolder.getInventory());
        }else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "IMPORT")) {
            closeFlag = false;
            MusicSheetImportHolder sheetImportHolder = new MusicSheetImportHolder(daw);
            player.openInventory(sheetImportHolder.getInventory());
        }
    }
    @Override
    public void onClose(Player player) {
        if (!closeFlag) return;
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(), () -> {
           MainMenuHolder mainMenuHolder = new MainMenuHolder(daw);
           player.openInventory(mainMenuHolder.getInventory());
        });
    }
}
