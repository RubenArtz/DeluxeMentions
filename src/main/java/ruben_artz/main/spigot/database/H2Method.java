package ruben_artz.main.spigot.database;

import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.database.utils.CacheMethod;
import ruben_artz.main.spigot.database.utils.UnClosableConnection;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

public class H2Method implements CacheMethod {
    private Connection conn;
    private DeluxeMentions plugin;
    private Cache cacheInstance;

    @Override
    public Connection getConnection() {
        try {
            if (conn.isClosed()) {
                plugin.getLogger().warning("H2 connection is dead, making a new one");
                init(plugin, cacheInstance);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new UnClosableConnection(conn);
    }

    @Override
    public void init(DeluxeMentions plugin, Cache cacheInstance) {
        this.plugin = plugin;
        this.cacheInstance = cacheInstance;

        // fix h2 error messages not being found (due to relocation)
        try {
            Field field = DbException.class.getDeclaredField("MESSAGES");
            field.setAccessible(true);
            ((Properties) (field.get(new Properties())))
                    .load(getClass().getResourceAsStream("/h2_messages.prop"));
        } catch (IllegalAccessException | NoSuchFieldException | IOException e) {
            plugin.getLogger().log(Level.WARNING, "Unable to set h2 messages file! Error messages from h2 might not be very useful!", e);
        }

        File file = new File(plugin.getDataFolder(), "cache.trace.db");
        if (file.exists()) {
            plugin.getLogger().info("Deleting junk trace file");
            try {
                if (!file.delete()) {
                    plugin.getLogger().warning("Failed to delete junk trace file!");
                }
            } catch (SecurityException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to delete junk trace file: ", e);
            }
        }

        String url = "jdbc:h2:" + plugin.getDataFolder().getAbsolutePath() + "/cache/" + File.separator + "cache;DATABASE_TO_UPPER=false;TRACE_LEVEL_FILE=0";
        try {
            //conn = DriverManager.getConnection(url);
            conn = new JdbcConnection(url, new Properties(), null, null, false);

            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_CONNECTED").replace("{Database}", "H2"));
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
    }

    @Override
    public void close(Connection connection) throws SQLException {

    }

    @Override
    public void shutdown() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
