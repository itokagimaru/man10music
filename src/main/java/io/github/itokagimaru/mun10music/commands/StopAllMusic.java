package io.github.itokagimaru.mun10music.commands;

import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.task.PlayMusic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StopAllMusic implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }
        if (!player.hasPermission("man10music.admin")) return true;
        for (PlayMusic play : PlayMusicManager.getMusicList()) {
            play.stopTask();
        }
        PlayMusicManager.removeMusic();

        return true;
    }
}
