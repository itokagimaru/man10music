package io.github.itokagimaru.mun10music.manager.music;

import io.github.itokagimaru.mun10music.Man10Music;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Music {
    private int id;
    private final UUID composerUUID;
    private List<UUID> relates;
    private String title;
    private final EnumMap<Track, int[]> musicByTrack;
    private int bpm;

    private Music(int id,
                  UUID composerUUID,
                  List<UUID> relates,
                  String title,
                  Map<Track, int[]> musicByTrack,
                  int bpm,
                  int defaultLength) {
        this.id = id;
        this.composerUUID = composerUUID;
        this.relates = relates == null ? new ArrayList<>() : relates;
        this.title = title;
        this.musicByTrack = normalizeTracks(musicByTrack, defaultLength);
        this.bpm = bpm;
    }

    public static Music fromDb(int id,
                               String composerUUID,
                               List<UUID> relates,
                               String title,
                               int[] musicRed,
                               int[] musicAqua,
                               int[] musicGreen,
                               int[] musicYellow,
                               int bpm) {
        EnumMap<Track, int[]> trackMap = new EnumMap<>(Track.class);
        trackMap.put(Track.RED, musicRed);
        trackMap.put(Track.AQUA, musicAqua);
        trackMap.put(Track.GREEN, musicGreen);
        trackMap.put(Track.YELLOW, musicYellow);
        return new Music(id, UUID.fromString(composerUUID), relates, title, trackMap, bpm, 0);
    }

    public static Music create(UUID composerUUID, List<UUID> relates, String title, int bpm, int length) {
        return new Music(-1, composerUUID, relates, title, createEmptyTracks(length), bpm, length);
    }

    public Music(Player player) {
        int length = Man10Music.getInstance().getPluginConfigData().getMusic().getMaxLength();
        // 自動IDは非同期のDB登録で確定する
        this.id = -1;
        this.composerUUID = player.getUniqueId();
        this.relates = new ArrayList<>();
        this.title = "New Musics";
        this.musicByTrack = createEmptyTracks(length);
        this.bpm = 60;

        Man10Music plugin = Man10Music.getInstance();
        if (plugin == null) return;
        MusicManager.saveMusicToDb(plugin.getMySQLManager(), this)
                .thenAccept(generatedId -> {
                    if (generatedId != null && generatedId > 0) {
                        this.id = generatedId;
                        return;
                    }
                    plugin.getLogger().warning("Music作成失敗: 自動採番IDが取得できませんでした");
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

    public int[] getMusic(Track track) {
        if (track == null) return new int[0];
        return musicByTrack.getOrDefault(track, new int[0]);
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

    public void setMusic(Track track, int[] musicRed) {
        this.musicByTrack.put(track, musicRed);
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    private static EnumMap<Track, int[]> createEmptyTracks(int length) {
        EnumMap<Track, int[]> trackMap = new EnumMap<>(Track.class);
        trackMap.put(Track.RED, new int[length]);
        trackMap.put(Track.AQUA, new int[length]);
        trackMap.put(Track.GREEN, new int[length]);
        trackMap.put(Track.YELLOW, new int[length]);
        return trackMap;
    }

    private static EnumMap<Track, int[]> normalizeTracks(Map<Track, int[]> source, int defaultLength) {
        EnumMap<Track, int[]> trackMap = new EnumMap<>(Track.class);
        trackMap.put(Track.RED, normalizeTrackArray(source, Track.RED, defaultLength));
        trackMap.put(Track.AQUA, normalizeTrackArray(source, Track.AQUA, defaultLength));
        trackMap.put(Track.GREEN, normalizeTrackArray(source, Track.GREEN, defaultLength));
        trackMap.put(Track.YELLOW, normalizeTrackArray(source, Track.YELLOW, defaultLength));
        return trackMap;
    }

    private static int[] normalizeTrackArray(Map<Track, int[]> source, Track track, int defaultLength) {
        if (source == null) return new int[defaultLength];
        int[] music = source.get(track);
        if (music != null) return music;
        return new int[defaultLength];
    }
}
