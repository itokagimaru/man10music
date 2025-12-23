package io.github.itokagimaru.itokagimaru_daw.manager;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import io.github.itokagimaru.itokagimaru_daw.db.MySQLManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class InventoryManager {
    private final HashMap<UUID, ItemStack[]> inv = Itokagimaru_daw.inv;
    private MySQLManager mysql;
    public InventoryManager(MySQLManager mySQLManager){
        mysql = mySQLManager;
    }

    public void saveInventory(Player player) {
        inv.put(player.getUniqueId(), player.getInventory().getContents().clone());
        saveToDB(player, true);
    }

    public void saveToDB(Player p, boolean flag) {

        String sql =
                "INSERT INTO player_data (uuid, inventory) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE inventory = ?";

        String inv = toBase64(p.getInventory());

        Bukkit.getScheduler().runTaskAsynchronously(Itokagimaru_daw.getInstance(), () -> {
            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, p.getUniqueId().toString());
                ps.setString(2, inv);
                ps.setString(3, inv);

                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadInventory(Player player) {
        if (!inv.containsKey(player.getUniqueId())) {
            return;
        }
        player.getInventory().setContents(inv.get(player.getUniqueId()).clone());
        inv.remove(player.getUniqueId());
    }

    public void loadFromDB(Player p) {

        String selectSql =
                "SELECT inventory FROM player_data WHERE uuid = ?";

        Bukkit.getScheduler().runTaskAsynchronously(Itokagimaru_daw.getInstance(), () -> {
            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(selectSql)) {

                ps.setString(1, p.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) return;

                String inv = rs.getString("inventory");

                Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(), () -> {
                    fromBase64(p.getInventory(), inv);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }


    public String toBase64(PlayerInventory inv) {
        try {
            YamlConfiguration cfg = new YamlConfiguration();
            cfg.set("items", inv.getContents());

            String yaml = cfg.saveToString();
            return Base64.getEncoder()
                    .encodeToString(yaml.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void fromBase64(PlayerInventory inv, String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            String yaml = new String(bytes, StandardCharsets.UTF_8);

            YamlConfiguration cfg = new YamlConfiguration();
            cfg.loadFromString(yaml);

            List<?> list = cfg.getList("items");
            ItemStack[] items = list.toArray(new ItemStack[0]);

            inv.setContents(items);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
