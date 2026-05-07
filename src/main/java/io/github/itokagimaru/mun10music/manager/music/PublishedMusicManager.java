package io.github.itokagimaru.mun10music.manager.music;

import io.github.itokagimaru.mun10music.db.MySQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PublishedMusicManager {

    public static CompletableFuture<Integer> savePublishedMusic(MySQLManager mysql, Music music) {
        if (mysql == null || music == null || music.getMusic() == null) {
            return CompletableFuture.completedFuture(-1);
        }

        return CompletableFuture.supplyAsync(() -> {
            byte[] musicBytes = MusicDataCodec.toByteArray(music.getMusic());
            String relatesYaml = MusicDataCodec.toYamlRelates(music.getRelates());

            String insertSql =
                    "INSERT INTO published_music (music_id, composer, relates, music, name, bpm) VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, music.getId());
                ps.setString(2, music.getComposerUUID().toString());
                ps.setString(3, relatesYaml);
                ps.setBytes(4, musicBytes);
                ps.setString(5, music.getTitle());
                ps.setInt(6, music.getBpm());

                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
                return -1;

            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        });
    }

    public static CompletableFuture<Integer> isNewPublishedMusic(MySQLManager mysql, Music music) {
        if (mysql == null || music == null || music.getMusic() == null) {
            return CompletableFuture.completedFuture(-1);
        }

        return loadPublishedByMusicId(mysql, music.getId()).thenApply(publishedList -> {
            for (PublishedMusic published : publishedList) {
                if (isSameMusic(published.getMusic(), music)) return published.getPublishedMusicID();
            }
            return -1;
        });
    }

    public static CompletableFuture<Music> loadPublishedByPublicId(MySQLManager mysql, int publicId) {
        if (mysql == null || publicId <= 0) return CompletableFuture.completedFuture(null);

        return CompletableFuture.supplyAsync(() -> {
            String sql =
                    "SELECT music_id, composer, relates, music, name, bpm FROM published_music WHERE public_id = ?";

            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, publicId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) return null;

                byte[] musicBytes = rs.getBytes("music");
                int[] music = MusicDataCodec.toIntArray(musicBytes);
                List<UUID> relates = MusicDataCodec.fromYamlRelates(rs.getString("relates"));

                return new Music(
                        rs.getInt("music_id"),
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

    public static CompletableFuture<List<PublishedMusic>> loadPublishedByMusicId(MySQLManager mysql, int musicId) {
        if (mysql == null || musicId <= 0) return CompletableFuture.completedFuture(Collections.emptyList());

        return CompletableFuture.supplyAsync(() -> {
            String sql =
                    "SELECT public_id, music_id, composer, relates, music, name, bpm FROM published_music WHERE music_id = ? ORDER BY public_id ASC";

            try (Connection con = mysql.getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, musicId);
                ResultSet rs = ps.executeQuery();

                List<PublishedMusic> result = new ArrayList<>();
                while (rs.next()) {
                    byte[] musicBytes = rs.getBytes("music");
                    int[] music = MusicDataCodec.toIntArray(musicBytes);
                    List<UUID> relates = MusicDataCodec.fromYamlRelates(rs.getString("relates"));

                    Music musicData = new Music(
                            rs.getInt("music_id"),
                            rs.getString("composer"),
                            relates,
                            rs.getString("name"),
                            music,
                            rs.getInt("bpm")
                    );

                    result.add(new PublishedMusic(rs.getInt("public_id"), musicData));
                }
                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        });
    }

    private static boolean isSameMusic(Music left, Music right) {
        if (left == null || right == null) return false;
        if (left.getId() != right.getId()) return false;
        if (!left.getTitle().equals(right.getTitle())) return false;
        if (left.getBpm() != right.getBpm()) return false;
        return Arrays.equals(left.getMusic(), right.getMusic());
    }
}
