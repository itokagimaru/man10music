package io.github.itokagimaru.mun10music.config;
import org.bukkit.configuration.file.FileConfiguration;
public final class PluginConfigData {
    private final Items items;
    private final Icons icons;
    private final Musics musics;
    public PluginConfigData(FileConfiguration config) {
        this.items = new Items(config);
        this.icons = new Icons(config);
        this.musics = new Musics(config);
    }
    public Items getItems() {
        return items;
    }
    public Icons getIcons() {
        return icons;
    }
    public Musics getMusic() {
        return musics;
    }
}
