package io.github.itokagimaru.itokagimaru_daw.listeners;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.workspace.WorkspacesMenuHolder;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractEntityListner implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof GlowItemFrame itemFrame) {
            ItemStack item = itemFrame.getItem();
            if(!(ItemData.ITEM_ID.get(item).equals("CASSETTE_WORKSPACE")))return;
            WorkspacesMenuHolder workspacesMenuHolder = new WorkspacesMenuHolder();
            player.openInventory(workspacesMenuHolder.getInventory());
        }
    }
}
