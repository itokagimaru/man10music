package io.github.itokagimaru.mun10music.gui.menu.base;

import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class BaseOptionBpmHolder extends BaseGuiHolder {
    protected final int[] bpmList = {1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 16, 20, 24, 25, 30, 40, 48, 50, 60, 75, 80, 100, 120, 150, 200, 240, 300, 400, 600, 1200};
    protected int selectedBpmId;
    protected Music music;

    protected BaseOptionBpmHolder(String title, Music music) {
        this.music = music;
        inv = Bukkit.createInventory(this, 9, Component.text(title));
        baseSetup();
    }

    protected void baseSetup() {
        Icons icons = iconsData();
        ItemStack left = new ItemStack(icons.getTriangleLeft().getMaterial());
        ItemStack right = new ItemStack(icons.getTriangleRight().getMaterial());
        MakeItem.setItemMeta(left, "", null, icons.getTriangleLeft().getCmd(), ItemData.BUTTON_ID, "SHIFT LEFT");
        MakeItem.setItemMeta(right, "", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "SHIFT RIGHT");
        inv.setItem(0, left);
        inv.setItem(8, right);
        updateBpmIcons(music.getBpm());
    }

    public void updateBpmIcons(int bpm) {
        getSelectBpmId(bpm);
        if (selectedBpmId > bpmList.length - 7) selectedBpmId = bpmList.length - 7;
        for (int i = 0; i < 7; i++) {
            ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            MakeItem.setItemMetaByColor(green, "set:" + bpmList[selectedBpmId + i], NamedTextColor.GREEN, 0, ItemData.BPM, bpmList[selectedBpmId + i]);
            ItemData.BUTTON_ID.set(green, "SET BPM");
            inv.setItem(i + 1, green);
        }
    }

    public void getSelectBpmId(int bpm) {
        selectedBpmId = 0;
        for (int i = 0; i < bpmList.length; i++) {
            if (bpm == bpmList[i]) {
                selectedBpmId = i;
            }
        }
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
                onSetBpm(player, bpm);
            }
            case "SHIFT RIGHT" -> {
                ItemStack firstBpmItem = inv.getItem(1);
                if (firstBpmItem == null) return;
                getSelectBpmId(ItemData.BPM.get(firstBpmItem));
                selectedBpmId += 1;
                if (selectedBpmId > bpmList.length - 7) selectedBpmId = bpmList.length - 7;
                int bpm = bpmList[selectedBpmId];
                updateBpmIcons(bpm);
            }
            case "SHIFT LEFT" -> {
                ItemStack firstBpmItem = inv.getItem(1);
                if (firstBpmItem == null) return;
                getSelectBpmId(ItemData.BPM.get(firstBpmItem));
                selectedBpmId -= 1;
                if (selectedBpmId < 0) selectedBpmId = 0;
                int bpm = bpmList[selectedBpmId];
                updateBpmIcons(bpm);
            }
        }
    }

    protected abstract void onSetBpm(Player player, int bpm);
}

