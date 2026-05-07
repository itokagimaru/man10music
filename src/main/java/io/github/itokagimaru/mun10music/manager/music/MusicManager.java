package io.github.itokagimaru.mun10music.manager.music;

import io.github.itokagimaru.mun10music.Man10Music;
import io.github.itokagimaru.mun10music.data.ItemData;
import io.github.itokagimaru.mun10music.db.MySQLManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class MusicManager {

    public static void saveMusicForPdc(ItemStack pdcHolder, int[] music){
        if (music.length != Man10Music.MUSIC_LENGTH) music = Arrays.copyOf(music, Man10Music.MUSIC_LENGTH);
        if (pdcHolder == null)return;
        ItemData.MUSIC_SAVED_RED.set(pdcHolder, music);
    }

    public static int[] loadMusicForPdc(ItemStack pdcHolder){//後々複数セーブするのでクッションとして作っておきます
        if (pdcHolder == null)return null;
        int[] music = ItemData.MUSIC_SAVED_RED.get(pdcHolder);
        if (music.length != Man10Music.MUSIC_LENGTH) music = Arrays.copyOf(music, Man10Music.MUSIC_LENGTH);
        return music;
    }

    public static CompletableFuture<Music> loadMusicFromDb(MySQLManager mysql, int musicId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql =
                    "SELECT id, composer, relates, music, name, bpm FROM music_table WHERE id = ?";

            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, musicId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) return null;

                byte[] musicBytes = rs.getBytes("musics");
                int[] music = MusicDataCodec.toIntArray(musicBytes);
                List<UUID> relates = MusicDataCodec.fromYamlRelates(rs.getString("relates"));

                return new Music(
                        rs.getInt("id"),
                        rs.getString("composer"),
                        relates,
                        rs.getString("name"),
                        music,
                        rs.getInt("bpm")
                );

            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static CompletableFuture<Integer> saveMusicToDb(MySQLManager mysql, Music music) {
        return CompletableFuture.supplyAsync(() -> {
            if (mysql == null || music == null || music.getMusic() == null) {
                // 失敗理由をログに残す
                Man10Music.getInstance().getLogger().warning("saveMusicToDb失敗: mysql/musicがnull、または楽曲配列がnullです");
                return -1;
            }

            int[] normalized = music.getMusic();
            if (normalized.length != Man10Music.MUSIC_LENGTH) {
                normalized = Arrays.copyOf(normalized, Man10Music.MUSIC_LENGTH);
            }
            byte[] musicBytes = MusicDataCodec.toByteArray(normalized);
            String relatesYaml = MusicDataCodec.toYamlRelates(music.getRelates());

            try (Connection con = mysql.getConn()) {
                if (music.getId() > 0) {
                    String updateSql =
                            "UPDATE music_table SET composer = ?, relates = ?, music = ?, name = ?, bpm = ? WHERE id = ?";

                    try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                        ps.setString(1, music.getComposerUUID().toString());
                        ps.setString(2, relatesYaml);
                        ps.setBytes(3, musicBytes);
                        ps.setString(4, music.getTitle());
                        ps.setInt(5, music.getBpm());
                        ps.setInt(6, music.getId());

                        ps.executeUpdate();
                        return music.getId();
                    }
                }

                String insertSql =
                        "INSERT INTO music_table (composer, relates, music, name, bpm) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, music.getComposerUUID().toString());
                    ps.setString(2, relatesYaml);
                    ps.setBytes(3, musicBytes);
                    ps.setString(4, music.getTitle());
                    ps.setInt(5, music.getBpm());

                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) return keys.getInt(1);
                    }
                    // 失敗理由をログに残す
                    Man10Music.getInstance().getLogger().warning("saveMusicToDb失敗: INSERTの自動採番IDが取得できませんでした");
                    return -1;
                }

            } catch (SQLException e) {
                // 失敗理由をログに残す
                Man10Music.getInstance().getLogger().warning("saveMusicToDb失敗: SQL例外が発生しました: " + e.getMessage());
                e.printStackTrace();
                return -1;
            }
        });
    }

    public static CompletableFuture<Music> createMusicForPlayer(MySQLManager mysql, Player player) {
        if (mysql == null || player == null) return CompletableFuture.completedFuture(null);

        List<UUID> relates = new ArrayList<>();
        String title = "未設定";
        int[] music = new int[Man10Music.MUSIC_LENGTH];
        int bpm = 60;
        Music newMusic = new Music(-1, player.getUniqueId().toString(), relates, title, music, bpm);

        AuthorityTableManager authorityManager = new AuthorityTableManager(mysql);
        // saveMusicToDbで新規作成し、ID確定後に権限テーブルへ登録する
        return saveMusicToDb(mysql, newMusic).thenCompose(generatedId -> {
            if (generatedId == null || generatedId <= 0) {
                return CompletableFuture.completedFuture(null);
            }

            return authorityManager.grant(player, generatedId).thenApply(granted -> {
                if (!granted) return null;
                return new Music(
                        generatedId,
                        player.getUniqueId().toString(),
                        relates,
                        title,
                        music,
                        bpm
                );
            });
        });
    }

    public static CompletableFuture<Boolean> deleteMusicFromDb(MySQLManager mysql, int musicId) {
        if (mysql == null || musicId <= 0) return CompletableFuture.completedFuture(false);

        AuthorityTableManager authorityManager = new AuthorityTableManager(mysql);
        // 権限削除→曲削除の順に実行する
        return authorityManager.revokeByMusicId(musicId).thenCompose(removed -> {
            if (!removed) return CompletableFuture.completedFuture(false);

            return CompletableFuture.supplyAsync(() -> {
                String sql = "DELETE FROM music_table WHERE id = ?";

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
        });
    }
}
