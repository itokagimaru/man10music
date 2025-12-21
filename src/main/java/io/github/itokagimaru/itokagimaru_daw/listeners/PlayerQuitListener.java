package io.github.itokagimaru.itokagimaru_daw.listeners;

import io.github.itokagimaru.itokagimaru_daw.manager.AutPlayManager;
import io.github.itokagimaru.itokagimaru_daw.manager.InventoryManager;
import io.github.itokagimaru.itokagimaru_daw.manager.PlayMusicManager;
import io.github.itokagimaru.itokagimaru_daw.task.PlayMusic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        AutPlayManager.set(player, false);
        PlayMusic play = PlayMusicManager.getMusic(player);
        if(play != null){
            play.stopTask(player);
        }

        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.loadInventory(player);
    }
}
