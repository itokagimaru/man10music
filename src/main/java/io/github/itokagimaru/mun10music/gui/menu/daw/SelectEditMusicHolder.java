package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseMusicListHolder;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.MusicManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SelectEditMusicHolder extends BaseMusicListHolder {
    private static final List<Component> ICONS_LORE = List.of(
            Component.text(
                    "左クリック: メニューを開く"
            ).decoration(TextDecoration.BOLD, true).color(NamedTextColor.YELLOW),
            Component.text(
                    "右クリック: 曲を編集"
            ).decoration(TextDecoration.BOLD, true).color(NamedTextColor.AQUA)
    );

    public SelectEditMusicHolder(Player openPlayer) {
        super(openPlayer, "編集する曲を選択", ICONS_LORE);
        setup();
    }

    private void setup() {
        ItemStack adder = new ItemStack(Material.PAPER);
        adder.editMeta(meta -> {
            meta.customName(Component.text("新規作成"));
            meta.setItemModel(NamespacedKey.minecraft("nether_star"));
        });
        ItemData.BUTTON_ID.set(adder, "ADD");
        inv.setItem(inv.getSize() - 1, adder);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        Player player = (Player) event.getWhoClicked();
        switch (ItemData.BUTTON_ID.get(item)) {
            case "ADD" -> {
                Man10Music plugin = Man10Music.getInstance();
                if (plugin == null) return;
                MusicManager.createMusicForPlayer(plugin.getMySQLManager(), player)
                        .thenRun(() -> Bukkit.getScheduler().runTask(plugin, () -> {
                            musicListSetup(player);
                            setup();
                        }));
            }
        }
        super.onClick(event);
    }

    @Override
    protected void onMusicLeftClick(Player player, int musicID) {
        Music music = getMusic(musicID);
        closeFlag = false;
        MusicEditMenuHolder musicEditMenuHolder = new MusicEditMenuHolder(music);
        player.openInventory(musicEditMenuHolder.getInventory());
    }

    @Override
    protected void onMusicRightClick(Player player, int musicID) {
        Music music = getMusic(musicID);
        closeFlag = false;
        SelectEditTrackHolder selectEditTrackHolder = new SelectEditTrackHolder(music);
        player.openInventory(selectEditTrackHolder.getInventory());
    }

    @Override
    public void onClose(Player player) {
        if (closeFlag) {
            closeFlag = false;
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                MainMenuHolder mainMenuHolder = new MainMenuHolder();
                player.openInventory(mainMenuHolder.getInventory());
            });
        }
    }
}
