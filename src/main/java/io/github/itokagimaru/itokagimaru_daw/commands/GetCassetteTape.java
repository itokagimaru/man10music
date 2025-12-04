package io.github.itokagimaru.itokagimaru_daw.commands;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.data.StringKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GetCassetteTape implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }

        ItemStack stack = new ItemStack(Material.PAPER);
        stack.editMeta(meta -> {
            meta.customName(Component.text("カセットテープ"));
            meta.setItemModel(NamespacedKey.minecraft("cassette_tape"));
        });
        ItemData.ITEM_ID.set(stack,"CASSETTE TAPE");
        player.give(stack);

        return true;
    }
}
