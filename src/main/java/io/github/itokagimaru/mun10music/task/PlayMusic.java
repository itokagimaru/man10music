package io.github.itokagimaru.mun10music.task;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseGuiHolder;
import io.github.itokagimaru.mun10music.gui.menu.base.BasePlayMusicHolder;
import io.github.itokagimaru.mun10music.gui.menu.daw.DawsPlayModeHolder;
import io.github.itokagimaru.mun10music.gui.menu.daw.InputModeHolder;
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
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.EnumSet;


public class PlayMusic {
    BukkitTask task;
    ItemStack cassetteIcon;
    boolean isPrivate = true;
    Player requester;
    BasePlayMusicHolder requestHolder;
    float volume;
    Double soundRange;

    public void setPrivate(boolean bool, World world){
        if (Man10Music.getInstance().getPluginConfigData().getMusic().getCantPlayPublicWorlds().contains(world.getName())){
            isPrivate = false;
            return;
        }
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

    public BasePlayMusicHolder getRequestHolder() {
        return requestHolder;
    }

    public void setRequestHolder(BasePlayMusicHolder holder) {
        // ホルダーを明示的に紐付ける
        requestHolder = holder;
    }

    public void playMusic(Entity target, int[] musicRed, int[] musicAqua, int[] musicGreen, int[] musicYellow, int bpm, float volume, Double soundRange) {
        playMusic(target, musicRed, musicAqua, musicGreen, musicYellow, bpm, volume, soundRange, 0, EnumSet.allOf(Track.class));
    }

    public void playMusic(Entity target,
                          int[] musicRed,
                          int[] musicAqua,
                          int[] musicGreen,
                          int[] musicYellow,
                          int bpm,
                          float volume,
                          Double soundRange,
                          int startIndex,
                          EnumSet<Track> trackSet) {
        this.volume = volume;
        this.soundRange = soundRange;
        if (trackSet == null || trackSet.isEmpty()) {
            return;
        }

        long interval = (1200 / bpm);
        int[] red = trackSet.contains(Track.RED) ? musicRed : null;
        int[] aqua = trackSet.contains(Track.AQUA) ? musicAqua : null;
        int[] green = trackSet.contains(Track.GREEN) ? musicGreen : null;
        int[] yellow = trackSet.contains(Track.YELLOW) ? musicYellow : null;
        int maxLength = getMaxLength(red, aqua, green, yellow);
        int safeStart = Math.max(0, startIndex);
        if (safeStart >= maxLength) {
            return;
        }

        task = new BukkitRunnable() {
            int count = safeStart;

            @Override
            public void run() {
                if (count >= maxLength) {
                    stopTask(target);
                    return;
                }

                playTrack(target, red, count);
                playTrack(target, aqua, count);
                playTrack(target, green, count);
                playTrack(target, yellow, count);
                InventoryHolder holder = requester.getOpenInventory().getTopInventory().getHolder();
                if(holder instanceof BaseGuiHolder baseGuiHolder){
                    baseGuiHolder.onPlayNote(count, requester);
                }

                count++;
            }
        }.runTaskTimer(Man10Music.getInstance(), 0, interval);
    }

    public void playMusic(Entity target, Music music, float volume, Double soundRange) {
        Entity targetEntity = Bukkit.getEntity(target.getUniqueId());
        if (targetEntity == null) return;
        if (music == null) return;
        playMusic(target, music, 0, EnumSet.allOf(Track.class), volume, soundRange);
    }

    public void playMusic(Entity target, Music music, int startIndex, EnumSet<Track> trackSet, float volume, Double soundRange) {
        if (music == null) return;
        playMusic(
                target,
                music.getMusic(Track.RED),
                music.getMusic(Track.AQUA),
                music.getMusic(Track.GREEN),
                music.getMusic(Track.YELLOW),
                music.getBpm(),
                volume,
                soundRange,
                startIndex,
                trackSet
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
        if (task != null) {
            task.cancel();
        }
        PlayMusicManager.removeMusic(target);
        if (AutPlayManager.get(target)) {
            PlayMusic play = new PlayMusic();
            play.setPrivate(isPrivate, target.getWorld());
            play.setRequester(requester);
            PlayMusicManager.setPlayingMusic(target, play);
        } else if (requester.getOpenInventory().getTopInventory().getHolder() == requestHolder) {
            Icons icons = Man10Music.getInstance().getPluginConfigData().getIcons();
            ItemStack play = new ItemStack(icons.getTriangleRight().getMaterial());
            MakeItem.setItemMeta(play, "再生", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "PLAY");
            requestHolder.getInventory().setItem(4, play);
        } else if (requester.getOpenInventory().getTopInventory().getHolder() instanceof InputModeHolder holder) {
            holder.onStopMusic(requester, 0, 0);
        }
        if (target instanceof Player player) {
            player.sendMessage("再生を停止しました");
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
        int redLength = musicRed == null ? 0 : getLen(musicRed);
        int aquaLength = musicAqua == null ? 0 : getLen(musicAqua);
        int greenLength = musicGreen == null ? 0 : getLen(musicGreen);
        int yellowLength = musicYellow == null ? 0 : getLen(musicYellow);
        return Math.max(Math.max(redLength, aquaLength), Math.max(greenLength, yellowLength));
    }

    int getLen(int[] music){
        int len = 0;
        for (int note: music){
            if (note == -1) return len;
            len++;
        }
        return len;
    }
}
