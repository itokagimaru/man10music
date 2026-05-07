package io.github.itokagimaru.mun10music.commands;

import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.task.PlayMusic;
import io.github.itokagimaru.mun10music.util.GetPresetItemStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class Mmusic implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }
        if (!player.hasPermission("man10music.admin")) return true;

        switch (args[0]){
            case "stop_all_music" -> {
                for (PlayMusic play : PlayMusicManager.getMusicList()) {
                    play.stopTask();
                }
                PlayMusicManager.removeMusic();
            }
            case "get" -> {
                switch (args[1]){
                    case "daw" -> player.give(GetPresetItemStack.daw());
                    case "playItem" -> player.give(GetPresetItemStack.walkMan());
                    case "workSpase" -> player.give(GetPresetItemStack.workSpace());
                    case "cassette" -> player.give(GetPresetItemStack.cassette());
                    case "radio" -> player.give(GetPresetItemStack.radio());
                }
            }
            case "help" -> {
                player.sendMessage(Component.text("/mmusic get [item] -> アイテムの入手").color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
                player.sendMessage(Component.text("/mmusic stop_all_music -> 全ての音楽を停止").color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return null;
        if (!player.hasPermission("man10music.admin")) return null;
        if (args.length == 1) {
            return List.of(
                    "get",
                    "stop_all_music"
            );
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("get")) {
                return List.of(
                        "daw",
                        "playItem",
                        "workSpase",
                        "cassette",
                        "radio"
                );
            }
        }
        return null;
    }
}
