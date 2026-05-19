package io.github.itokagimaru.mun10music.gui.menu.walkman;

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
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ItemsPlayModeHolder extends BasePlayMusicHolder {
    private final Player player;

    public ItemsPlayModeHolder(Player requester, Entity performer) {
        super(null);
        this.player = requester;
        setPerformer(performer);
        setup(performer);
        isPrivate = false;
    }

    public void setup(Entity target) {
        PlayMusic play = PlayMusicManager.getMusic(target);
        if (play != null) {
            if (play.getRequestHolder() instanceof BasePlayMusicHolder holder) {
                holder.requestStopFromExternal();
            }
            play.setRequestHolder(this);
            setRecordIcons(play.getCassetteIcon());
            setStopIcon();
        }
        ItemStack autPlayIcon = upDateAutoPlayIcon(AutPlayManager.get(target));
        inv.setItem(0, autPlayIcon);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (clickedItem == null) return;

        String buttonId = ItemData.BUTTON_ID.get(clickedItem);
        if ("STOP".equals(buttonId)) {
            // STOP時は自動再生をOFFにして再起動を抑止
            AutPlayManager.set(getPerformingTarget(player), false);
            inv.setItem(0, upDateAutoPlayIcon(false));
            super.onClick(event);
            return;
        }

        super.onClick(event);
        if (Objects.equals(ItemData.ITEM_ID.get(clickedItem), "recordCassette") || Objects.equals(ItemData.ITEM_ID.get(clickedItem), "mergedCassette")) {
            setRecordIcons(clickedItem);
            setMusics();
        } else if (Objects.equals(buttonId, "RECORD BUTTON")) {
            removeIcon();
        } else if (Objects.equals(buttonId, "AUTPLAY_ICON")) {
            Entity target = getPerformingTarget(player);
            boolean newFlag = !AutPlayManager.get(target);
            AutPlayManager.set(target, newFlag);
            ItemStack autPlayIcon = upDateAutoPlayIcon(newFlag);
            inv.setItem(0, autPlayIcon);

            if (!newFlag) {
                // 自動再生OFF時は次曲開始の監視を止める
                stopSequenceMonitor();
            }
        }
    }

    public void removeIcon(){
        ItemStack bar = new ItemStack(icons().getBaseMaterial());
        MakeItem.setItemMeta(bar, "未選択", null, "barrier", null, null);
        inv.setItem(7, bar);
        inv.setItem(1, null);
    }

    public void setRecordIcons(ItemStack item){
        ItemStack recordButton = item.clone();
        ItemData.ITEM_ID.set(recordButton,"");
        ItemData.BUTTON_ID.set(recordButton, "RECORD BUTTON");
        inv.setItem(7, recordButton);
    }
    public ItemStack upDateAutoPlayIcon(boolean flag){
        ItemStack autPlayIcon;
        if (flag) {
            autPlayIcon = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            MakeItem.setItemMetaByColor(autPlayIcon,"自動再生:ON", NamedTextColor.RED,0,ItemData.BUTTON_ID, "AUTPLAY_ICON");
        } else {
            autPlayIcon = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            MakeItem.setItemMetaByColor(autPlayIcon,"自動再生:OFF", NamedTextColor.WHITE,0,ItemData.BUTTON_ID, "AUTPLAY_ICON");
        }
        return autPlayIcon;
    }

    @Override
    protected void onSequenceFinished(Player player) {
        Entity target = getPerformingTarget(player);
        if (musics != null && !musics.isEmpty() && AutPlayManager.get(target)) {
            currentIndex = 0;
            startSequence(player);
            return;
        }
        super.onSequenceFinished(player);
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

    private void setMusics() {
        ItemStack selected = inv.getItem(7);
        if (selected == null) {
            musics = Collections.emptyList();
            return;
        }

        getMusics(ItemData.PUBLISHED_MUSIC_IDS.get(selected)).thenAccept(loaded -> {
            // メインスレッドで状態更新
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> musics = loaded);
        });
    }

    @Override
    public void onClose(Player player) {}

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
}
