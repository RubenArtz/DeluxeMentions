package ruben_artz.main.spigot.database;

import org.bukkit.Bukkit;
import ruben_artz.main.spigot.DeluxeMentions;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    private static String url;
    private static Connection connection;

    public SQLite() {
        File folder = new File(plugin.getDataFolder() + "/cache");
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                plugin.sendConsole(plugin.prefix + plugin.getFileTranslations().getString("MESSAGE_SQLITE_ERROR_FOLDER"));
            }
        }
        File dataFolder = new File(folder.getPath() + "/database.db");
        if (!dataFolder.exists()) {
            try {
                if (!dataFolder.createNewFile()) {
                    plugin.sendConsole(plugin.prefix + plugin.getFileTranslations().getString("MESSAGE_SQLITE_ERROR_CREATE"));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        url = "jdbc:sqlite:" + dataFolder;
        try {
            Class.forName("org.sqlite.JDBC");
            DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            plugin.sendConsole(plugin.prefix + plugin.getFileTranslations().getString("MESSAGE_SQLITE_ERROR_CONNECTION"));
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public void init() {
        try {
            checkConnection();
            String prepared = "CREATE TABLE IF NOT EXISTS "+plugin.table+" (" +
                    "UUID VARCHAR(200), " +
                    "EXCLUDETIMER VARCHAR(200), " +
                    "MENTION VARCHAR(200))";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(prepared);
                plugin.sendConsole(""+plugin.prefix+plugin.getFileTranslations().getString("MESSAGE_USING_SQLITE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection() {
        return connection;
    }

    public static void checkConnection() throws SQLException {
        boolean renew = false;

        if (connection == null) renew = true;
        else
        if (connection.isClosed()) renew = true;

        if (renew) connection = DriverManager.getConnection(url);
    }

    public static void shutdown() {
        try {
            checkConnection();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
