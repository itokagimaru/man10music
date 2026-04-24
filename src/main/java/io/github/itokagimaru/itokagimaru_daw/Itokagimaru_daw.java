package io.github.itokagimaru.itokagimaru_daw;

import io.github.itokagimaru.itokagimaru_daw.commands.*;
import io.github.itokagimaru.itokagimaru_daw.config.PluginConfigData;
import io.github.itokagimaru.itokagimaru_daw.db.MySQLManager;
import io.github.itokagimaru.itokagimaru_daw.gui.listener.ClickInventoryListener;
import io.github.itokagimaru.itokagimaru_daw.gui.listener.CloseInventoryListeners;
import io.github.itokagimaru.itokagimaru_daw.listeners.ItemUseListener;
import io.github.itokagimaru.itokagimaru_daw.listeners.PlayerInteractEntityListener;
import io.github.itokagimaru.itokagimaru_daw.listeners.PlayerJoinListener;
import io.github.itokagimaru.itokagimaru_daw.listeners.PlayerQuitListener;
import io.github.itokagimaru.itokagimaru_daw.manager.InventoryManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public final class Itokagimaru_daw extends JavaPlugin implements Listener {
    public static Itokagimaru_daw instance;

    public static final HashMap<UUID, ItemStack[]> inv = new HashMap<>();
    public static int MUSIC_LENGTH = 16384;//=2^14,>=2^3
    public static int MAX_PAGE = MUSIC_LENGTH / 8;
    private MySQLManager mysql;
    public InventoryManager inventoryManager;
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
                new PlayerQuitListener(),
                new PlayerInteractEntityListener(),
                new PlayerJoinListener()
        );
        getSLF4JLogger().info("イベントリスナーを登録しました。");

        // command
        registerCommand("getDawItem", new GetDawItem());
        registerCommand("getSheetMusic", new GetSheetMusicItem());
        registerCommand("getPlayItem", new GetPlayItem());
        registerCommand("getCassetteTape", new GetCassetteTape());
        registerCommand("setCassettesName", new SetCassetteName());
        registerCommand("cassetteTransfer", new CassetteTransfer());
        registerCommand("getCassetteWorkSpace", new GetCassetteWorkSpace());
        registerCommand("getRadio", new GetRadio());
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
        inventoryManager = new InventoryManager(mysql);
        instance = this;
    }

    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command == null) throw new RuntimeException(String.format("コマンド %s が見つかりませんでした。", name));
        command.setExecutor(executor);
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable(){}

    public static Itokagimaru_daw getInstance() {
        return instance;
    }

    public PluginConfigData getPluginConfigData() {
        return pluginConfigData;
    }

}
