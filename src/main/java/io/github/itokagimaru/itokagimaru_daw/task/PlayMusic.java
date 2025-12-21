package io.github.itokagimaru.itokagimaru_daw.task;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.walkman.ItemsPlayModeHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.AutPlayManager;
import io.github.itokagimaru.itokagimaru_daw.manager.ParticleManager;
import io.github.itokagimaru.itokagimaru_daw.manager.PlayMusicManager;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import io.github.itokagimaru.itokagimaru_daw.util.PlaySound;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public class PlayMusic {
    //HashMap<UUID, BukkitTask> tasks = new HashMap<>();
    BukkitTask task;
    ItemStack cassetteIcon;
    boolean isPrivate = true;
    Player requester;

    public void setPrivate(boolean bool){
        isPrivate = bool;
    }

    public void setRequester(Player player){
        requester = player;
    }

    public void playMusic(Entity target, ItemStack pdcHolder) {
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
                    float volume;
                    if (AutPlayManager.get(target)) {
                        volume =(float) 0.125;
                    } else {
                        volume =(float) 0.25;
                    }
                    PlaySound.playNote(target, loadedMusic[count], volume, isPrivate);
                    if(target instanceof Player player){
                        ParticleManager.playNote(player, isPrivate);
                    }
                }
                count++;
            }
        }.runTaskTimer(Itokagimaru_daw.getInstance(), 0, interval);
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
            play.playMusic(target,cassetteIcon);
        } else if (requester.getOpenInventory().getTopInventory().getHolder() instanceof ItemsPlayModeHolder holder) {
            ItemStack play = new ItemStack(Material.PAPER);
            MakeItem.setItemMeta(play, "再生", null, "next_b_right", ItemData.BUTTON_ID, "PLAY");
            holder.getInventory().setItem(4, play);
        }
    }
}
