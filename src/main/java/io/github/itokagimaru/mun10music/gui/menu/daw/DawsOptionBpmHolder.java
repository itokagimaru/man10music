package io.github.itokagimaru.mun10music.gui.menu.daw;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.gui.menu.base.BaseOptionBpmHolder;
import io.github.itokagimaru.mun10music.manager.music.Music;
import io.github.itokagimaru.mun10music.manager.music.MusicManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DawsOptionBpmHolder extends BaseOptionBpmHolder {
    public DawsOptionBpmHolder(Music music) {
        super("Option/BPM", music);
    }

    @Override
    protected void onSetBpm(Player player, int bpm) {
        closeFlag = false;
        music.setBpm(bpm);
        MusicManager.saveMusicToDb(Man10Music.getInstance().getMySQLManager(), music).thenAccept(musicID -> {
            Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                if (musicID >= 0) {
                    DawsPlayModeHolder dawsPlayModeHolder = new DawsPlayModeHolder(music);
                    player.openInventory(dawsPlayModeHolder.getInventory());
                } else {
                    Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
                        player.sendMessage("§cBPMの保存に失敗しました");
                        player.closeInventory();
                    });
                }
            });
        });
    }

    @Override
    public void onClose(Player player) {
        if (!closeFlag)return;
        closeFlag = false;
        Bukkit.getScheduler().runTask(Man10Music.getInstance(), () -> {
            DawsPlayModeHolder dawsPlayModeHolder = new DawsPlayModeHolder(music);
            player.openInventory(dawsPlayModeHolder.getInventory());
        });
    }
}
