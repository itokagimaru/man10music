package io.github.itokagimaru.mun10music.manager;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.config.Items;
import io.github.itokagimaru.mun10music.config.PluginConfigData;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.util.MakeItem;
import io.github.itokagimaru.mun10music.util.PlaySound;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SheetMusicManager {
    public static ItemStack makeSheetMusic(Player player, ItemStack daw) {
        int[] musicList = MusicManager.loadMusicForPdc(daw);
        if(musicList.length == 0 || musicList[0] == -1) return null;
        PluginConfigData config = Man10Music.getInstance().getPluginConfigData();
        Items items = config.getItems();
        ItemStack item = new ItemStack(items.getSheetMusicWritten().getMaterial());
        MakeItem.setItemMeta(item,"記述済みの楽譜", null, items.getSheetMusicWritten().getCmd(),ItemData.ITEM_ID,"WRITTEN MUSIC");
        ItemData.MUSIC_SAVED_RED.set(item,musicList);
        PlaySound.playPageTurn(player);
        ItemMeta meta = item.getItemMeta();
        meta.setMaxStackSize(1);
        meta.lore(List.of(Component.text("written by " + player.getName())));
        item.setItemMeta(meta);
        return  item;
    }

    public static void loadSheetMusic(ItemStack daw, ItemStack item) {
        int[] music = ItemData.MUSIC_SAVED_RED.get(item);
        MusicManager.saveMusicForPdc(daw, music);
    }
}
