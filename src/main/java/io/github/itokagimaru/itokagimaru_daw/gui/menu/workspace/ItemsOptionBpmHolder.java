package io.github.itokagimaru.itokagimaru_daw.gui.menu.workspace;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.daw.DawsOptionBpmHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.MusicManager;
import io.github.itokagimaru.itokagimaru_daw.util.FakeEnchant;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemsOptionBpmHolder extends DawsOptionBpmHolder {
    ItemStack sourceItem;
    ItemStack destinationItem;

    public void setReturnItem(ItemStack sourceItem, ItemStack destinationItem) {
        this.sourceItem = sourceItem;
        this.destinationItem = destinationItem;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        switch (buttonId) {
            case "SET BPM" -> {
                int bpm = ItemData.BPM.get(clicked);
                ConvertManuHolder convertManuHolder = new ConvertManuHolder(bpm);
                convertManuHolder.setMusicIcon(sourceItem);
                convertManuHolder.setCassetteIcon(destinationItem);
                player.openInventory(convertManuHolder.getInventory());
            }
            case "SHIFT RIGHT" -> {
                int selectBpmId = getSelectBpmId(ItemData.BPM.get(inv.getItem(1)));
                selectBpmId += 1;
                if (selectBpmId > bpmList.length - 7) selectBpmId = bpmList.length - 7;
                int bpm = bpmList[selectBpmId];
                updateBpmIcons(bpm);
            }
            case "SHIFT LEFT" -> {
                int selectBpmId = getSelectBpmId(ItemData.BPM.get(inv.getItem(1)));
                selectBpmId -= 1;
                if (selectBpmId < 0) selectBpmId = 0;
                int bpm = bpmList[selectBpmId];
                updateBpmIcons(bpm);
            }
        }
    }
    @Override
    public void onClose(Player player) {
        ConvertManuHolder convertManuHolder = new ConvertManuHolder(60);
        convertManuHolder.setMusicIcon(sourceItem);
        convertManuHolder.setCassetteIcon(destinationItem);
        player.openInventory(convertManuHolder.getInventory());
    }
}
