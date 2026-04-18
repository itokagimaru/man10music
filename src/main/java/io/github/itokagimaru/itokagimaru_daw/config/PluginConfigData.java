package io.github.itokagimaru.itokagimaru_daw.config;
import org.bukkit.configuration.file.FileConfiguration;
public final class PluginConfigData {
    private final Items items;
    private final Icons icons;
    public PluginConfigData(FileConfiguration config) {
        this.items = new Items(config);
        this.icons = new Icons(config);
    }
    public Items getItems() {
        return items;
    }
    public Icons getIcons() {
        return icons;
    }
}
