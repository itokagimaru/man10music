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
                    "SELECT id, composer, relates, music_red, music_aqua, music_green, music_yellow, name, bpm FROM music_table WHERE id = ?";

            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, musicId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) return null;

                int[] musicRed = MusicDataCodec.toIntArray(rs.getBytes("music_red"));
                int[] musicAqua = MusicDataCodec.toIntArray(rs.getBytes("music_aqua"));
                int[] musicGreen = MusicDataCodec.toIntArray(rs.getBytes("music_green"));
                int[] musicYellow = MusicDataCodec.toIntArray(rs.getBytes("music_yellow"));
                List<UUID> relates = MusicDataCodec.fromYamlRelates(rs.getString("relates"));

                return Music.fromDb(
                        rs.getInt("id"),
                        rs.getString("composer"),
                        relates,
                        rs.getString("name"),
                        musicRed,
                        musicAqua,
                        musicGreen,
                        musicYellow,
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
            if (mysql == null || music == null) {
                // 失敗理由をログに残す
                Man10Music.getInstance().getLogger().warning("saveMusicToDb失敗: mysql/musicがnullです");
                return -1;
            }

            int[] musicRed = normalizeMusicArray(music.getMusic(Track.RED));
            int[] musicAqua = normalizeMusicArray(music.getMusic(Track.AQUA));
            int[] musicGreen = normalizeMusicArray(music.getMusic(Track.GREEN));
            int[] musicYellow = normalizeMusicArray(music.getMusic(Track.YELLOW));

            byte[] musicRedBytes = MusicDataCodec.toByteArray(musicRed);
            byte[] musicAquaBytes = MusicDataCodec.toByteArray(musicAqua);
            byte[] musicGreenBytes = MusicDataCodec.toByteArray(musicGreen);
            byte[] musicYellowBytes = MusicDataCodec.toByteArray(musicYellow);
            String relatesYaml = MusicDataCodec.toYamlRelates(music.getRelates());

            try (Connection con = mysql.getConn()) {
                if (music.getId() > 0) {
                    String updateSql =
                            "UPDATE music_table SET composer = ?, relates = ?, music_red = ?, music_aqua = ?, music_green = ?, music_yellow = ?, name = ?, bpm = ? WHERE id = ?";

                    try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                        ps.setString(1, music.getComposerUUID().toString());
                        ps.setString(2, relatesYaml);
                        ps.setBytes(3, musicRedBytes);
                        ps.setBytes(4, musicAquaBytes);
                        ps.setBytes(5, musicGreenBytes);
                        ps.setBytes(6, musicYellowBytes);
                        ps.setString(7, music.getTitle());
                        ps.setInt(8, music.getBpm());
                        ps.setInt(9, music.getId());

                        ps.executeUpdate();
                        return music.getId();
                    }
                }

                String insertSql =
                        "INSERT INTO music_table (composer, relates, music_red, music_aqua, music_green, music_yellow, name, bpm) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, music.getComposerUUID().toString());
                    ps.setString(2, relatesYaml);
                    ps.setBytes(3, musicRedBytes);
                    ps.setBytes(4, musicAquaBytes);
                    ps.setBytes(5, musicGreenBytes);
                    ps.setBytes(6, musicYellowBytes);
                    ps.setString(7, music.getTitle());
                    ps.setInt(8, music.getBpm());

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
        int bpm = 60;
        Music newMusic = Music.create(player.getUniqueId(), relates, title, bpm, Man10Music.MUSIC_LENGTH);

        AuthorityTableManager authorityManager = new AuthorityTableManager(mysql);
        // saveMusicToDbで新規作成し、ID確定後に権限テーブルへ登録する
        return saveMusicToDb(mysql, newMusic).thenCompose(generatedId -> {
            if (generatedId == null || generatedId <= 0) {
                return CompletableFuture.completedFuture(null);
            }

            return authorityManager.grant(player, generatedId).thenApply(granted -> {
                if (!granted) return null;
                return Music.fromDb(
                        generatedId,
                        player.getUniqueId().toString(),
                        relates,
                        title,
                        newMusic.getMusic(Track.RED),
                        newMusic.getMusic(Track.AQUA),
                        newMusic.getMusic(Track.GREEN),
                        newMusic.getMusic(Track.YELLOW),
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

    private static int[] normalizeMusicArray(int[] music) {
        if (music == null) return new int[Man10Music.MUSIC_LENGTH];
        if (music.length != Man10Music.MUSIC_LENGTH) return Arrays.copyOf(music, Man10Music.MUSIC_LENGTH);
        return music;
    }
}
