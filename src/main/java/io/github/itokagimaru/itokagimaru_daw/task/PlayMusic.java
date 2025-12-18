package io.github.itokagimaru.itokagimaru_daw.task;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.walkman.ItemsPlayModeHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.AutPlayManager;
import io.github.itokagimaru.itokagimaru_daw.manager.ParticleManager;
import io.github.itokagimaru.itokagimaru_daw.manager.PlayerMusicManager;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import io.github.itokagimaru.itokagimaru_daw.util.PlaySound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public class PlayMusic {
    //HashMap<UUID, BukkitTask> tasks = new HashMap<>();
    BukkitTask task;
    ItemStack cassetteIcon;
    boolean isPrivate;
    public void setPrivate(boolean bool){
        isPrivate = bool;
    }
    public void playMusic(Player player,ItemStack pdcHolder) {
        cassetteIcon = pdcHolder.clone();
        int[] lodedMusic = ItemData.MUSIC_SAVED_RED.get(pdcHolder);
        int bpm = ItemData.BPM.get(pdcHolder);
        long interval = (1200 / bpm);
        task = new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (lodedMusic[count] == -1) {
                    stopTask(player);
                } else if (lodedMusic[count] != 0) {
                    float volume;
                    if (AutPlayManager.get(player)) {
                        volume =(float) 0.125;
                    } else {
                        volume =(float) 0.25;
                    }
                    PlaySound.playNote(player, lodedMusic[count], volume, isPrivate);
                    ParticleManager.playNote(player);
                }
                count++;
            }
        }.runTaskTimer(Itokagimaru_daw.getInstance(), 0, interval);
    }
    public ItemStack getCassetteIcon() {
        return cassetteIcon;
    }
    public void stopTask(Player player) {
        task.cancel();
        PlayerMusicManager.removeMusic(player);
        if (AutPlayManager.get(player)) {
            PlayMusic play = new PlayMusic();
            PlayerMusicManager.setPlayingMusic(player, play);
            play.playMusic(player,cassetteIcon);
        } else if (player.getOpenInventory().getTopInventory().getHolder() instanceof BaseGuiHolder holder) {
            ItemStack play = new ItemStack(Material.PAPER);
            MakeItem.setItemMeta(play, "再生", null, "next_b_right", ItemData.BUTTON_ID, "PLAY");
            holder.getInventory().setItem(4, play);
        }
    }
}
