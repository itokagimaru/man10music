package io.github.itokagimaru.mun10music.gui.menu.workspace;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.daw.DawsOptionBpmHolder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;


public class OptionSetBpmHolder extends DawsOptionBpmHolder {
    ItemStack sourceItem;
    ItemStack destinationItem;

    UUID frameUuid;

    public OptionSetBpmHolder() {
        super(null, 0);
    }

    public void setUuid(UUID uuid){
        frameUuid = uuid;
    }

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
                getSelectBpmId(bpm);
                onClose(player);
            }
            case "SHIFT RIGHT" -> {
                getSelectBpmId(ItemData.BPM.get(inv.getItem(1)));
                selectedBpmId += 1;
                if (selectedBpmId > bpmList.length - 7) selectedBpmId = bpmList.length - 7;
                int bpm = bpmList[selectedBpmId];
                updateBpmIcons(bpm);
            }
            case "SHIFT LEFT" -> {
                getSelectBpmId(ItemData.BPM.get(inv.getItem(1)));
                selectedBpmId -= 1;
                if (selectedBpmId < 0) selectedBpmId = 0;
                int bpm = bpmList[selectedBpmId];
                updateBpmIcons(bpm);
            }
        }
    }

    @Override
    public void onClose(Player player) {
        if (!closeFlag) return;
        closeFlag = false;
        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
            ConvertMenuHolder convertMenuHolder = new ConvertMenuHolder(bpmList[selectedBpmId]);
            convertMenuHolder.setMusicIcon(sourceItem);
            convertMenuHolder.setCassetteIcon(destinationItem);
            convertMenuHolder.setUuid(frameUuid);
            player.openInventory(convertMenuHolder.getInventory());
        });
    }
}
