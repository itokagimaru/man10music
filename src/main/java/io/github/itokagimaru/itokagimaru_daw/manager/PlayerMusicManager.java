package io.github.itokagimaru.itokagimaru_daw.manager;

import io.github.itokagimaru.itokagimaru_daw.task.PlayMusic;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerMusicManager {
    public static final HashMap<UUID, PlayMusic> playing = new HashMap<>();

    public static void setPlayingMusic(Player player, PlayMusic play) {
        playing.put(player.getUniqueId(), play);
    }

    public static PlayMusic getMusic(Player player) {
        return playing.get(player.getUniqueId());
    }
    public static void removeMusic(Player player) {
        playing.remove(player.getUniqueId());
    }
}
