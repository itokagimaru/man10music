package io.github.itokagimaru.mun10music;

import io.github.itokagimaru.mun10music.commands.*;
import io.github.itokagimaru.mun10music.config.PluginConfigData;
import io.github.itokagimaru.mun10music.db.MySQLManager;
import io.github.itokagimaru.mun10music.gui.listener.ClickInventoryListener;
import io.github.itokagimaru.mun10music.gui.listener.CloseInventoryListeners;
import io.github.itokagimaru.mun10music.listeners.ItemUseListener;
import io.github.itokagimaru.mun10music.listeners.PlayerInteractEntityListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public class Man10Music extends JavaPlugin implements Listener {
    public static Man10Music instance;

    public static final HashMap<UUID, ItemStack[]> inv = new HashMap<>();
    public static int MUSIC_LENGTH = 16384;//=2^14,>=2^3
    public static int MAX_PAGE = MUSIC_LENGTH / 8;
    private MySQLManager mysql;
    private PluginConfigData pluginConfigData;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        pluginConfigData = new PluginConfigData(getConfig());
        int configuredMusicLength = pluginConfigData.getMusic().getMaxLength();
        if (configuredMusicLength > 0) {
            MUSIC_LENGTH = configuredMusicLength;
            MAX_PAGE = MUSIC_LENGTH / 8;
        }

        // listener
        registerListeners(
                this,
                new ClickInventoryListener(),
                new ItemUseListener(),
                new CloseInventoryListeners(),
                new PlayerInteractEntityListener()
        );
        getSLF4JLogger().info("イベントリスナーを登録しました。");

        // command
        registerCommandWithTabCompleter("mmusic", new Mmusic(), new Mmusic());
        getSLF4JLogger().info("コマンドを登録しました。");

        //dataBase
        mysql = new MySQLManager();
        mysql.init(
                getConfig().getString("mysql.host"),
                getConfig().getInt("mysql.port"),
                getConfig().getString("mysql.database"),
                getConfig().getString("mysql.user"),
                getConfig().getString("mysql.password")
        );
        instance = this;
    }

    private void registerCommandWithTabCompleter(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            getSLF4JLogger().warn("コマンド {} が見つかりませんでした", name);
            return;
        }
        command.setExecutor(executor);
        command.setTabCompleter(tabCompleter);
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable(){}

    public static Man10Music getInstance() {
        return instance;
    }

    public MySQLManager getMySQLManager() {
        return mysql;
    }

    public PluginConfigData getPluginConfigData() {
        return pluginConfigData;
    }

}
