package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class MainMenuHolder extends BaseGuiHolder {
    int defBPM = 60;

    public MainMenuHolder() {
        this.inv = Bukkit.createInventory(this, 9, Component.text("MainMenu"));
        setup();
    }

    public void setup() {

        ItemStack writable = new ItemStack(Material.WRITABLE_BOOK);
        MakeItem.setItemMetaByColor(writable, "編集モード", NamedTextColor.YELLOW, 0, ItemData.BUTTON_ID, "EDIT MODE");
        this.inv.setItem(2, writable);

        ItemStack disc = new ItemStack(Material.MUSIC_DISC_13);
        MakeItem.setItemMetaByColor(disc, "再生モード", NamedTextColor.YELLOW, 0, ItemData.BUTTON_ID, "PLAY MODE");
        this.inv.setItem(6, disc);

        ItemStack bar = new ItemStack(Material.BARRIER);
        MakeItem.setItemMetaByColor(bar, "しゅうりょう", NamedTextColor.DARK_RED, 0, ItemData.BUTTON_ID, "CLOSE");
        this.inv.setItem(8, bar);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player clickedPlayer = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (Objects.equals(ItemData.BUTTON_ID.get(clickedItem), "EDIT MODE")) {
            SelectEditMusicHolder selectEditMusicHolder = new SelectEditMusicHolder(clickedPlayer);
            clickedPlayer.openInventory(selectEditMusicHolder.getInventory());
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clickedItem), "PLAY MODE")) {
            SelectPlayMusicHolder selectPlayMusicHolder = new SelectPlayMusicHolder(clickedPlayer);
            clickedPlayer.openInventory(selectPlayMusicHolder.getInventory());
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clickedItem), "CLOSE")) {
            clickedPlayer.closeInventory();
        }
    }

    @Override
    public void onClose(Player player) {

    }
}
