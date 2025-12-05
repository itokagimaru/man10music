package io.github.itokagimaru.itokagimaru_daw.commands;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.manager.ByteArrayManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class CassetteTransfer implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }

        if (args.length != 0) {
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
            int[] music = ByteArrayManager.decode(ItemData.BYTE_LIST.get(item));
            ItemData.MUSIC_SAVED_RED.set(item, music);
            meta = item.getItemMeta();
            meta.getPersistentDataContainer().remove(new NamespacedKey("itokagimaru_daw","bytelist"));
        }
        return false;
    }
}
