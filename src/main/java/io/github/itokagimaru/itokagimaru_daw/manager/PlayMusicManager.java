package io.github.itokagimaru.itokagimaru_daw.manager;

import io.github.itokagimaru.itokagimaru_daw.task.PlayMusic;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.UUID;

public class PlayMusicManager {
    public static final HashMap<UUID, PlayMusic> playing = new HashMap<>();

    public static void setPlayingMusic(Entity target, PlayMusic play) {
        playing.put(target.getUniqueId(), play);
    }

    public static PlayMusic getMusic(Entity target) {
        return playing.get(target.getUniqueId());
    }
    public static PlayMusic[] getMusicList() {
        return playing.values().toArray(new PlayMusic[0]);
    }
    public static void removeMusic(Entity target) {
        playing.remove(target.getUniqueId());
    }
    public static void removeMusic() {
        playing.clear();
    }
}
