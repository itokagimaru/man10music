package io.github.itokagimaru.mun10music.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SwapItems {
    public static void mainAndHead(Player player) {
        ItemStack main = player.getInventory().getItemInMainHand().clone();
        ItemStack head = player.getInventory().getHelmet().clone();
        player.getInventory().setHelmet(main);
        player.getInventory().setItemInMainHand(head);
    }
}
