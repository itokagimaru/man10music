package io.github.itokagimaru.mun10music.gui.menu.radio;

import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.walkman.ItemsPlayModeHolder;
import io.github.itokagimaru.mun10music.manager.AutPlayManager;
import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.task.PlayMusic;
import io.github.itokagimaru.mun10music.util.MakeItem;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public RadioPlayHolder(GlowItemFrame target) {
        super(target);
        setSettingItem();
    }

    public void setSettingItem(){
        ItemStack setting = new ItemStack(Material.FLOWER_BANNER_PATTERN);
        MakeItem.setItemMetaByColor(setting, "Setting", NamedTextColor.YELLOW, 0, ItemData.BUTTON_ID, "setting");
        inv.setItem(0, setting);
    }
    @Override
    public void onClick(InventoryClickEvent event){
        ItemStack clicked = event.getCurrentItem();
        String buttonId = ItemData.BUTTON_ID.get(clicked);
        if (("setting").equals(buttonId)){
            Player player = (Player) event.getWhoClicked();
            closeFlag = false;
            RadiosSettingHolder radiosSettingHolder = new RadiosSettingHolder();
            radiosSettingHolder.setUuid(fream.getUniqueId());
            player.openInventory(radiosSettingHolder.getInventory());
        }
        super.onClick(event);
        setSettingItem();
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
        play.playMusic(fream,clicked_inv.getItem(7), musicData().getAutoPlayVolume(), musicData().getSoundRange());
    }

    @Override
    public void stopMusic(InventoryClickEvent event){
        ItemStack clickedItem = event.getCurrentItem();
        PlayMusic play = PlayMusicManager.getMusic(fream);
        MakeItem.setItemMeta(clickedItem, "再生", null, icons().getTriangleRight().getCmd(), ItemData.BUTTON_ID, "PLAY");
        AutPlayManager.set(fream,false);
        inv.setItem(0,upDateAutoPlayIcon(false));
        play.stopTask(fream);
    }

    @Override
    public ItemStack upDateAutoPlayIcon(boolean flag){
        return new ItemStack(Material.AIR);
    }

    private Icons icons() {
        return iconsData();
    }

}
