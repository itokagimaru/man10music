package io.github.itokagimaru.mun10music.manager;

import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.UUID;

public class AutPlayManager {
    public static HashMap<UUID, Boolean> autPlay  = new HashMap<>();

    public static void set(Entity target, Boolean flag){
        autPlay.put(target.getUniqueId(), flag);
    }

    public static Boolean get(Entity target){
        if(autPlay.get(target.getUniqueId()) == null) set(target, false);
        return autPlay.get(target.getUniqueId());
    }

    public static void remove(Entity target){
        autPlay.remove(target.getUniqueId());
    }

}
