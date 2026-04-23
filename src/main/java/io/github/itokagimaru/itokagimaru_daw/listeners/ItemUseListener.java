package io.github.itokagimaru.itokagimaru_daw.listeners;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.walkman.ItemsPlayModeHolder;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.daw.MainMenuHolder;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import io.github.itokagimaru.itokagimaru_daw.util.SwapItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
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
        ItemStack item = event.getItem();
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (item == null) return;

        String itemId = ItemData.ITEM_ID.get(item);
        switch (itemId) {
            case "daw" -> {
                event.setCancelled(true);
                MainMenuHolder mainMenuHolder = new MainMenuHolder(item);
                player.openInventory(mainMenuHolder.getInventory());
            }
            case "walkman" -> {
                event.setCancelled(true);
                SwapItems.mainAndHead(player);
                Location location = player.getLocation();
                location.setPitch(0);
                player.teleport(location);
                ItemsPlayModeHolder itemsPlayModeHolder = new ItemsPlayModeHolder(player);
                player.openInventory(itemsPlayModeHolder.getInventory());
            }
        }
    }
}
