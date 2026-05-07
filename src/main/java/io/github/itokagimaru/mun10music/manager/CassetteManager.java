package io.github.itokagimaru.mun10music.manager;

import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.PublishedMusic;
import io.github.itokagimaru.mun10music.util.GetPresetItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CassetteManager {
    public static List<Component> makeCassetteLore(Music music) {
        Player composer = (Player) Bukkit.getOfflinePlayer(music.getComposerUUID());
        if (composer == null) return null;
        return List.of(
                Component.text("\"").color(NamedTextColor.GRAY)
                        .append(Component.text(music.getTitle()).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true)
                                .append(Component.text("\"was recorded in this").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false))
                        ),
                Component.text("BPM:").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                        .append(Component.text(music.getBpm()).color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true)),
                Component.text("recorded by ").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                        .append(Component.text(composer.getName()).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true))
        );
    }

    public static List<Component> makeMergedCassetteLore(List<PublishedMusic> musics) {
        List<Component> lore = new ArrayList<>();
        int i = 1;
        for (PublishedMusic publishedMusic : musics) {
            Music music = publishedMusic.getMusic();
            Player composer = (Player) Bukkit.getOfflinePlayer(music.getComposerUUID());
            if (composer == null) continue;
            lore.add(
                    Component.text(i + ". \"").color(NamedTextColor.GRAY)
                            .append(Component.text(music.getTitle()).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true)
                                    .append(Component.text("\"").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false))
                            ));
            lore.add(
                    Component.text("recorded by ").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                            .append(Component.text(composer.getName()).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true))
            );
            lore.add(
                    Component.empty()
            );
            i++;
        }
        return lore;
    }

    public static ItemStack makeMergedCassetteItem(List<PublishedMusic> musics) {
        ItemStack cassette = GetPresetItemStack.cassette();
        cassette.editMeta(meta -> {
            meta.customName(Component.text("記録済みのカセットテープ").color(NamedTextColor.AQUA));
            meta.setMaxStackSize(1);
            meta.lore(makeMergedCassetteLore(musics));
        });
        int[] musicIDs = new int[musics.size()];
        int i = 0;
        for (PublishedMusic music : musics) {
            musicIDs[i] = music.getPublishedMusicID();
            i++;
        }
        ItemData.PUBLISHED_MUSIC_IDS.set(cassette, musicIDs);
        ItemData.ITEM_ID.set(cassette, "mergedCassette");

        return cassette;
    }
}
