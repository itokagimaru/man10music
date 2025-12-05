package io.github.itokagimaru.itokagimaru_daw.listeners;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.ItemsOptionBpmHolder;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.ItemsPlayModeHolder;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.MainMenuHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.SheetMusicManager;
import io.github.itokagimaru.itokagimaru_daw.util.SwapItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class DawItemUseListener implements Listener {
    @EventHandler
    public static void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (item == null) return;
        if (item.getType() != Material.WOODEN_HOE) return;

        if (item.getItemMeta().hasItemModel()) {
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
                    Location location = player.getLocation();
                    location.setPitch(0);
                    player.teleport(location);
                    ItemsPlayModeHolder itemsPlayModeHolder = new ItemsPlayModeHolder(player);
                    player.openInventory(itemsPlayModeHolder.getInventory());
                }
            }
//            if (Objects.equals(data, NamespacedKey.minecraft("itokagimaru_daw"))) {
//                MainMenuHolder mainMenuHolder = new MainMenuHolder();
//                player.openInventory(mainMenuHolder.getInventory());
//            } else if (Objects.equals(data, NamespacedKey.minecraft("blank_sheet_music"))) {
//                item.setItemMeta(SheetMusicManager.makeSheetMusic(player));
//            } else if (Objects.equals(data, NamespacedKey.minecraft("written_sheet_music"))) {
//                SheetMusicManager.loadSheetMusic(player, item);
//            } else if (Objects.equals(data, NamespacedKey.minecraft("cassette_tape"))) {
//                if (ItemData.BPM.get(item) != -1) return;
//                ItemsOptionBpmHolder itemsOptionBpmHolder = new ItemsOptionBpmHolder();
//                itemsOptionBpmHolder.updateBpmIcons(60);
//                player.openInventory(itemsOptionBpmHolder.getInventory());
//            } else if (Objects.equals(data, NamespacedKey.minecraft("walkman"))) {
//                SwapItems.mainAndHead(player);
//                Location location = player.getLocation();
//                location.setPitch(0);
//                player.teleport(location);
//                ItemsPlayModeHolder itemsPlayModeHolder = new ItemsPlayModeHolder();
//                player.openInventory(itemsPlayModeHolder.getInventory());
//            }
//        }
        }
    }
}
