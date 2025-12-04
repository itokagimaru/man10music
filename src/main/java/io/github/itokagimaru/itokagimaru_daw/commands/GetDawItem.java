package io.github.itokagimaru.itokagimaru_daw.commands;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GetDawItem implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }
        ItemStack stack = new ItemStack(Material.WOODEN_HOE);
        stack.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("itokagimaru_daw"));
            meta.customName(Component.text("daw").color(TextColor.color(149, 229, 249)));
        });
        ItemData.ITEM_ID.set(stack,"daw");
        ItemData.MUSIC_SAVED_RED.set(stack,new int[Itokagimaru_daw.MUSIC_LENGTH]);
        player.give(stack);
        return true;
    }

}
