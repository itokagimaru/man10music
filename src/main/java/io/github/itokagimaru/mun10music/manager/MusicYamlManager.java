package io.github.itokagimaru.mun10music.manager;

import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.MusicDataCodec;
import io.github.itokagimaru.mun10music.manager.music.Track;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class MusicYamlManager {
    public static String toYaml(Music music) {
        if (music == null) return "";

        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("version", 2);
        yaml.set("id", music.getId());
        yaml.set("composer", music.getComposerUUID().toString());

        List<String> relates = new ArrayList<>();
        if (music.getRelates() != null) {
            for (UUID uuid : music.getRelates()) {
                relates.add(uuid.toString());
            }
        }
        yaml.set("relates", relates);
        yaml.set("title", music.getTitle() == null ? "" : music.getTitle());
        yaml.set("bpm", music.getBpm());

        setMusicEntry(yaml, "musicRed", music.getMusic(Track.RED));
        setMusicEntry(yaml, "musicAqua", music.getMusic(Track.AQUA));
        setMusicEntry(yaml, "musicGreen", music.getMusic(Track.GREEN));
        setMusicEntry(yaml, "musicYellow", music.getMusic(Track.YELLOW));

        return yaml.saveToString();
    }

    private static void setMusicEntry(YamlConfiguration yaml, String key, int[] musicArray) {
        if (musicArray == null) {
            // 楽曲配列がnullのときは空として保存
            yaml.set(key, "");
            yaml.set(key + "Length", 0);
            return;
        }
        byte[] musicBytes = MusicDataCodec.toByteArray(musicArray);
        String encoded = Base64.getEncoder().encodeToString(musicBytes);
        yaml.set(key, encoded);
        yaml.set(key + "Length", musicArray.length);
    }
}
