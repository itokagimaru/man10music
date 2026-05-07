package io.github.itokagimaru.mun10music.manager.music;

public class PublishedMusic {
    private final int publishedMusicID;
    private final Music music;

    public PublishedMusic(int publishedMusicID, Music music) {
        this.publishedMusicID = publishedMusicID;
        this.music = music;
    }

    public int getPublishedMusicID() {
        return publishedMusicID;
    }
    public Music getMusic() {
        return music;
    }
}
