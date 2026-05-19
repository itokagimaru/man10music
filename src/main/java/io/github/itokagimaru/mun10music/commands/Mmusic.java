package io.github.itokagimaru.mun10music.commands;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.manager.MusicYamlManager;
import io.github.itokagimaru.mun10music.manager.music.PublishedMusicManager;
import io.github.itokagimaru.mun10music.task.PlayMusic;
import io.github.itokagimaru.mun10music.util.GetPresetItemStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
                    case "cassette" -> {
                        if (args.length == 2) player.give(GetPresetItemStack.cassette());
                        else {
                            ItemStack item = GetPresetItemStack.cassette();
                            ItemData.PUBLISHED_MUSIC_IDS.set(item, new int[] {Integer.parseInt(args[2])});
                            ItemData.ITEM_ID.set(item, "recordCassette");
                            player.give(item);
                        }
                    }
                }
            }
            case "help" -> {
                player.sendMessage(Component.text("/mmusic get [item] -> アイテムの入手").color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
                player.sendMessage(Component.text("/mmusic stop_all_music -> 全ての音楽を停止").color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
            }
            case "yml" -> {
                if (args.length < 2) return false;
                String rawID = args[1];
                int id;
                try {
                    id = Integer.parseInt(rawID);
                } catch (NumberFormatException e) {
                    player.sendMessage("IDは数字でなければなりません");
                    return false;
                }
                PublishedMusicManager.loadPublishedByPublicId(Man10Music.getInstance().getMySQLManager(), id).thenAccept(music -> {
                    if (music == null) {
                        player.sendMessage("IDに対応する曲が見つかりませんでした");
                        return;
                    }

                    String yaml = MusicYamlManager.toYaml(music);
                    Path baseDir = Man10Music.getInstance().getDataFolder().toPath().resolve("exports");
                    Path outFile = baseDir.resolve("music_" + id + ".yml");
                    try {
                        Files.createDirectories(baseDir);
                        Files.writeString(outFile, yaml, StandardCharsets.UTF_8,
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                            player.sendMessage(Component.text("YAMLを保存しました: " + outFile).color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
                        });
                    } catch (IOException e) {
                        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                            player.sendMessage(Component.text("YAML保存に失敗しました: " + e.getMessage()).color(net.kyori.adventure.text.format.NamedTextColor.RED));
                        });
                    }
                });
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
                        "cassette"
                );
            } else if (args[0].equalsIgnoreCase("yml")) {
                return List.of(
                        "id"
                );
            }
        }
        return null;
    }
}
