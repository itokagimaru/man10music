package io.github.itokagimaru.itokagimaru_daw.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class Music {
    private final int maxLength;
    private final float defaultVolume;
    private final float autoPlayVolume;
    private final Double soundRange;

    public Music(FileConfiguration config) {
        this.maxLength = config.getInt("musicConfig.music.maxLength", 16384);
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

