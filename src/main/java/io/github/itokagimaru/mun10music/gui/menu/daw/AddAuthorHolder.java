package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.data.StringKey;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.manager.music.AuthorityTableManager;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.MusicManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddAuthorHolder extends BaseGuiHolder {
    Music music;
    StringKey PLAYER_HEAD_UUID = new StringKey(new NamespacedKey("man10music", "uuid"), () -> "");
    public AddAuthorHolder(Music music) {
        this.music = music;
        inv = Bukkit.createInventory(this, 54, Component.text("権限を追加するプレイヤーを選択"));
        setup();
    }

    private void setup() {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        int slot = 0;
        for (Player player : onlinePlayers) {
            if (slot >= inv.getSize()) break;
            setPlayerHead(slot, player.getUniqueId());
            slot++;
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        Player player = (Player) event.getWhoClicked();
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        if ("playerHead".equals(buttonId)) {
            String uuidStr = PLAYER_HEAD_UUID.get(clicked);
            if (uuidStr == null) return;
            UUID uuid = UUID.fromString(uuidStr);
            List<UUID> relates = music.getRelates();
            if (relates == null) {
                relates = new ArrayList<>();
            } else {
                relates = new ArrayList<>(relates);
            }
            if (relates.contains(uuid)) return;
            relates.add(uuid);
            music.setRelates(relates);
            MusicManager.saveMusicToDb(Man10Music.getInstance().getMySQLManager(), music).thenRun(() -> {
                AuthorityTableManager authorityTableManager = new AuthorityTableManager(Man10Music.getInstance().getMySQLManager());
                authorityTableManager.grant(Bukkit.getPlayer(uuid), music.getId()).thenRun(() -> {
                    Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                        player.sendMessage(Component.text("権限を付与しました").color(NamedTextColor.GREEN));
                        closeFlag = false;
                        MusicAuthorHolder authorHolder = new MusicAuthorHolder(music);
                        player.openInventory(authorHolder.getInventory());
                    });
                }).exceptionally(ex -> {
                    Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                        player.sendMessage(Component.text("権限の付与に失敗しました").color(NamedTextColor.RED));
                    });
                    return null;
                });

            });
        }
    }

    @Override
    public void onClose(Player player) {
        if (closeFlag) {
            closeFlag = false;
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                MusicAuthorHolder authorHolder = new MusicAuthorHolder(music);
                player.openInventory(authorHolder.getInventory());
            });
        }
    }

    private void setPlayerHead(int slot, UUID uuid) {
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
        ItemData.BUTTON_ID.set(head, "playerHead");
        PLAYER_HEAD_UUID.set(head, uuid.toString());
        inv.setItem(slot, head);
    }
}
