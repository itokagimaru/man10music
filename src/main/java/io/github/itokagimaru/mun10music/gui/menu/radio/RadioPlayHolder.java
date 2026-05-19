package io.github.itokagimaru.mun10music.gui.menu.radio;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BasePlayMusicHolder;
import io.github.itokagimaru.mun10music.manager.AutPlayManager;
import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.PublishedMusicManager;
import io.github.itokagimaru.mun10music.task.PlayMusic;
import io.github.itokagimaru.mun10music.util.MakeItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class RadioPlayHolder extends BasePlayMusicHolder {
    private final ArmorStand stand;

    public RadioPlayHolder(Player requester, ArmorStand stand) {
        super(null);
        this.stand = stand;
        if (stand != null) {
            setPerformer(stand);
        }
        setup();
        isPrivate = false;
    }

    private void setup() {
        if (stand == null) return;
        PlayMusic play = PlayMusicManager.getMusic(stand);
        if (play != null) {
            if (play.getRequestHolder() instanceof BasePlayMusicHolder holder) {
                holder.requestStopFromExternal();
            }
            play.setRequestHolder(this);
            ItemStack cassetteIcon = play.getCassetteIcon();
            if (cassetteIcon == null) {
                removeIcon();
            } else {
                setRecordIcons(cassetteIcon);
                setMusics(cassetteIcon.clone());
            }
            setStopIcon();
        }
    }
    @Override
    public void onClick(InventoryClickEvent event){
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        Player player = (Player) event.getWhoClicked();

        if (stand == null) {
            player.sendMessage("GUIをもう一度開きなおしてください");
            return;
        }

        if ("PLAY".equals(buttonId)) {
            ItemStack selected = inv.getItem(7);
            if (selected == null) {
                player.sendMessage("カセットが選択されていません");
                return;
            }
            // 再生直前に最新の曲リストを再ロード
            setMusics(selected.clone());
            if (musics == null || musics.isEmpty()) {
                player.sendMessage("読み込み中です。少し待ってから再生してください");
                return;
            }
            AutPlayManager.set(stand, true);
            setPerformer(stand);
            super.onClick(event);
            return;
        }
        if ("STOP".equals(buttonId)) {
            AutPlayManager.set(stand, false);
            super.onClick(event);
            return;
        }
        super.onClick(event);
        if (Objects.equals(ItemData.ITEM_ID.get(clicked), "recordCassette") || Objects.equals(ItemData.ITEM_ID.get(clicked), "mergedCassette")) {
            setRecordIcons(clicked);
            setMusics(clicked.clone());
        } else if (Objects.equals(buttonId, "RECORD BUTTON")) {
            removeIcon();
        }
    }

    public void removeIcon(){
        ItemStack bar = new ItemStack(icons().getBaseMaterial());
        MakeItem.setItemMeta(bar, "未選択", null, "barrier", null, null);
        inv.setItem(7, bar);
        inv.setItem(1, null);
    }

    public void setRecordIcons(ItemStack item){
        if (item == null) {
            removeIcon();
            return;
        }
        ItemStack recordButton = item.clone();
        ItemData.ITEM_ID.set(recordButton,"");
        ItemData.BUTTON_ID.set(recordButton, "RECORD BUTTON");
        inv.setItem(7, recordButton);
    }

    private void setMusics(ItemStack selected) {
        if (selected == null) {
            musics = Collections.emptyList();
            return;
        }

        getMusics(ItemData.PUBLISHED_MUSIC_IDS.get(selected)).thenAccept(loaded -> {
            // メインスレッドで状態更新
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> musics = loaded);
        });
    }

    private CompletableFuture<List<Music>> getMusics(int[] publishedIDList) {
        if (publishedIDList == null || publishedIDList.length == 0) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<CompletableFuture<Music>> futures = new ArrayList<>();
        for (int publicId : publishedIDList) {
            if (publicId <= 0) continue;
            futures.add(PublishedMusicManager.loadPublishedByPublicId(
                    Man10Music.getInstance().getMySQLManager(), publicId));
        }
        if (futures.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<Music> loaded = new ArrayList<>();
                    for (CompletableFuture<Music> future : futures) {
                        Music music = future.join();
                        if (music != null) loaded.add(music);
                    }
                    return loaded;
                });
    }

    @Override
    protected void onCreatePlay(Player player, PlayMusic play) {
        ItemStack cassetteIcon = inv.getItem(7);
        if (cassetteIcon == null) return;
        play.setCassetteIcon(cassetteIcon.clone());
    }

    private Icons icons() {
        return iconsData();
    }

    @Override
    protected void onSequenceFinished(Player player) {
        if (musics != null && !musics.isEmpty() && AutPlayManager.get(stand)) {
            currentIndex = 0;
            startSequence(player);
            return;
        }
        super.onSequenceFinished(player);
    }

}
