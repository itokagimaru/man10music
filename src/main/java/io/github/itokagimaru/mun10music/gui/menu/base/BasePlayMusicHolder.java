package io.github.itokagimaru.mun10music.gui.menu.base;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.MusicManager;
import io.github.itokagimaru.mun10music.manager.music.PublishedMusicManager;
import io.github.itokagimaru.mun10music.task.PlayMusic;
import io.github.itokagimaru.mun10music.util.MakeItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public abstract class BasePlayMusicHolder extends BaseGuiHolder {
    protected List<Music> musics;
    protected int currentIndex = 0;
    protected Entity performer;
    private boolean stopRequested = false;
    private BukkitTask sequenceTask;
    private PlayMusic currentPlay;
    protected boolean isPrivate;

    public BasePlayMusicHolder(List<Music> musics) {
        this.musics = musics;
        inv = Bukkit.createInventory(this, 9, Component.text("PlayMode"));
        baseSetup();
    }



    private void baseSetup() {
        Icons icons = iconsData();
        ItemStack playIcon = new ItemStack(icons.getTriangleRight().getMaterial());
        MakeItem.setItemMetaByColor(playIcon, "再生", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "PLAY");
        inv.setItem(4, playIcon);
    }


    protected void setPerformer(Entity performer) {
        this.performer = performer;
    }

    protected Entity getPerformingTarget(Player requester) {
        return performer != null ? performer : requester;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        Entity target = getPerformingTarget(player);
        switch (ItemData.BUTTON_ID.get(item)) {
            case "PLAY" -> {
                player.sendMessage(Component.text("再生を開始します/ "));
                if (musics == null || musics.isEmpty()) return;
                startSequence(player);
            }
            case "STOP" -> {
                stopRequested = true;
                stopSequenceMonitor();
                PlayMusic play = PlayMusicManager.getMusic(target);

                setPlayIcon();

                if (play != null) {
                    play.stopTask(target);
                }
            }
        }
    }

    protected void startSequence(Player player) {
        stopRequested = false;
        currentIndex = 0;
        setStopIcon();
        startTrack(player);
        startSequenceMonitor(player);
    }

    protected void startTrack(Player player) {
        if (currentIndex >= musics.size()) {
            onSequenceFinished(player);
            return;
        }
        Entity target = getPerformingTarget(player);
        currentPlay = new PlayMusic();
        currentPlay.setPrivate(isPrivate);
        currentPlay.setRequester(player);
        onCreatePlay(player, currentPlay);
        PlayMusicManager.setPlayingMusic(target, currentPlay);
        currentPlay.playMusic(target, musics.get(currentIndex).getMusic(), musics.get(currentIndex).getBpm(), musicData().getDefaultVolume(), musicData().getSoundRange());
    }

    protected void onCreatePlay(Player player, PlayMusic play) {
        // 追加設定が必要な場合は子クラスで実装
    }

    private void startSequenceMonitor(Player player) {
        stopSequenceMonitor();
        Entity target = getPerformingTarget(player);
        sequenceTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (stopRequested) {
                    cancel();
                    return;
                }
                PlayMusic playing = PlayMusicManager.getMusic(target);
                // 再生終了で管理対象が変わったら次曲へ進める
                if (playing == null || playing != currentPlay) {
                    currentIndex++;
                    if (currentIndex >= musics.size()) {
                        onSequenceFinished(player);
                        return;
                    }
                    startTrack(player);
                }
            }
        }.runTaskTimer(Man10Music.getInstance(), 1, 1);
    }

    protected void onSequenceFinished(Player player) {
        stopSequenceMonitor();
        setPlayIcon();
    }

    public void requestStopFromExternal() {
        // 外部停止時に監視とUIを止める
        stopRequested = true;
        stopSequenceMonitor();
        setPlayIcon();
    }

    protected void stopSequenceMonitor() {
        if (sequenceTask != null) {
            sequenceTask.cancel();
            sequenceTask = null;
        }
    }

    protected void setStopIcon() {
        ItemStack stopIcon = new ItemStack(Material.ELYTRA);
        MakeItem.setItemMeta(stopIcon, "再生停止", null, 0, ItemData.BUTTON_ID, "STOP");
        inv.setItem(4, stopIcon);
    }

    protected void setPlayIcon() {
        ItemStack playIcon = new ItemStack(iconsData().getTriangleRight().getMaterial());
        MakeItem.setItemMeta(playIcon, "再生", null, iconsData().getTriangleRight().getCmd(), ItemData.BUTTON_ID, "PLAY");
        inv.setItem(4, playIcon);
    }

    @Override
    public void onClose(Player player) {

    }
}
