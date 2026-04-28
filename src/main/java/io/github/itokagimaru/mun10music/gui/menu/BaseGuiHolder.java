package io.github.itokagimaru.mun10music.gui.menu;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.config.Items;
import io.github.itokagimaru.mun10music.config.Music;
import io.github.itokagimaru.mun10music.config.PluginConfigData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class BaseGuiHolder implements InventoryHolder {
    protected Inventory inv;
    public boolean closeFlag = true;
    private final PluginConfigData pluginConfigData;
    private final Items items;
    private final Icons icons;
    private final Music music;

    protected BaseGuiHolder() {
        Man10Music plugin = Man10Music.getInstance();
        if (plugin == null) {
            throw new IllegalStateException("プラグイン初期化前にGUIが生成されました。");
        }
        this.pluginConfigData = plugin.getPluginConfigData();
        this.items = pluginConfigData.getItems();
        this.icons = pluginConfigData.getIcons();
        this.music = pluginConfigData.getMusic();
    }

    public abstract void onClick(InventoryClickEvent event);

    public abstract void onClose(Player player);

    protected final PluginConfigData configData() {
        return pluginConfigData;
    }

    protected final Items itemsData() {
        return items;
    }

    protected final Icons iconsData() {
        return icons;
    }

    protected final Music musicData() {
        return music;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }
}
