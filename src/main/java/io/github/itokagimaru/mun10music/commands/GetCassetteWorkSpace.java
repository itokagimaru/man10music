package io.github.itokagimaru.mun10music.commands;

import io.github.itokagimaru.mun10music.util.GetPresetItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetCassetteWorkSpace implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }
        if (!player.hasPermission("man10music.admin")) return true;

        player.give(GetPresetItemStack.workSpace());

        return true;
    }

}
