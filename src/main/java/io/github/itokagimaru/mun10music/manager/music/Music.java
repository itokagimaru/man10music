package io.github.itokagimaru.mun10music.manager.music;

import io.github.itokagimaru.mun10music.Man10Music;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Music {
    private int id;
    private final UUID composerUUID;
    private List<UUID> relates;
    private String title;
    private int[] music;
    private int bpm;

    public Music(int id, String composerUUID, List<UUID> relates, String title, int[] music, int bpm) {
        this.id = id;
        this.composerUUID = UUID.fromString(composerUUID);
        this.relates = relates;
        this.title = title;
        this.music = music;
        this.bpm = bpm;
    }

    public Music(Player player) {
        // 自動IDは非同期のDB登録で確定する
        this.id = -1;
        this.composerUUID = player.getUniqueId();
        this.relates = new ArrayList<>();
        this.title = "New Musics";
        this.music = new int[Man10Music.getInstance().getPluginConfigData().getMusic().getMaxLength()];
        this.bpm = 60;
        Man10Music plugin = Man10Music.getInstance();
        if (plugin == null) return;

        MusicManager.saveMusicToDb(plugin.getMySQLManager(), this)
                .thenAccept(generatedId -> {
                    if (generatedId != null && generatedId > 0) {
                        this.id = generatedId;
                    }
                });
    }

    public int getId() {
        return id;
    }

    public UUID getComposerUUID() {
        return composerUUID;
    }

    public List<UUID> getRelates() {
        return relates;
    }

    public String getTitle() {
        return title;
    }

    public int[] getMusic() {
        return music;
    }

    public int getBpm() {
        return bpm;
    }

    public void setRelates(List<UUID> relates) {
        this.relates = relates;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMusic(int[] music) {
        this.music = music;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }
}
