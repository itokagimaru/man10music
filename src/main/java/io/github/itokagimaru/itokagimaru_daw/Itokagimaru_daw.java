package io.github.itokagimaru.itokagimaru_daw;

import io.github.itokagimaru.itokagimaru_daw.commands.GetCassetteTape;
import io.github.itokagimaru.itokagimaru_daw.commands.GetDawItem;
import io.github.itokagimaru.itokagimaru_daw.commands.GetPlayItem;
import io.github.itokagimaru.itokagimaru_daw.commands.GetSheetMusicItem;
import io.github.itokagimaru.itokagimaru_daw.commands.SetCassetteName;
import io.github.itokagimaru.itokagimaru_daw.gui.listener.DawClickInventoryListener;
import io.github.itokagimaru.itokagimaru_daw.gui.listener.DawCloseInventoryListeners;
import io.github.itokagimaru.itokagimaru_daw.listeners.DawItemUseListener;
import io.github.itokagimaru.itokagimaru_daw.manager.MusicManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;


public final class Itokagimaru_daw extends JavaPlugin implements Listener {
    public static Itokagimaru_daw instance;

//    public static Map<UUID, int[]> savedMusicList = new HashMap<>();
    public static final HashMap<UUID, ItemStack[]> inv = new HashMap<>();
    public static final int MUSIC_LENGTH = 2048;
    public static final int MAX_PAGE = MUSIC_LENGTH / 8;

    @Override
    public void onEnable() {
        // setup music manager
//        MusicManager music = new MusicManager();
//        music.setSavedMusicList(music.loadMapFile(this));
//        getSLF4JLogger().info("曲をロードしました。");

        // listener
        registerListeners(
                this,
                new DawClickInventoryListener(),
                new DawItemUseListener(),
                new DawCloseInventoryListeners()
        );
        getSLF4JLogger().info("イベントリスナーを登録しました。");

        // command
        //getServer().getPluginManager().registerEvents(new PlayerJpinListener(),this);
        registerCommand("getDawItem", new GetDawItem());
        registerCommand("getSheetMusic", new GetSheetMusicItem());
        registerCommand("getPlayItem", new GetPlayItem());
        registerCommand("getCassetteTape", new GetCassetteTape());
        registerCommand("setCassettesName", new SetCassetteName());
        getSLF4JLogger().info("コマンドを登録しました。");

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
    public void onDisable() {
//        MusicManager music = new MusicManager();
//        music.makeMapFile(this, music.getSavedMusicList());
    }

    public static Itokagimaru_daw getInstance() {
        return instance;
    }
}
