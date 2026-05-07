package io.github.itokagimaru.mun10music.gui.menu.base;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.manager.music.AuthorityTableManager;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.util.MakeItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public abstract class BaseMusicListHolder extends BaseGuiHolder {
    private final List<Component> iconsLore;
    private List<Music> musics;
    public BaseMusicListHolder(Player openPlayer, String title, List<Component> iconsLore) {
        this.iconsLore = iconsLore;
        if (title == null) title = "";
        inv = Bukkit.createInventory(this, 54, Component.text(title));
        musicListSetup(openPlayer);
    }

    protected void musicListSetup(Player player) {
        Man10Music plugin = Man10Music.getInstance();
        if (plugin == null) return;
        inv.clear();

        AuthorityTableManager authorityManager = new AuthorityTableManager(plugin.getMySQLManager());
        authorityManager.loadAuthorizedMusic(player)
                .thenAccept(musics -> Bukkit.getScheduler().runTask(plugin, () -> {
                    if (musics == null || musics.isEmpty()) return;
                    if (inv == null || !player.isOnline()) return;
                    if (player.getOpenInventory() == null || player.getOpenInventory().getTopInventory() != inv) return;
                    this.musics = musics;
                    int i = 0;
                    for (Music music : musics) {
                        if (i >= inv.getSize()) break;
                        setMusicIcon(i, music);
                        i++;
                    }
                }));
    }


    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        Player player = (Player) event.getWhoClicked();
        String buttonID = ItemData.BUTTON_ID.get(item);
        switch (buttonID) {
            case "MUSIC"  -> {
                int musicID = getMusicID(item);
                if (event.isLeftClick()) onMusicLeftClick(player, musicID);
                else if (event.isRightClick()) onMusicRightClick(player, musicID);
            }
        }
    }

    protected abstract void onMusicLeftClick(Player player, int musicID);
    protected abstract void onMusicRightClick(Player player, int musicID);

    protected void setMusicIcon(int i, Music music) {
        ItemStack musicIcon = new ItemStack(itemsData().getSheetMusicWritten().getMaterial());
        MakeItem.setItemMeta(musicIcon, music.getTitle(), null, itemsData().getSheetMusicWritten().getCmd(), ItemData.BUTTON_ID, "MUSIC");
        setMusicID(music.getId(), musicIcon);
        musicIcon.editMeta(meta -> {
            meta.lore(iconsLore);
        });
        inv.setItem(i, musicIcon);
    }

    protected int getMusicID(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey("man10music", "music_id"), PersistentDataType.INTEGER, -1);
    }
    protected void setMusicID(int i, ItemStack item) {
        item.editMeta(meta -> {
            meta.getPersistentDataContainer().set(new NamespacedKey("man10music", "music_id"), PersistentDataType.INTEGER, i);
        });
    }

    protected Music getMusic(int musicID) {
        for (Music music : musics) {
            if (music.getId() == musicID) return music;
        }
        return null;
    }
}
