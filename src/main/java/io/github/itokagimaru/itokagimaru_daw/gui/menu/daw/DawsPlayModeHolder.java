package io.github.itokagimaru.itokagimaru_daw.gui.menu.daw;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.itokagimaru_daw.manager.PlayMusicManager;
import io.github.itokagimaru.itokagimaru_daw.task.PlayMusic;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class DawsPlayModeHolder extends BaseGuiHolder {
    public DawsPlayModeHolder(int bpm) {
        inv = Bukkit.createInventory(this, 9, Component.text("PlayMode"));
        setup(bpm);
    }

    public void setup(int bpm) {
        ItemStack clock = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(clock, "現在のBPM:" + bpm, NamedTextColor.YELLOW, "clock", ItemData.BPM, bpm);
        ItemData.BUTTON_ID.set(clock, "OPTION BPM");
        inv.setItem(2, clock);
        ItemStack playIcon = new ItemStack(Material.PAPER);
        MakeItem.setItemMetaByColor(playIcon, "再生", null, "next_b_right", ItemData.BUTTON_ID, "PLAY");
        inv.setItem(4, playIcon);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        Inventory clicked_inv = event.getClickedInventory();
        if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "OPTION BPM")) {
            int bpm = ItemData.BPM.get(clicked);
            closeFlag = false;
            player.closeInventory();
            DawsOptionBpmHolder dawsOptionBpmHolder = new DawsOptionBpmHolder();
            dawsOptionBpmHolder.updateBpmIcons(bpm);
            player.openInventory(dawsOptionBpmHolder.getInventory());
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "PLAY")) {
            int bpm = ItemData.BPM.get(Objects.requireNonNull(clicked_inv.getItem(2)));
            MakeItem.setItemMeta(clicked, "再生停止", null, "elytra", ItemData.BUTTON_ID, "STOP");
            ItemStack pdcHolder = player.getInventory().getItemInMainHand().clone();
            ItemData.BPM.set(pdcHolder,bpm);
            PlayMusic play = new PlayMusic();
            play.setPrivate(true);
            play.setRequester(player);
            PlayMusicManager.setPlayingMusic(player, play);
            play.playMusic(player, pdcHolder);
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "STOP")) {
            PlayMusic play = PlayMusicManager.getMusic(player);
            MakeItem.setItemMeta(clicked, "再生", null, "next_b_right", ItemData.BUTTON_ID, "PLAY");
            play.stopTask(player);
        }
    }

    @Override
    public void onClose(Player player) {
        PlayMusic play = PlayMusicManager.getMusic(player);
        if (play != null) play.stopTask(player);
        if(!closeFlag)return;
        closeFlag = false;
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(),() -> {
            MainMenuHolder mainMenuHolder = new MainMenuHolder();
            player.openInventory(mainMenuHolder.getInventory());
        });

    }
}

