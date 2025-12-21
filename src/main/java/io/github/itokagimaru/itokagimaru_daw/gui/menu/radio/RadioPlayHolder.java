package io.github.itokagimaru.itokagimaru_daw.gui.menu.radio;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.walkman.ItemsPlayModeHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.AutPlayManager;
import io.github.itokagimaru.itokagimaru_daw.manager.PlayMusicManager;
import io.github.itokagimaru.itokagimaru_daw.task.PlayMusic;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class RadioPlayHolder extends ItemsPlayModeHolder {
    Entity fream;
    public void setFream(Entity glowFream){
        if(!(glowFream instanceof GlowItemFrame))return;
        fream = glowFream;
    }
    public RadioPlayHolder(Entity target) {
        super(target);
    }

    @Override
    public void playMusic(InventoryClickEvent event){
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        Inventory clicked_inv = event.getClickedInventory();
        double bpm = ItemData.BPM.get(Objects.requireNonNull(clicked_inv.getItem(7)));
        if (bpm == -1) return;
        MakeItem.setItemMeta(clickedItem, "再生停止", null, "elytra", ItemData.BUTTON_ID, "STOP");
        PlayMusic play = new PlayMusic();
        play.setPrivate(false);
        play.setRequester(player);
        AutPlayManager.set(fream,true);
        PlayMusicManager.setPlayingMusic(fream, play);
        play.playMusic(fream,clicked_inv.getItem(7));
    }

    @Override
    public void stopMusic(InventoryClickEvent event){
        ItemStack clickedItem = event.getCurrentItem();
        PlayMusic play = PlayMusicManager.getMusic(fream);
        MakeItem.setItemMeta(clickedItem, "再生", null, "next_b_right", ItemData.BUTTON_ID, "PLAY");
        AutPlayManager.set(fream,false);
        inv.setItem(0,upDateAutoPlayIcon(false));
        play.stopTask(fream);
    }

    @Override
    public ItemStack upDateAutoPlayIcon(boolean flag){
        return new ItemStack(Material.AIR);
    }

}
