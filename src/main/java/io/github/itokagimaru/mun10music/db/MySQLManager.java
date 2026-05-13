package io.github.itokagimaru.mun10music.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLManager {
    private HikariDataSource ds;

    private void createMusicTable() {
        String sql =
                "CREATE TABLE IF NOT EXISTS music_table (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "composer CHAR(36) NOT NULL," +
                        "relates TEXT," +
                        "music_red MEDIUMBLOB NOT NULL," +
                        "music_aqua MEDIUMBLOB NOT NULL," +
                        "music_green MEDIUMBLOB NOT NULL," +
                        "music_yellow MEDIUMBLOB NOT NULL," +
                        "name VARCHAR(100) NOT NULL," +
                        "bpm INT NOT NULL" +
                        ");";

        try (Connection con = ds.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createAuthorityTable() {
        String sql =
                "CREATE TABLE IF NOT EXISTS authority_table (" +
                        "uuid CHAR(36) NOT NULL," +
                        "music_id INT NOT NULL," +
                        "PRIMARY KEY (uuid, music_id)" +
                        ");";

        try (Connection con = ds.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPublishedMusicTable() {
        String sql =
                "CREATE TABLE IF NOT EXISTS published_music (" +
                        "public_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "music_id INT NOT NULL," +
                        "composer CHAR(36) NOT NULL," +
                        "relates TEXT," +
                        "music_red MEDIUMBLOB NOT NULL," +
                        "music_aqua MEDIUMBLOB NOT NULL," +
                        "music_green MEDIUMBLOB NOT NULL," +
                        "music_yellow MEDIUMBLOB NOT NULL," +
                        "name VARCHAR(100) NOT NULL," +
                        "bpm INT NOT NULL" +
                        ");";

        try (Connection con = ds.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init(String host, int port, String db, String user, String pass) {
        HikariConfig cfg = new HikariConfig();

        cfg.setJdbcUrl(
                "jdbc:mysql://" + host + ":" + port + "/" + db +
                        "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8"
        );
        cfg.setUsername(user);
        cfg.setPassword(pass);

        ds = new HikariDataSource(cfg);
        createMusicTable();
        createAuthorityTable();
        createPublishedMusicTable();
    }

    public Connection getConn() throws SQLException {
        return ds.getConnection();
    }

    public void close() {
        if (ds != null) ds.close();
    }
}
