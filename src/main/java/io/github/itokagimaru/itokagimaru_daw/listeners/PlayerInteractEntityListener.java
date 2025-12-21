package io.github.itokagimaru.itokagimaru_daw.listeners;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.radio.RadioPlayHolder;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.workspace.WorkspacesMenuHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.AutPlayManager;
import io.github.itokagimaru.itokagimaru_daw.manager.PlayMusicManager;
import io.github.itokagimaru.itokagimaru_daw.task.PlayMusic;
import io.github.itokagimaru.itokagimaru_daw.util.GetPresetItemStack;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerInteractEntityListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof GlowItemFrame itemFrame) {
            ItemStack item = itemFrame.getItem();
            if(ItemData.ITEM_ID.get(item).equals("CASSETTE_WORKSPACE")){
                if(player.isSneaking()){
                    UUID uuid = player.getUniqueId();
                    if (!String.valueOf(uuid).equals(ItemData.UUID.get(item)))return;
                    itemFrame.setItem(null);
                    itemFrame.remove();
                    player.give(GetPresetItemStack.workSpace());
                    return;
                }
                UUID uuid = itemFrame.getUniqueId();
                WorkspacesMenuHolder workspacesMenuHolder = new WorkspacesMenuHolder();
                workspacesMenuHolder.setUuid(uuid);
                player.openInventory(workspacesMenuHolder.getInventory());
            } else if (("CASSETTE_WORKSPACE_ITEM").equals(ItemData.ITEM_ID.get(item))) {
                if (itemFrame.getFacing() != BlockFace.UP) return;
                ItemStack icon = new ItemStack(Material.PAPER);
                MakeItem.setItemMeta(icon,"",null,"cassette_workspace",ItemData.ITEM_ID,"CASSETTE_WORKSPACE");
                ItemData.UUID.set(icon, String.valueOf(player.getUniqueId()));
                itemFrame.setItem(icon);
                itemFrame.setFixed(true);
                itemFrame.setInvulnerable(true);
            } else if (("RADIO").equals(ItemData.ITEM_ID.get(item))){
                UUID uuid = player.getUniqueId();
                if (!String.valueOf(uuid).equals(ItemData.UUID.get(item)))return;
                if(player.isSneaking()){
                    itemFrame.setItem(null);
                    itemFrame.remove();
                    PlayMusic play = PlayMusicManager.getMusic(itemFrame);
                    if (play != null){
                        AutPlayManager.set(itemFrame,false);
                        play.stopTask(itemFrame);
                    }
                    player.give(GetPresetItemStack.radio());
                    return;
                }
                RadioPlayHolder radioPlayHolder = new RadioPlayHolder(itemFrame);
                radioPlayHolder.setFream(itemFrame);
                player.openInventory(radioPlayHolder.getInventory());
            } else if (("RADIO_ITEM").equals(ItemData.ITEM_ID.get(item))) {
                if (itemFrame.getFacing() != BlockFace.UP) return;
                ItemStack icon = new ItemStack(Material.PAPER);
                MakeItem.setItemMeta(icon,"",null,"radio_cassette",ItemData.ITEM_ID,"RADIO");
                ItemData.UUID.set(icon, String.valueOf(player.getUniqueId()));
                itemFrame.setItem(icon);
                itemFrame.setFixed(true);
                itemFrame.setInvulnerable(true);
            }

        }
    }
}
