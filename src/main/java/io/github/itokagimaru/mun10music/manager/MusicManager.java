package io.github.itokagimaru.mun10music.manager;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;


public class MusicManager {

    public static void saveMusicForPdc(ItemStack pdcHolder, int[] music){
        if (music.length != Man10Music.MUSIC_LENGTH) music = Arrays.copyOf(music, Man10Music.MUSIC_LENGTH);
        if (pdcHolder == null)return;
        ItemData.MUSIC_SAVED_RED.set(pdcHolder, music);
    }

    public static int[] loadMusicForPdc(ItemStack pdcHolder){//後々複数セーブするのでクッションとして作っておきます
        if (pdcHolder == null)return null;
        int[] music = ItemData.MUSIC_SAVED_RED.get(pdcHolder);
        if (music.length != Man10Music.MUSIC_LENGTH) music = Arrays.copyOf(music, Man10Music.MUSIC_LENGTH);
        return music;
    }

}
