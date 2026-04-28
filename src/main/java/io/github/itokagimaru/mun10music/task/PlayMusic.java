package io.github.itokagimaru.mun10music.task;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.mun10music.gui.menu.daw.DawsPlayModeHolder;
import io.github.itokagimaru.mun10music.gui.menu.walkman.ItemsPlayModeHolder;
import io.github.itokagimaru.mun10music.manager.AutPlayManager;
import io.github.itokagimaru.mun10music.manager.ParticleManager;
import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.util.MakeItem;
import io.github.itokagimaru.mun10music.util.PlaySound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public class PlayMusic {
    BukkitTask task;
    ItemStack cassetteIcon;
    boolean isPrivate = true;
    Player requester;
    BaseGuiHolder requestHolder;
    float volume;
    Double soundRange;

    public void setPrivate(boolean bool){
        isPrivate = bool;
    }

    public void setRequester(Player player){
        requester = player;
        if (player.getInventory().getHolder() instanceof ItemsPlayModeHolder holder){
            requestHolder = holder;
        }else if (player.getOpenInventory().getTopInventory().getHolder() instanceof DawsPlayModeHolder holder){
            requestHolder = holder;
        }
    }

    public void playMusic(Entity target, ItemStack pdcHolder, float volume, Double soundRange) {
        this.volume = volume;
        this.soundRange = soundRange;
        cassetteIcon = pdcHolder.clone();
        int[] loadedMusic = ItemData.MUSIC_SAVED_RED.get(pdcHolder);
        int bpm = ItemData.BPM.get(pdcHolder);
        long interval = (1200 / bpm);
        task = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (loadedMusic[count] == -1) {
                    stopTask(target);
                } else if (loadedMusic[count] != 0) {
                    PlaySound.playNote(target, loadedMusic[count], volume, soundRange, isPrivate);
                    if(target instanceof Player player){
                        ParticleManager.playNote(player, isPrivate);
                    }
                }
                count++;
            }
        }.runTaskTimer(Man10Music.getInstance(), 0, interval);
    }
    public ItemStack getCassetteIcon() {
        return cassetteIcon;
    }
    public void stopTask(Entity target) {
        task.cancel();
        PlayMusicManager.removeMusic(target);
        if (AutPlayManager.get(target)) {
            PlayMusic play = new PlayMusic();
            play.setPrivate(isPrivate);
            play.setRequester(requester);
            PlayMusicManager.setPlayingMusic(target, play);
            play.playMusic(target,cassetteIcon, volume, soundRange);
        } else if (requester.getOpenInventory().getTopInventory().getHolder() == requestHolder) {
            Icons icons = Man10Music.getInstance().getPluginConfigData().getIcons();
            ItemStack play = new ItemStack(icons.getTriangleRight().getMaterial());
            MakeItem.setItemMeta(play, "再生", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "PLAY");
            requestHolder.getInventory().setItem(4, play);
        }
    }

    public void stopTask(){
        task.cancel();
    }
}
