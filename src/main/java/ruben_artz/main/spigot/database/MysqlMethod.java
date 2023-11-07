package ruben_artz.main.spigot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.database.utils.CacheMethod;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MysqlMethod implements CacheMethod {
    @Override
    public Connection getConnection() throws SQLException {
        if (ds == null) return null;
        return ds.getConnection();
    }

    private static HikariDataSource ds;
    private final HikariConfig hikariConfig = new HikariConfig();

    @Override
    public void init(DeluxeMentions plugin, Cache cacheInstance) {
        String host = plugin.getConfig().getString("MENTION.DATABASE.HOST");
        String username = plugin.getConfig().getString("MENTION.DATABASE.USER");
        String password = plugin.getConfig().getString("MENTION.DATABASE.PASSWORD");
        String database = plugin.getConfig().getString("MENTION.DATABASE.DATABASE");
        int minCount = plugin.getConfig().getInt("MENTION.DATABASE.MIN_CONNECTIONS");
        int maxCount = plugin.getConfig().getInt("MENTION.DATABASE.MAX_CONNECTIONS");

        String url = "jdbc:mysql://" + host + "/" + database + "?" + setParameters(plugin);

        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(maxCount);
        hikariConfig.setMinimumIdle(minCount);

        ds = new HikariDataSource(hikariConfig);
        ds.setLeakDetectionThreshold(60 * 1000);

        try (Connection conn = getConnection()) {

            if (conn.isValid(5000)) {
                plugin.sendConsole(plugin.getPrefix()+plugin.getFileTranslations().getString("MESSAGE_DATABASE_CONNECTED").replace("{Database}", "Mysql"));
            }
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix()+plugin.getFileTranslations().getString("MESSAGE_MYSQL_NOT_CONNECTED"));
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    private String setParameters(DeluxeMentions plugin) {
        List<String> parameterList = plugin.getConfig().getStringList("MENTION.DATABASE.PARAMETERS");

        StringBuilder stringBuilder = new StringBuilder();

        for (String parameter : parameterList) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            stringBuilder.append(parameter);
        }

        return stringBuilder.toString();
    }


    @Override
    public void close(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public void shutdown() {
        ds.close();
    }
}