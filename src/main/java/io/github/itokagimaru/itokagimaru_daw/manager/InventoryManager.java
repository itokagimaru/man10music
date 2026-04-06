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
import java.util.concurrent.CompletableFuture;

public class InventoryManager {

    private final HashMap<UUID, ItemStack[]> inv = Itokagimaru_daw.inv;
    private final MySQLManager mysql;

    public InventoryManager(MySQLManager mySQLManager){
        this.mysql = mySQLManager;
    }

    public void saveInventory(Player player) {

        ItemStack[] contents = player.getInventory().getContents().clone();
        inv.put(player.getUniqueId(), contents);


        saveToDB(player.getUniqueId(), contents);
    }

    public CompletableFuture<Void> saveToDB(UUID uuid, ItemStack[] contents) {

        return CompletableFuture
                .supplyAsync(() -> toBase64(contents)) // 重い処理
                .thenAcceptAsync(base64 -> {

                    String sql =
                            "INSERT INTO player_data (uuid, inventory) VALUES (?, ?) " +
                                    "ON DUPLICATE KEY UPDATE inventory = ?";

                    try (Connection con = mysql.getConn();
                         PreparedStatement ps = con.prepareStatement(sql)) {

                        ps.setString(1, uuid.toString());
                        ps.setString(2, base64);
                        ps.setString(3, base64);

                        ps.executeUpdate();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                });
    }

    public void loadInventory(Player player) {
        if (!inv.containsKey(player.getUniqueId())) return;

        player.getInventory().setContents(inv.get(player.getUniqueId()).clone());
        inv.remove(player.getUniqueId());
    }

    public CompletableFuture<Void> loadFromDB(Player player) {

        UUID uuid = player.getUniqueId();

        return CompletableFuture
                .supplyAsync(() -> {

                    String selectSql =
                            "SELECT inventory FROM player_data WHERE uuid = ?";

                    try (Connection con = mysql.getConn();
                         PreparedStatement ps = con.prepareStatement(selectSql)) {

                        ps.setString(1, uuid.toString());
                        ResultSet rs = ps.executeQuery();

                        if (!rs.next()) return null;

                        return rs.getString("inventory");

                    } catch (SQLException e) {
                        e.printStackTrace();
                        return null;
                    }

                })
                .thenAccept(base64 -> {

                    if (base64 == null) return;

                    // Bukkit APIはメインスレッド
                    Bukkit.getScheduler().runTask(Itokagimaru_daw.getInstance(), () -> {
                        ItemStack[] items = fromBase64(base64);
                        player.getInventory().setContents(items);
                    });

                });
    }

    public String toBase64(ItemStack[] items) {
        try {
            YamlConfiguration cfg = new YamlConfiguration();
            cfg.set("items", items);

            String yaml = cfg.saveToString();
            return Base64.getEncoder()
                    .encodeToString(yaml.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public ItemStack[] fromBase64(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            String yaml = new String(bytes, StandardCharsets.UTF_8);

            YamlConfiguration cfg = new YamlConfiguration();
            cfg.loadFromString(yaml);

            List<?> list = cfg.getList("items");
            return list.toArray(new ItemStack[0]);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
