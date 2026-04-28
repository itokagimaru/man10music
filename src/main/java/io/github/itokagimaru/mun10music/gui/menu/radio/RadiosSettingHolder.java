package io.github.itokagimaru.mun10music.gui.menu.radio;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.gui.menu.workspace.SettingHolder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;

public class RadiosSettingHolder extends SettingHolder {
    public RadiosSettingHolder(){
        super();
    }

    @Override
    public void onClose(Player player){
        if (!closeFlag) return;
        closeFlag = false;
        World world = player.getWorld();
        Entity entity = world.getEntity(frameUuid);
        if (!(entity instanceof GlowItemFrame fream)) return;
        Bukkit.getScheduler().runTask(Man10Music.getInstance(),() -> {
            RadioPlayHolder radioPlayHolder = new RadioPlayHolder(fream);
            radioPlayHolder.setFream(fream);
            player.openInventory(radioPlayHolder.getInventory());
        });
    }
}
