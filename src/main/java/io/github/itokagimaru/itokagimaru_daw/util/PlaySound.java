package io.github.itokagimaru.itokagimaru_daw.util;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class PlaySound {
    public static void playNote(Player player, int soundId, float volume) {
        float pitch;
        soundId -= 2;//soundIdの正規化(諸事情で引数側が2からになってます
        pitch = 1;
        player.getWorld().playSound(player.getLocation(), "soundid" + soundId, SoundCategory.RECORDS, volume, pitch);
    }

    public static void playPageTurn(Player player) {
        player.getWorld().playSound(player.getLocation().add(0, 2, 0), Sound.ITEM_BOOK_PAGE_TURN, 2f, 1f);
    }

    public static void playLevelUp(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
    }
}
