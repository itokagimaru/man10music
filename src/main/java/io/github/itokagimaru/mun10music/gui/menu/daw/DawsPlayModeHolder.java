package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Icons;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.BaseGuiHolder;
import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.task.PlayMusic;
import io.github.itokagimaru.mun10music.util.MakeItem;
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
    ItemStack daw;
    private final int mainSlot;

    public DawsPlayModeHolder(int bpm, ItemStack daw, int mainSlot) {
        this.daw = daw;
        this.mainSlot = mainSlot;
        inv = Bukkit.createInventory(this, 9, Component.text("PlayMode"));
        setup(bpm);
    }

    public void setup(int bpm) {
        Icons icons = iconsData();

        ItemStack clock = new ItemStack(Material.CLOCK);
        MakeItem.setItemMetaByColor(clock, "現在のBPM:" + bpm, NamedTextColor.YELLOW, 0, ItemData.BPM, bpm);
        ItemData.BUTTON_ID.set(clock, "OPTION BPM");
        inv.setItem(2, clock);
        ItemStack playIcon = new ItemStack(icons.getTriangleRight().getMaterial());
        MakeItem.setItemMetaByColor(playIcon, "再生", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "PLAY");
        inv.setItem(4, playIcon);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        Inventory clickedInv = event.getClickedInventory();
        if (clicked == null || clickedInv == null) {
            return;
        }

        Icons icons = iconsData();
        if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "OPTION BPM")) {
            int bpm = ItemData.BPM.get(clicked);
            closeFlag = false;
            player.closeInventory();
            DawsOptionBpmHolder dawsOptionBpmHolder = new DawsOptionBpmHolder(daw, mainSlot);
            dawsOptionBpmHolder.updateBpmIcons(bpm);
            player.openInventory(dawsOptionBpmHolder.getInventory());
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "PLAY")) {
            ItemStack bpmItem = clickedInv.getItem(2);
            if (bpmItem == null) {
                return;
            }
            int bpm = ItemData.BPM.get(bpmItem);

            ItemStack stopIcon = new ItemStack(Material.ELYTRA);
            MakeItem.setItemMeta(stopIcon, "再生停止", null, 0, ItemData.BUTTON_ID, "STOP");
            inv.setItem(4, stopIcon);

            ItemStack pdcHolder = daw.clone();
            ItemData.BPM.set(pdcHolder,bpm);
            PlayMusic play = new PlayMusic();
            play.setPrivate(true);
            play.setRequester(player);
            PlayMusicManager.setPlayingMusic(player, play);
            play.playMusic(player, pdcHolder, musicData().getDefaultVolume(), musicData().getSoundRange());
        } else if (Objects.equals(ItemData.BUTTON_ID.get(clicked), "STOP")) {
            PlayMusic play = PlayMusicManager.getMusic(player);

            ItemStack playIcon = new ItemStack(icons.getTriangleRight().getMaterial());
            MakeItem.setItemMeta(playIcon, "再生", null, icons.getTriangleRight().getCmd(), ItemData.BUTTON_ID, "PLAY");
            inv.setItem(4, playIcon);

            play.stopTask(player);
        }
    }

    @Override
    public void onClose(Player player) {
        PlayMusic play = PlayMusicManager.getMusic(player);
        if (play != null) play.stopTask(player);
        if(!closeFlag)return;
        closeFlag = false;
        Bukkit.getScheduler().runTask(Man10Music.getInstance(),() -> {
            MainMenuHolder mainMenuHolder = new MainMenuHolder(daw, mainSlot);
            player.openInventory(mainMenuHolder.getInventory());
        });

    }
}

