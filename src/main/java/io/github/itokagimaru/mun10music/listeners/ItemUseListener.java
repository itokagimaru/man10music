package io.github.itokagimaru.mun10music.listeners;

import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.walkman.ItemsPlayModeHolder;
import io.github.itokagimaru.mun10music.gui.menu.daw.MainMenuHolder;
import io.github.itokagimaru.mun10music.util.SwapItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemUseListener implements Listener {
    @EventHandler
    public static void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (item == null || item.getType() == Material.AIR) {
            ItemStack head = player.getInventory().getHelmet();
            if (head == null || head.getType() == Material.AIR) return;
            if (("walkman").equals(ItemData.ITEM_ID.get(head))) {
                useWalkman(player);
                return;
            }
            return;
        }
        String itemId = ItemData.ITEM_ID.get(item);
        switch (itemId) {
            case "daw" -> {
                event.setCancelled(true);
                MainMenuHolder mainMenuHolder = new MainMenuHolder();
                player.openInventory(mainMenuHolder.getInventory());
            }
            case "walkman" -> {
                event.setCancelled(true);
                SwapItems.mainAndHead(player);
                useWalkman(player);
            }
        }


    }

    private static void useWalkman(Player player) {
        Location location = player.getLocation();
        location.setPitch(0);
        player.teleport(location);
        ItemsPlayModeHolder itemsPlayModeHolder = new ItemsPlayModeHolder(player, player);
        player.openInventory(itemsPlayModeHolder.getInventory());
    }
}
