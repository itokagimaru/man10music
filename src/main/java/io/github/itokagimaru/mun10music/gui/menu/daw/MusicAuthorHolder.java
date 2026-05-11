package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.data.StringKey;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.manager.music.AuthorityTableManager;
import io.github.itokagimaru.mun10music.manager.music.Music;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class MusicAuthorHolder extends BaseGuiHolder {
    Music music;
    StringKey PLAYER_HEAD_UUID = new StringKey(new NamespacedKey("man10music", "uuid"), () -> "");
    public MusicAuthorHolder(Music music) {
        this.music = music;
        inv = Bukkit.createInventory(this, 54, Component.text("権限者の管理").append(Component.text(music.getTitle()).color(NamedTextColor.GREEN)));
        setup();
    }
    private void setup() {
        ItemStack adder = new ItemStack(Material.NETHER_STAR);
        adder.editMeta(meta -> {
            meta.customName(Component.text("権限者を追加").color(NamedTextColor.GREEN));
        });
        ItemData.BUTTON_ID.set(adder, "addAuthor");
        inv.setItem(inv.getSize() - 1, adder);
        AuthorityTableManager authorityManager = new AuthorityTableManager(Man10Music.getInstance().getMySQLManager());
        authorityManager.loadAuthorizedUuidsByMusicId(music.getId()).thenAccept(uuids -> {
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                int slot = 0;
                for (UUID uuid : uuids) {
                    if (slot >= inv.getSize()) break;
                    setAuthorHead(slot, uuid);
                    slot++;
                }
            });
        });
    }

    private void setAuthorHead(int slot, UUID uuid) {
        if (uuid == null) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if (skullMeta == null) return;
        skullMeta.setOwningPlayer(offlinePlayer);
        head.setItemMeta(skullMeta);
        head.editMeta(meta -> {
            meta.customName(Component.text(offlinePlayer.getName()).color(NamedTextColor.GREEN));
        });
        ItemData.BUTTON_ID.set(head, "authorHead");
        PLAYER_HEAD_UUID.set(head, uuid.toString());
        inv.setItem(slot, head);
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
            case "authorHead" -> {
                String uuidStr = PLAYER_HEAD_UUID.get(clicked);
                if (uuidStr == null) return;
                UUID uuid = UUID.fromString(uuidStr);
                if (uuid.equals(player.getUniqueId())) {
                    player.sendMessage(Component.text("自分は削除できません").color(NamedTextColor.RED));
                    return;
                }
                Player targetPlayer = Bukkit.getPlayer(uuid);
                if (targetPlayer == null) return;
                AuthorityTableManager authorityManager = new AuthorityTableManager(Man10Music.getInstance().getMySQLManager());
                authorityManager.revoke(targetPlayer, music.getId()).thenAccept(isCompleted -> {
                    Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                        if (isCompleted) {
                            inv.remove(clicked);
                            player.sendMessage(Component.text("権限を削除しました").color(NamedTextColor.GREEN));
                        } else {
                            player.sendMessage(Component.text("権限の削除に失敗しました").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true));
                        }
                    });
                });
            }
            case "addAuthor" -> {
                closeFlag = false;
                AddAuthorHolder addAuthorHolder = new AddAuthorHolder(music);
                player.openInventory(addAuthorHolder.getInventory());
            }
        }

    }

    @Override
    public void onClose(Player player) {
        if (closeFlag) {
            closeFlag = false;
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                MusicEditMenuHolder editMenuHolder = new MusicEditMenuHolder(music);
                player.openInventory(editMenuHolder.getInventory());
            });
        }
    }


}
