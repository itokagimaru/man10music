package io.github.itokagimaru.mun10music.task;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.gui.menu.daw.DawsPlayModeHolder;
import io.github.itokagimaru.mun10music.gui.menu.walkman.ItemsPlayModeHolder;
import io.github.itokagimaru.mun10music.manager.AutPlayManager;
import io.github.itokagimaru.mun10music.manager.ParticleManager;
import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.MusicManager;
import io.github.itokagimaru.mun10music.manager.music.PublishedMusicManager;
import io.github.itokagimaru.mun10music.manager.music.Track;
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

    public BaseGuiHolder getRequestHolder() {
        return requestHolder;
    }

    public void setRequestHolder(BaseGuiHolder holder) {
        // ホルダーを明示的に紐付ける
        requestHolder = holder;
    }

    public void playMusic(Entity target, int[] musicRed, int[] musicAqua, int[] musicGreen, int[] musicYellow, int bpm, float volume, Double soundRange) {
        this.volume = volume;
        this.soundRange = soundRange;
        long interval = (1200 / bpm);
        int maxLength = getMaxLength(musicRed, musicAqua, musicGreen, musicYellow);

        task = new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= maxLength) {
                    stopTask(target);
                    return;
                }

                playTrack(target, musicRed, count);
                playTrack(target, musicAqua, count);
                playTrack(target, musicGreen, count);
                playTrack(target, musicYellow, count);

                count++;
            }
        }.runTaskTimer(Man10Music.getInstance(), 0, interval);
    }

    public void playMusic(Entity target, Music music, float volume, Double soundRange) {
        if (music == null) return;
        playMusic(
                target,
                music.getMusic(Track.RED),
                music.getMusic(Track.AQUA),
                music.getMusic(Track.GREEN),
                music.getMusic(Track.YELLOW),
                music.getBpm(),
                volume,
                soundRange
        );
    }

    public void playMusic(Entity target, int musicID, float volume, Double soundRange) {
        MusicManager.loadMusicFromDb(Man10Music.getInstance().getMySQLManager(), musicID).thenAccept(music -> {
            if (music == null) return;
            playMusic(target, music, volume, soundRange);
        });
    }

    public void playPublishedMusic(Entity target, int musicID, float volume, Double soundRange) {
        PublishedMusicManager.loadPublishedByPublicId(Man10Music.getInstance().getMySQLManager(), musicID).thenAccept(music -> {
            if (music == null) return;
            playMusic(target, music, volume, soundRange);
        });
    }


    public ItemStack getCassetteIcon() {
        return cassetteIcon;
    }

    public void setCassetteIcon(ItemStack cassetteIcon) {
        this.cassetteIcon = cassetteIcon;
    }

    public void stopTask(Entity target) {
        task.cancel();
        PlayMusicManager.removeMusic(target);
        if (AutPlayManager.get(target)) {
            PlayMusic play = new PlayMusic();
            play.setPrivate(isPrivate);
            play.setRequester(requester);
            PlayMusicManager.setPlayingMusic(target, play);
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

    private void playTrack(Entity target, int[] music, int index) {
        if (music == null || index >= music.length) return;

        int note = music[index];
        if (note == -1) {
            return;
        }
        if (note != 0) {
            PlaySound.playNote(target, note, volume, soundRange, isPrivate);
            if (target instanceof Player player) {
                ParticleManager.playNote(player, isPrivate);
            }
        }
    }

    private int getMaxLength(int[] musicRed, int[] musicAqua, int[] musicGreen, int[] musicYellow) {
        int redLength = musicRed == null ? 0 : musicRed.length;
        int aquaLength = musicAqua == null ? 0 : musicAqua.length;
        int greenLength = musicGreen == null ? 0 : musicGreen.length;
        int yellowLength = musicYellow == null ? 0 : musicYellow.length;
        return Math.max(Math.max(redLength, aquaLength), Math.max(greenLength, yellowLength));
    }
}
