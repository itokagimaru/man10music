package io.github.itokagimaru.mun10music.commands;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.gui.menu.workspace.WorkspacesMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.github.itokagimaru.mun10music.gui.menu.radio.RadioPlayHolder;

public class MmusicFurnitures implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Man10Music.getInstance().getLogger().info("このコマンドはコンソールからのみ使用できます");
            return false;
        }
        Man10Music.getInstance().getLogger().info(Arrays.toString(args));
        switch (args[0]) {
            case "workspace" -> {
                if (args.length < 2) return false;
                String playerName = args[1];
                Player player = Bukkit.getPlayer(playerName);
                Man10Music.getInstance().getLogger().info(args[1]);
                if (player == null) return false;

                WorkspacesMenuHolder workspacesMenuHolder = new WorkspacesMenuHolder(player.getUniqueId());
                player.openInventory(workspacesMenuHolder.getInventory());
                return true;
            }
            case "radio" -> {
                if (args.length < 6) return false;
                String playerName = args[2];
                String strX = args[3];
                String strY = args[4];
                String strZ = args[5];
                double x, y, z;
                Player player;
                World world;
                Location location;
                try {//引数のstr→変換→
                    x = Double.parseDouble(strX);
                    y = Double.parseDouble(strY);
                    z = Double.parseDouble(strZ);
                    player = Bukkit.getPlayer(playerName);
                    if (player == null) return false;
                    world = player.getWorld();
                    location = new Location(world, x, y, z);
                }catch (NumberFormatException e) {
                    return false;
                }
                //座標にアマスタがあるか(複数出ないように)
                Collection<Entity> entities = world.getNearbyEntities(location, 0.5, 0.5, 0.5);
                ArmorStand stand = null;
                for (Entity entity : entities) {
                    byte isMarker = entity.getPersistentDataContainer().getOrDefault(new NamespacedKey("mmusic", "radiomarker"), PersistentDataType.BYTE, (byte) 0);
                    if (isMarker != (byte) 0 || !(entity instanceof ArmorStand)) continue;
                    stand = (ArmorStand) entity;
                }
                if (stand == null){//無ければ生み出す
                    stand = world.spawn(location, ArmorStand.class, armorStand -> {
                        armorStand.setInvisible(true);
                        armorStand.setInvulnerable(true);
                        armorStand.setGravity(false);
                        armorStand.setMarker(true);
                    });
                    stand.getPersistentDataContainer().set(new NamespacedKey("mmusic", "radiomarker"), PersistentDataType.BYTE, (byte) 1);
                }
                switch (args[1]) {
                    case "remove" -> {
                        // アマスタがあるなら消す
                        if (stand == null) return false;
                        stand.remove();
                        return true;
                    }
                    case "play" -> {
                        // アマスタがあるならラジオGUIを開く
                        if (stand == null) return false;
                        RadioPlayHolder radioPlayHolder = new RadioPlayHolder(player, stand);
                        player.openInventory(radioPlayHolder.getInventory());
                    }
                    default -> {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return null;
    }


    private byte[] doubleArrayToByteArray(double[] d) {
        ByteBuffer bb = ByteBuffer.allocate(d.length * 4);
        for (double value : d) {
            bb.putDouble(value);
        }
        return bb.array();
    }
}
