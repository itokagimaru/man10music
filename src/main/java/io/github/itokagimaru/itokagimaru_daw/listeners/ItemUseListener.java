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
        if (item.getItemMeta().hasItemModel()) {
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
//                case "CASSETTE WORKSPACE" -> {
//                    event.setCancelled(true);
//                    Location location = player.getLocation();
//                    if(location.getBlock().getType().isSolid()){
//                        player.sendMessage("ここには設置できません");
//                        return;
//                    }
//                    Block blockUnder = location.clone().subtract(0, 1, 0).getBlock();
//                    if (!blockUnder.getType().isSolid()) {
//                        player.sendMessage("空中には設置できません");
//                        return;
//                    }
//
//                    boolean existsFrame = location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5).stream().anyMatch(
//                            e -> e instanceof GlowItemFrame
//                    );
//
//                    if (existsFrame) {
//                        player.sendMessage("すでに額縁があります");
//                        return;
//                    }
//                    ItemStack icon = new ItemStack(Material.PAPER);
//                    MakeItem.setItemMeta(icon,"",null,"cassette_workspace",ItemData.ITEM_ID,"CASSETTE_WORKSPACE");
//                    GlowItemFrame frame = (GlowItemFrame) location.getWorld().spawn(location, GlowItemFrame.class);
//                    frame.setFacingDirection(BlockFace.UP,true);
//                    frame.setItem(icon);
//                    frame.setFixed(true);
//                    frame.setInvulnerable(true);
//                }
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
