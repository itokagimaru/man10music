package io.github.itokagimaru.mun10music.gui.listener;

import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.gui.menu.daw.AnvilGUIOpening;
import io.github.itokagimaru.mun10music.gui.menu.daw.NamingAnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.AnvilView;

public class CloseInventoryListeners implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // is player
        if (!(event.getPlayer() instanceof Player player)) return;

        // get inventory and check
        if(event.getView() instanceof AnvilView){
            if (!(AnvilGUIOpening.isOpening(player))){
                return;
            }
            NamingAnvilGUI namingAnvilGUI = AnvilGUIOpening.anvilOpening.get(player.getUniqueId());
            namingAnvilGUI.onClose(player);
            return;
        }
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof BaseGuiHolder guiHolder)) return;
        // call onClose
        guiHolder.onClose(player);
    }
}
