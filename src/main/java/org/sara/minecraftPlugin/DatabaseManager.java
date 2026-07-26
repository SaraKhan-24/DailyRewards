package org.sara.minecraftPlugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Wraps a single SQLite connection stored in the plugin's data folder
 * (plugins/DailyRewards/database.db). The SQLite JDBC driver is bundled
 * with Paper, so no extra dependency/shading is required.
 */
public class DatabaseManager {

    private final JavaPlugin plugin;
    private Connection connection;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/database.db";
            connection = DriverManager.getConnection(url);
            plugin.getLogger().info("Connected to SQLite database.");
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().severe("Could not connect to the database: " + e.getMessage());
        }
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS rewards ("
                + "uuid TEXT PRIMARY KEY,"
                + "balance INTEGER NOT NULL DEFAULT 0,"
                + "last_claim INTEGER NOT NULL DEFAULT 0"
                + ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not create table: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not close the database connection: " + e.getMessage());
        }
    }

    /** Epoch-millis timestamp of the player's last claim, or 0 if they have never claimed. */
    public long getLastClaim(UUID uuid) {
        String sql = "SELECT last_claim FROM rewards WHERE uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getLong("last_claim");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not read last claim: " + e.getMessage());
        }
        return 0L;
    }

    public int getBalance(UUID uuid) {
        String sql = "SELECT balance FROM rewards WHERE uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("balance");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not read balance: " + e.getMessage());
        }
        return 0;
    }

    /** Adds {@code amount} to the player's balance and stamps the current time as their last claim. */
    public void claimReward(UUID uuid, int amount) {
        String sql = "INSERT INTO rewards (uuid, balance, last_claim) VALUES (?, ?, ?) "
                + "ON CONFLICT(uuid) DO UPDATE SET "
                + "balance = balance + excluded.balance, "
                + "last_claim = excluded.last_claim";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setInt(2, amount);
            statement.setLong(3, System.currentTimeMillis());
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not save claim: " + e.getMessage());
        }
    }
}
