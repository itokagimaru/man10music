package io.github.itokagimaru.mun10music.gui.menu.workspace;

import org.bukkit.entity.Player;

import java.util.*;

public class AnvilGUIOpening {
    public static final Map<UUID, NamingAnvilGUI> anvilOpening = new HashMap<>();
    public static boolean isOpening(Player player){
        NamingAnvilGUI anvilGUI = anvilOpening.get(player.getUniqueId());
        return anvilGUI != null;
    }
}
