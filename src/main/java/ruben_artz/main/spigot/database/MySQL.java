package ruben_artz.main.spigot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ruben_artz.main.spigot.DeluxeMentions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {
    private static HikariDataSource ds;
    private final HikariConfig hikariConfig = new HikariConfig();

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void init(DeluxeMentions plugin) {
        String host = plugin.getConfig().getString("MENTION.DATABASE.HOST");
        String username = plugin.getConfig().getString("MENTION.DATABASE.USER");
        String password = plugin.getConfig().getString("MENTION.DATABASE.PASSWORD");
        String database = plugin.getConfig().getString("MENTION.DATABASE.DATABASE");
        boolean useSSL = plugin.getConfig().getBoolean("MENTION.DATABASE.USE_SSL");
        boolean allowPublicKeyRetrieval = plugin.getConfig().getBoolean("MENTION.DATABASE.ALLOW_PUBLIC_KEY_RETRIEVAL");
        int minCount = plugin.getConfig().getInt("MENTION.DATABASE.MIN_CONNECTIONS");
        int maxCount = plugin.getConfig().getInt("MENTION.DATABASE.MAX_CONNECTIONS");

        String url = "jdbc:mysql://"+host+"/"+database+"?useSSL="+useSSL+"&allowPublicKeyRetrieval="+allowPublicKeyRetrieval+"&characterEncoding=utf8&useInformationSchema=true";
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(maxCount);
        hikariConfig.setMinimumIdle(minCount);

        ds = new HikariDataSource(hikariConfig);
        ds.setLeakDetectionThreshold(25 * 1000);

        try (Connection connection = ds.getConnection()) {
            Statement statement = connection.createStatement();
            String prepared = "CREATE TABLE IF NOT EXISTS " + plugin.table + " (" +
                    "UUID VARCHAR(45) NOT NULL," +
                    "EXCLUDETIMER VARCHAR(45) NOT NULL," +
                    "MENTION VARCHAR(45) NOT NULL," +
                    "PRIMARY KEY (USER))";
            statement.executeUpdate(prepared);
            plugin.sendConsole(plugin.prefix + plugin.getFileTranslations().getString("MESSAGES_MYSQL_CONNECTED"));
        } catch (SQLException exception) {
            exception.printStackTrace();
            plugin.sendConsole(plugin.prefix + plugin.getFileTranslations().getString("MESSAGE_MYSQL_NOT_CONNECTED"));
        }
    }

    public static void close(Connection connection) throws SQLException {
        connection.close();
    }

    public static void shutdown() {
        ds.close();
    }
}
