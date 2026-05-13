package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.MusicManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MusicEditMenuHolder extends BaseGuiHolder {
    private final Music music;

    public MusicEditMenuHolder(Music music) {
        this.music = music;
        inv = Bukkit.createInventory(this, 9, Component.text("編集内容を選択/ ").append(Component.text(music.getTitle()).color(NamedTextColor.GREEN)));
        setup();
    }

    private void setup() {
        ItemStack input = new ItemStack(Material.WRITABLE_BOOK);
        input.editMeta(meta -> {
            meta.customName(Component.text("楽譜の内容を編集").color(NamedTextColor.YELLOW));
        });
        ItemData.BUTTON_ID.set(input, "EDIT");
        inv.setItem(1, input);
        ItemStack title = new ItemStack(Material.NAME_TAG);
        title.editMeta(meta -> {
            meta.customName(Component.text("タイトルを編集").color(NamedTextColor.YELLOW));
        });
        ItemData.BUTTON_ID.set(title, "TITLE");
        inv.setItem(3, title);
        ItemStack author = new ItemStack(Material.BELL);
        author.editMeta(meta -> {
            meta.customName(Component.text("権限を編集").color(NamedTextColor.YELLOW));
        });
        ItemData.BUTTON_ID.set(author, "AUTHOR");
        inv.setItem(5, author);
        ItemStack delete = new ItemStack(Material.LAVA_BUCKET);
        delete.editMeta(meta -> {
            meta.customName(Component.text("削除").color(NamedTextColor.RED));
        });
        ItemData.BUTTON_ID.set(delete, "DELETE");
        inv.setItem(7, delete);
    }



    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        if (buttonId == null) return;
        switch (buttonId) {
            case "EDIT" -> {
                closeFlag = false;
                SelectEditTrackHolder selectEditTrackHolder = new SelectEditTrackHolder(music);
                player.openInventory(selectEditTrackHolder.getInventory());
            }
            case "TITLE" -> {
                closeFlag = false;
                NamingAnvilGUI namingAnvilGUI = new NamingAnvilGUI(music);
                namingAnvilGUI.open(player);
            }
            case "AUTHOR" -> {
                closeFlag = false;
                MusicAuthorHolder musicAuthorHolder = new MusicAuthorHolder(music);
                player.openInventory(musicAuthorHolder.getInventory());
            }
            case "DELETE" -> {
                MusicManager.deleteMusicFromDb(Man10Music.getInstance().getMySQLManager(), music.getId()).thenAccept(success -> {
                    Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                        if (success) {
                            player.sendMessage(Component.text("楽曲を削除しました").color(NamedTextColor.GREEN));
                        } else {
                            player.sendMessage(Component.text("楽曲の削除に失敗しました").color(NamedTextColor.RED));
                        }
                        player.closeInventory();
                    });
                });
            }
        }
    }

    @Override
    public void onClose(Player player) {
        if (closeFlag) {
            closeFlag = false;
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                SelectEditMusicHolder selectEditMusicHolder = new SelectEditMusicHolder(player);
                player.openInventory(selectEditMusicHolder.getInventory());
            });
        }
    }
}
