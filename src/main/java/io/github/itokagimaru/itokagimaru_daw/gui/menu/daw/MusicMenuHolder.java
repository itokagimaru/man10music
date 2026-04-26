package io.github.itokagimaru.itokagimaru_daw.gui.menu.daw;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;


public class MusicMenuHolder extends BaseGuiHolder {
    ItemStack daw;
    private final int mainSlot;

    public MusicMenuHolder(ItemStack daw, int mainSlot) {
        this.daw = daw;
        this.mainSlot = mainSlot;
        inv = Bukkit.createInventory(this, 9, "MusicMenuHolder");
        setup();
    }
    public void setup() {
        ItemStack exportMusic = new ItemStack(itemsData().getSheetMusicBlank().getMaterial());
        MakeItem.setItemMetaByColor(exportMusic,"save", NamedTextColor.YELLOW,itemsData().getSheetMusicBlank().getCmd(), ItemData.BUTTON_ID,"EXPORT");
        inv.setItem(3,exportMusic);
        ItemStack importMusic = new ItemStack(itemsData().getSheetMusicWritten().getMaterial());
        MakeItem.setItemMetaByColor(importMusic,"load",NamedTextColor.YELLOW,itemsData().getSheetMusicWritten().getCmd(), ItemData.BUTTON_ID,"IMPORT");
        inv.setItem(5,importMusic);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "EXPORT")) {
            closeFlag = false;
            MusicSheetExportHolder sheetExportHolder = new MusicSheetExportHolder(daw, mainSlot);
            player.openInventory(sheetExportHolder.getInventory());
        }else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "IMPORT")) {
            closeFlag = false;
            MusicSheetImportHolder sheetImportHolder = new MusicSheetImportHolder(daw, mainSlot);
            player.openInventory(sheetImportHolder.getInventory());
        }
    }
    @Override
    public void onClose(Player player) {
        if (!closeFlag) return;
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(), () -> {
           MainMenuHolder mainMenuHolder = new MainMenuHolder(daw, mainSlot);
           player.openInventory(mainMenuHolder.getInventory());
        });
    }
}
