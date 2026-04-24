package io.github.itokagimaru.itokagimaru_daw.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlaySound {
    public static void playNote(Entity target, int soundId, float volume,Double soundRange, boolean isPrivet) {
        float pitch = 1;
        soundId -= 2;//soundIdの正規化(諸事情で引数側が3からになってます
        if(soundId < 0) return;
        int playSound = 1 + 3 * ((soundId) / 3);
        switch (soundId % 3){
            case 0 -> pitch = (float) Math.pow(2,1.0/12);
            case 1 -> pitch = (float) 1;
            case 2 -> pitch = (float) Math.pow(2,-1.0/12);
        }
        if (soundId == 72){//例外処理だよ
            playSound = 72;
            pitch = 1;
        }
        if (isPrivet){
            if(!(target instanceof Player player))return;
            player.playSound(player.getLocation(), "soundid" + playSound, SoundCategory.RECORDS, volume, pitch);
            return;
        }
        for (Entity entity : target.getWorld().getNearbyEntities(target.getLocation(),soundRange,soundRange,soundRange)){
            if(entity instanceof Player player){
                player.playSound(target.getLocation(), "soundid" + playSound, SoundCategory.RECORDS, volume, pitch);
            }
        }

//        pitch = 1;
//        if (isPrivet){
//            player.playSound(player.getLocation(), "soundid" + soundId, SoundCategory.RECORDS, volume, pitch);
//            return;
//        }
//        player.getWorld().playSound(player.getLocation(), "soundid" + soundId, SoundCategory.RECORDS, volume, pitch);
    }

    public static void playPageTurn(Player player) {
        player.getWorld().playSound(player.getLocation().add(0, 2, 0), Sound.ITEM_BOOK_PAGE_TURN, 2f, 1f);
    }

    public static void playLevelUp(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
    }

    public  static void playCompassLock(Player player){
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK,1f,1f);
    }

    public static void playSmithingTableUse(Player player){
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE,1f,1f);
    }

    public static void playAnvilUse(Player player){
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE,1f,1f);
    }
}
