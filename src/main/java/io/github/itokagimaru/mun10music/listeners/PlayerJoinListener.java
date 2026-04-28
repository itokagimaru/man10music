package io.github.itokagimaru.mun10music.listeners;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.manager.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Inventory playerInv = p.getInventory();
        ItemStack flager = playerInv.getItem(9);
        if (flager == null)return;
        if (("flager").equals(ItemData.BUTTON_ID.get(flager))){
            InventoryManager inventoryManager = Man10Music.getInstance().inventoryManager;
            inventoryManager.loadFromDB(p);
        }
    }
}
