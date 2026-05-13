package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.Track;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SelectEditTrackHolder extends BaseGuiHolder {
    Music music;

    public SelectEditTrackHolder(Music music) {
        this.music = music;
        inv = Bukkit.createInventory(this, 9, Component.text("編集するトラックを選択"));
        setup();
    }

    public void setup() {
        ItemStack trackRed = new ItemStack(iconsData().getBaseMaterial());
        trackRed.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("redstone_block"));
            meta.customName(Component.text("TrackOfRed").color(NamedTextColor.RED));
        });
        ItemData.BUTTON_ID.set(trackRed, "red");
        inv.setItem(0, trackRed);

        ItemStack trackAqua = new ItemStack(iconsData().getBaseMaterial());
        trackAqua.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("diamond_block"));
            meta.customName(Component.text("TrackOfAqua").color(NamedTextColor.AQUA));
        });
        ItemData.BUTTON_ID.set(trackAqua, "aqua");
        inv.setItem(1, trackAqua);

        ItemStack trackGreen = new ItemStack(iconsData().getBaseMaterial());
        trackGreen.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("emerald_block"));
            meta.customName(Component.text("TrackOfGreen").color(NamedTextColor.GREEN));
        });
        ItemData.BUTTON_ID.set(trackGreen, "green");
        inv.setItem(2, trackGreen);

        ItemStack trackYellow = new ItemStack(iconsData().getBaseMaterial());
        trackYellow.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("gold_block"));
            meta.customName(Component.text("TrackOfYellow").color(NamedTextColor.YELLOW));
        });
        ItemData.BUTTON_ID.set(trackYellow, "yellow");
        inv.setItem(3, trackYellow);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        if (buttonId == null) return;
        Track editTrack = Track.getByID(buttonId);
        if (editTrack == null || editTrack == Track.UNKNOWN) return;
        closeFlag = false;
        InputModeHolder inputModeHolder = new InputModeHolder(music, editTrack);
        inputModeHolder.open(player);
    }

    @Override
    public void onClose(Player player) {
        if (closeFlag) {
            closeFlag = false;
            SelectEditMusicHolder selectEditMusicHolder = new SelectEditMusicHolder(player);
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> player.openInventory(selectEditMusicHolder.getInventory()));
        }
    }
}
