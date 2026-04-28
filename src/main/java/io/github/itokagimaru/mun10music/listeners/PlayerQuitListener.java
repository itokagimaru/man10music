package io.github.itokagimaru.mun10music.listeners;

import io.github.itokagimaru.mun10music.manager.AutPlayManager;
import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.task.PlayMusic;
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
    }
}
