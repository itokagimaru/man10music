package io.github.itokagimaru.itokagimaru_daw.gui.menu.walkman;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.config.Icons;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.AutPlayManager;
import io.github.itokagimaru.itokagimaru_daw.manager.PlayMusicManager;
import io.github.itokagimaru.itokagimaru_daw.task.PlayMusic;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class ItemsPlayModeHolder extends BaseGuiHolder {
    public ItemsPlayModeHolder(Entity target) {
        inv = Bukkit.createInventory(this, 9, Component.text("PlayMode"));
        setup(target);
    }

    public void setup(Entity target) {
        Icons icons = iconsData();
        ItemStack playIcon = new ItemStack(icons.getBaseMaterial());
        PlayMusic play = PlayMusicManager.getMusic(target);
        if (play == null) {
            MakeItem.setItemMeta(playIcon, "再生", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "PLAY");
            ItemStack cassetteIcon = new ItemStack(icons.getBaseMaterial());
            MakeItem.setItemMeta(cassetteIcon, "未選択", null, "barrier" , null, null);
            inv.setItem(7, cassetteIcon);
        } else {
            MakeItem.setItemMeta(playIcon, "再生停止", null, "elytra", ItemData.BUTTON_ID, "STOP");
            setRecordIcons(play.getCassetteIcon());
        }
        inv.setItem(4, playIcon);

        ItemStack autPlayIcon = upDateAutoPlayIcon(AutPlayManager.get(target));
        inv.setItem(0, autPlayIcon);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (Objects.equals(ItemData.ITEM_ID.get(clickedItem), "recordCassette")) {
            setRecordIcons(clickedItem);
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clickedItem), "RECORD BUTTON")) {
            removeIcon();
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clickedItem), "PLAY")) {
            playMusic(event);
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clickedItem), "STOP")) {
            stopMusic(event);
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clickedItem), "AUTPLAY_ICON")) {
            AutPlayManager.set(player,!(AutPlayManager.get(player)));
            ItemStack autPlayIcon = upDateAutoPlayIcon(AutPlayManager.get(player));
            inv.setItem(0, autPlayIcon);
        }
    }

    public void removeIcon(){
        ItemStack bar = new ItemStack(icons().getBaseMaterial());
        MakeItem.setItemMeta(bar, "未選択", null, "barrier", null, null);
        inv.setItem(7, bar);
        inv.setItem(1, null);
    }

    public void playMusic(InventoryClickEvent event){
        ItemStack clickedItem = event.getCurrentItem();
        Inventory clicked_inv = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        double bpm = ItemData.BPM.get(Objects.requireNonNull(clicked_inv.getItem(7)));
        if (bpm == -1) return;
        MakeItem.setItemMeta(clickedItem, "再生停止", null, "elytra", ItemData.BUTTON_ID, "STOP");
        PlayMusic play = new PlayMusic();
        play.setPrivate(false);
        play.setRequester(player);
        PlayMusicManager.setPlayingMusic(player, play);
        play.playMusic(player,clicked_inv.getItem(7), musicData().getAutoPlayVolume(), musicData().getSoundRange());
    }

    public void stopMusic(InventoryClickEvent event){
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        PlayMusic play = PlayMusicManager.getMusic(player);
        MakeItem.setItemMeta(clickedItem, "再生", null, icons().getTriangleRight().getCmd(), ItemData.BUTTON_ID, "PLAY");
        AutPlayManager.set(player,false);
        inv.setItem(0,upDateAutoPlayIcon(false));
        play.stopTask(player);
    }

    public void setRecordIcons(ItemStack item){
        ItemStack recordButton = item.clone();
        ItemData.ITEM_ID.set(recordButton,"");
        ItemData.BUTTON_ID.set(recordButton, "RECORD BUTTON");
        inv.setItem(7, recordButton);
        ItemStack clock = new ItemStack(Material.CLOCK);
        int bpm = ItemData.BPM.get(item);
        MakeItem.setItemMeta(clock, "BPM", null, null, null, null);
        ItemMeta meta = clock.getItemMeta();
        meta.lore(List.of(Component.text("現在のBPM設定:" + bpm)));
        clock.setItemMeta(meta);
        inv.setItem(1, clock);
    }
    public ItemStack upDateAutoPlayIcon(boolean flag){
        ItemStack autPlayIcon;
        if (flag) {
            autPlayIcon = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            // TODO: 自動再生アイコンはIcons未定義のため、現状のMaterialを維持する。
            MakeItem.setItemMetaByColor(autPlayIcon,"自動再生:ON", NamedTextColor.RED,0,ItemData.BUTTON_ID, "AUTPLAY_ICON");
        } else {
            autPlayIcon = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            // TODO: 自動再生アイコンはIcons未定義のため、現状のMaterialを維持する。
            MakeItem.setItemMetaByColor(autPlayIcon,"自動再生:OFF", NamedTextColor.WHITE,0,ItemData.BUTTON_ID, "AUTPLAY_ICON");
        }
        return autPlayIcon;
    }

    private Icons icons() {
        return iconsData();
    }

    @Override
    public void onClose(Player player) {}
}
