package io.github.itokagimaru.mun10music.manager;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleManager {
    public static void playNote(Player player, boolean isPrivate) {
        if (isPrivate){
            player.spawnParticle(Particle.NOTE, player.getLocation().add(0, 2, 0), 1);
        } else {
            player.getWorld().spawnParticle(Particle.NOTE, player.getLocation().add(0, 2, 0), 1);
        }
    }
}
