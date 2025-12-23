package io.github.itokagimaru.itokagimaru_daw.gui.menu.radio;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.workspace.SettingHolder;
import io.github.itokagimaru.itokagimaru_daw.gui.menu.workspace.WorkspacesMenuHolder;
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
        Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(),() -> {
            RadioPlayHolder radioPlayHolder = new RadioPlayHolder(fream);
            radioPlayHolder.setFream(fream);
            player.openInventory(radioPlayHolder.getInventory());
        });
    }
}
