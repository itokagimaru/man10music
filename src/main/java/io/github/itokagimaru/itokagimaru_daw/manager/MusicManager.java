package io.github.itokagimaru.itokagimaru_daw.manager;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.data.ItemData;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.Arrays;


public class MusicManager {

    public static void saveMusicForPdc(ItemStack pdcHolder, int[] music){
        if (music.length != Itokagimaru_daw.MUSIC_LENGTH) music = Arrays.copyOf(music, Itokagimaru_daw.MUSIC_LENGTH);
        if (pdcHolder == null)return;
        ItemData.MUSIC_SAVED_RED.set(pdcHolder, music);
    }

    public static int[] loadMusicForPdc(ItemStack pdcHolder){//後々複数セーブするのでクッションとして作っておきます
        if (pdcHolder == null)return null;
        int[] music = ItemData.MUSIC_SAVED_RED.get(pdcHolder);
        if (music.length != Itokagimaru_daw.MUSIC_LENGTH) music = Arrays.copyOf(music, Itokagimaru_daw.MUSIC_LENGTH);
        return music;
    }

}
