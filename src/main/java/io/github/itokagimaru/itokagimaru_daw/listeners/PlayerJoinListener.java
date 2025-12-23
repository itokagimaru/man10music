package io.github.itokagimaru.itokagimaru_daw.listeners;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.manager.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Inventory playerInv = p.getInventory();
        ItemStack flager = playerInv.getItem(9);
        if (flager == null)return;
        if (("flager").equals(ItemData.BUTTON_ID.get(flager))){
            InventoryManager inventoryManager = Itokagimaru_daw.getInstance().inventoryManager;
            inventoryManager.loadFromDB(p);
        }
    }
}
