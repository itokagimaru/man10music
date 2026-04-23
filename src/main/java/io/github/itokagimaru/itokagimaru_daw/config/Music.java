package io.github.itokagimaru.itokagimaru_daw.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class Music {
    private final double defaultVolume;
    private final double autoPlayVolume;
    private final int radioRadius;

    public Music(FileConfiguration config) {
        this.defaultVolume = config.getDouble("musicConfig.volume.default", 1.0D);
        this.autoPlayVolume = config.getDouble("musicConfig.volume.autoPlay", 0.5D);
        this.radioRadius = config.getInt("musicConfig.radio.radius", 5);
    }

    public double getDefaultVolume() {
        return defaultVolume;
    }

    public double getAutoPlayVolume() {
        return autoPlayVolume;
    }

    public int getRadioRadius() {
        return radioRadius;
    }
}

