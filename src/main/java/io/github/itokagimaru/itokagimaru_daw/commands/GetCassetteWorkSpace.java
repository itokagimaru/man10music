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

import java.util.List;

public class GetCassetteWorkSpace implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }

        ItemStack stack = new ItemStack(Material.WOODEN_HOE);
        stack.editMeta(meta -> {
            meta.customName(Component.text("カセットテープ編集台"));
            meta.setItemModel(NamespacedKey.minecraft("cassette_workspace_item"));
            meta.lore(List.of(
                    Component.text("カセットテープを"),
                    Component.text("作成,編集できる作業台"),
                    Component.text("右クリックで設置できるようだ")
            ));
        });
        ItemData.ITEM_ID.set(stack,"CASSETTE WORKSPACE");
        player.give(stack);

        return true;
    }

}
