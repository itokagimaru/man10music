package io.github.itokagimaru.itokagimaru_daw.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public class SetCassetteName implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }
        if (args.length != 1) {
            player.sendMessage(Component.text("引数に異常があります"));
            return false;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        NamespacedKey data = new NamespacedKey("name", "key");
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("メインハンドにカセットテープを持って実行してください"));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage(Component.text("メインハンドにカセットテープを持って実行してください"));
            return true;
        }
        if (item.getItemMeta().getItemModel() != null) {
            data = item.getItemMeta().getItemModel();
        }

        if (item.getType() == Material.PAPER && Objects.equals(data, NamespacedKey.minecraft("cassette_tape"))) {
            @Nullable List<Component> lore = meta.lore();
            int lines = (lore != null) ? lore.size() : 0;
            if (lines == 2) {
                lore.addFirst(Component.text("\"" + args[0] + "\" was recorded in this"));
                meta.lore(lore);
                item.setItemMeta(meta);
                return true;
            } else if (lines == 3) {
                player.sendMessage(Component.text("もう設定されてるよ"));
                return true;
            } else {
                player.sendMessage(Component.text("そのカセットテープ怪しいかも..."));
                return true;
            }

        }
        return false;
    }
}
