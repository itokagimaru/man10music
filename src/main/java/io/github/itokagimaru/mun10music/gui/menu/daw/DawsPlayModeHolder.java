package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.gui.menu.base.BasePlayMusicHolder;
import io.github.itokagimaru.mun10music.manager.PlayMusicManager;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.task.PlayMusic;
import io.github.itokagimaru.mun10music.util.MakeItem;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DawsPlayModeHolder extends BasePlayMusicHolder {

    public DawsPlayModeHolder(Music music) {
        super(List.of(music));
        setup();
        isPrivate = true;
    }

    private void setup() {
        ItemStack clock = new ItemStack(Material.CLOCK);
        MakeItem.setItemMetaByColor(clock, "現在のBPM:" + musics.getFirst().getBpm(), NamedTextColor.YELLOW, 0, ItemData.BPM, musics.getFirst().getBpm());
        ItemData.BUTTON_ID.set(clock, "OPTION BPM");
        inv.setItem(2, clock);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        if (ItemData.BUTTON_ID.get(clickedItem).equals("OPTION BPM")) {
            onClickBpmIcon(event);
        }
    }



    protected void onClickBpmIcon(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        closeFlag = false;
        DawsOptionBpmHolder dawsOptionBpmHolder = new DawsOptionBpmHolder(musics.getFirst());
        player.openInventory(dawsOptionBpmHolder.getInventory());
    }

    @Override
    public void onClose(Player player) {
        PlayMusic play = PlayMusicManager.getMusic(player);
        if (play != null) play.stopTask(player);
        if(!closeFlag)return;
        closeFlag = false;
        Bukkit.getScheduler().runTask(Man10Music.getInstance(),() -> {
            MainMenuHolder mainMenuHolder = new MainMenuHolder();
            player.openInventory(mainMenuHolder.getInventory());
        });
    }
}
