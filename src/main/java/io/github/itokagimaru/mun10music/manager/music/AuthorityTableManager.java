package io.github.itokagimaru.mun10music.manager.music;

import io.github.itokagimaru.mun10music.db.MySQLManager;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AuthorityTableManager {

    private final MySQLManager mysql;

    public AuthorityTableManager(MySQLManager mysql) {
        this.mysql = mysql;
    }

    public CompletableFuture<Boolean> grant(Player player, int musicId) {
        return CompletableFuture.supplyAsync(() -> {
            if (mysql == null || player == null || musicId <= 0) return false;

            // 重複ペアは無視して権限を付与する
            String sql = "INSERT IGNORE INTO authority_table (uuid, music_id) VALUES (?, ?)";

            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, player.getUniqueId().toString());
                ps.setInt(2, musicId);
                ps.executeUpdate();
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> revoke(Player player, int musicId) {
        return CompletableFuture.supplyAsync(() -> {
            if (mysql == null || player == null || musicId <= 0) return false;

            // 対象の権限紐づけを削除する
            String sql = "DELETE FROM authority_table WHERE uuid = ? AND music_id = ?";

            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, player.getUniqueId().toString());
                ps.setInt(2, musicId);
                ps.executeUpdate();
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> revokeByMusicId(int musicId) {
        return CompletableFuture.supplyAsync(() -> {
            if (mysql == null || musicId <= 0) return false;

            // 曲IDに紐づく権限を全削除する
            String sql = "DELETE FROM authority_table WHERE music_id = ?";

            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, musicId);
                ps.executeUpdate();
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<List<Music>> loadAuthorizedMusic(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            if (mysql == null || player == null) return Collections.emptyList();

            String sql =
                    "SELECT m.id, m.composer, m.relates, m.music, m.name, m.bpm " +
                    "FROM authority_table a " +
                    "INNER JOIN music_table m ON a.music_id = m.id " +
                    "WHERE a.uuid = ? " +
                    "ORDER BY m.id ASC";

            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, player.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();

                List<Music> musics = new ArrayList<>();
                while (rs.next()) {
                    byte[] musicBytes = rs.getBytes("music");
                    int[] music = MusicDataCodec.toIntArray(musicBytes);
                    List<UUID> relates = MusicDataCodec.fromYamlRelates(rs.getString("relates"));

                    musics.add(new Music(
                            rs.getInt("id"),
                            rs.getString("composer"),
                            relates,
                            rs.getString("name"),
                            music,
                            rs.getInt("bpm")
                    ));
                }
                return musics;

            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        });
    }

    public CompletableFuture<List<UUID>> loadAuthorizedUuidsByMusicId(int musicId) {
        return CompletableFuture.supplyAsync(() -> {
            if (mysql == null || musicId <= 0) return Collections.emptyList();

            String sql = "SELECT uuid FROM authority_table WHERE music_id = ? ORDER BY uuid ASC";

            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, musicId);
                ResultSet rs = ps.executeQuery();

                List<UUID> uuids = new ArrayList<>();
                while (rs.next()) {
                    String uuidStr = rs.getString("uuid");
                    if (uuidStr == null || uuidStr.isBlank()) continue;
                    try {
                        uuids.add(UUID.fromString(uuidStr));
                    } catch (IllegalArgumentException ignored) {
                        // 不正なUUIDは無視
                    }
                }
                return uuids;

            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        });
    }
}
