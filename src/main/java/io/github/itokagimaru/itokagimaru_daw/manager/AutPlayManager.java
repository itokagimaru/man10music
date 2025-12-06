package io.github.itokagimaru.itokagimaru_daw.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class AutPlayManager {
    public static HashMap<UUID, Boolean> autPlay  = new HashMap<>();

    public static void set(Player player, Boolean flag){
        autPlay.put(player.getUniqueId(), flag);
    }

    public static Boolean get(Player player){
        if(autPlay.get(player.getUniqueId()) == null) set(player, false);
        return autPlay.get(player.getUniqueId());
    }

    public static void remove(Player player){
        autPlay.remove(player.getUniqueId());
    }

}
