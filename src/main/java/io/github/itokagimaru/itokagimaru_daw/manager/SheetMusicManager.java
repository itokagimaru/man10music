package io.github.itokagimaru.itokagimaru_daw.manager;

import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import io.github.itokagimaru.itokagimaru_daw.util.MakeItem;
import io.github.itokagimaru.itokagimaru_daw.util.PlaySound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SheetMusicManager {
    public static ItemStack makeSheetMusic(Player player) {
        MusicManager musicManager = new MusicManager();
        int[] musicList = musicManager.loadMusicForPdc(player.getInventory().getItemInMainHand());
        ItemStack item = new ItemStack(Material.PAPER);
        MakeItem.setItemMeta(item,"記述済みの楽譜", null, "written_sheet_music",ItemData.ITEM_ID,"WRITTEN MUSIC");
        ItemData.MUSIC_SAVED_RED.set(item,musicList);
        PlaySound playSound = new PlaySound();
        playSound.playPageTurn(player);
        ItemMeta meta = item.getItemMeta();
        meta.setMaxStackSize(1);
        meta.lore(List.of(Component.text("written by " + player.getName())));
        item.setItemMeta(meta);
        return  item;
    }

    public static void loadSheetMusic(Player player, ItemStack item) {
        int[] music = ItemData.MUSIC_SAVED_RED.get(item);
        ItemStack saveItem = player.getInventory().getItemInMainHand();
        MusicManager.saveMusicForPdc(saveItem,music);
    }
}
