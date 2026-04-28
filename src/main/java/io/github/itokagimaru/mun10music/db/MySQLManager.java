package io.github.itokagimaru.mun10music.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLManager {
    private HikariDataSource ds;
    private void createTable() {
        String sql =
                "CREATE TABLE IF NOT EXISTS player_data (" +
                        "uuid CHAR(36) PRIMARY KEY," +
                        "inventory MEDIUMBLOB NOT NULL" +
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
        createTable();
    }

    public Connection getConn() throws SQLException {
        return ds.getConnection();
    }

    public void close() {
        if (ds != null) ds.close();
    }
}

