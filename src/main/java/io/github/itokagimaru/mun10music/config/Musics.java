package io.github.itokagimaru.mun10music.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class Musics {
    private final int maxLength;
    private final float defaultVolume;
    private final float autoPlayVolume;
    private final Double soundRange;

    public Musics(FileConfiguration config) {
        this.maxLength = config.getInt("musicConfig.musics.maxLength", 16384);
        this.defaultVolume = (float) config.getDouble("musicConfig.volume.default", 1.0D);
        this.autoPlayVolume = (float) config.getDouble("musicConfig.volume.autoPlay", 0.5D);
        this.soundRange = config.getDouble("musicConfig.volume.radius", 5);
    }

    public int getMaxLength() {
        return maxLength;
    }

    public float getDefaultVolume() {
        return defaultVolume;
    }

    public float getAutoPlayVolume() {
        return autoPlayVolume;
    }

    public Double getSoundRange() {
        return soundRange;
    }
}

