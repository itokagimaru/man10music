package io.github.itokagimaru.mun10music.gui.menu.workspace;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseMusicListHolder;
import io.github.itokagimaru.mun10music.manager.CassetteManager;
import io.github.itokagimaru.mun10music.manager.music.PublishedMusicManager;
import io.github.itokagimaru.mun10music.util.FakeEnchant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class ConvertMusicListHolder extends BaseMusicListHolder {
    public static final List<Component> ICONS_LORE = List.of(
            Component.text(
                    "左クリック: 曲を変換する"
            ).color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true)
    );
    private final UUID freameUUID;
    public ConvertMusicListHolder(Player player, UUID freameUUID) {
        super(player, "変換する曲を選択", ICONS_LORE);
        this.freameUUID = freameUUID;
    }



    @Override
    protected void onMusicLeftClick(Player player, int musicID) {
        ItemStack cassette = searchCassette(player);
        if (cassette == null) {
            player.sendMessage(Component.text("カセットテープが見つかりませんでした").color(NamedTextColor.RED));
            return;
        }
        ItemStack recordedCassette = cassette.clone();
        recordedCassette.setAmount(1);
        cassette.setAmount(cassette.getAmount() - 1);
        recordedCassette.editMeta(meta -> {
            meta.customName(Component.text("記録済みのカセットテープ").color(NamedTextColor.AQUA));
            meta.setMaxStackSize(1);
        });
        int[] musics = new int[1];
        PublishedMusicManager.isNewPublishedMusic(Man10Music.getInstance().getMySQLManager(), getMusic(musicID)).thenAccept(publishedID -> {
            if (publishedID <= 0) {
                PublishedMusicManager.savePublishedMusic(Man10Music.getInstance().getMySQLManager(), getMusic(musicID)).thenAccept(newID -> {
                    musics[0] = newID;
                });
                return;
            }
            musics[0] = publishedID;
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                ItemData.PUBLISHED_MUSIC_IDS.set(recordedCassette, musics);
                ItemData.ITEM_ID.set(recordedCassette, "recordCassette");
                FakeEnchant.addFakeEnchant(recordedCassette);
                recordedCassette.lore(CassetteManager.makeCassetteLore(getMusic(musicID)));
                player.give(recordedCassette);
            });
        });

    }

    @Override
    protected void onMusicRightClick(Player player, int musicID) {}

    @Override
    public void onClose(Player player) {
        if (!closeFlag) return;
        closeFlag = false;
        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
            WorkspacesMenuHolder workspacesMenuHolder = new WorkspacesMenuHolder(freameUUID);
            player.openInventory(workspacesMenuHolder.getInventory());
        });
    }

    private ItemStack searchCassette(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                if (("CASSETTE TAPE").equals(ItemData.ITEM_ID.get(item))) {
                    return item;
                }
            }
        }
        return null;
    }
}
