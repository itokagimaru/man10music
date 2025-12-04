package io.github.itokagimaru.itokagimaru_daw.gui.menu;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DawsOptionBpmHolder extends BaseGuiHolder {
    final int[] bpmList = {1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 16, 20, 24, 25, 30, 40, 48, 50, 60, 75, 80, 100, 120, 150, 200, 240, 300, 400, 600, 1200};

    public DawsOptionBpmHolder() {
        inv = Bukkit.createInventory(this, 9, Component.text("Option/BPM"));
        setup();
    }

    public void setup() {
        ItemStack left = new ItemStack(Material.PAPER);
        ItemStack right = new ItemStack(Material.PAPER);
        MakeItem.setItemMeta(left, "", null, "next_b_left", ItemData.BUTTON_ID, "SHIFT LEFT");
        ItemData.FLAG.set(left,(byte) 1); //close時にアイテムを返還するかのフラグだよ。絶対もっといい方法があった...
        MakeItem.setItemMeta(right, "", null, "next_b_right", ItemData.BUTTON_ID, "SHIFT RIGHT");
        inv.setItem(0, left);
        inv.setItem(8, right);
    }

    public void updateBpmIcons(int bpm) {
        int selectedBpm = getSelectBpmId(bpm);
        ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        if (selectedBpm > bpmList.length - 7) selectedBpm = bpmList.length - 7;
        for (int i = 0; i < 7; i++) {
            MakeItem.setItemMeta(green, "set:" + bpmList[selectedBpm + i], null, null, ItemData.BPM, bpmList[selectedBpm + i]);
            ItemData.BUTTON_ID.set(green, "SET BPM");
            inv.setItem(i + 1, green);
        }
    }

    public int getSelectBpmId(int bpm) {
        int selectedBpm = 0;
        for (int i = 0; i < bpmList.length; i++) {
            if (bpm == bpmList[i]) {
                selectedBpm = i;
            }
        }
        return selectedBpm;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        String buttonId = ItemData.BUTTON_ID.get(clicked);
        switch (buttonId) {
            case "SET BPM" -> {
                int bpm = ItemData.BPM.get(clicked);
                DawsPlayModeHolder dawsPlayModeHolder = new DawsPlayModeHolder(bpm);
                player.openInventory(dawsPlayModeHolder.getInventory());
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
    public void onClose(Player player) {}
}
