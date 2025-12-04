package io.github.itokagimaru.itokagimaru_daw.commands;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
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
public class GetSheetMusicItem implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }
        ItemStack stack = new ItemStack(Material.PAPER);
        stack.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("blank_sheet_music"));
            meta.customName(Component.text("白紙の楽譜"));
        });
        ItemData.ITEM_ID.set(stack,"BLANK SHEET");
        player.give(stack);

        return true;
    }
}
