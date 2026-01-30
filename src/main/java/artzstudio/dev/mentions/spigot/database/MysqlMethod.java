/*
 *
 *  * Copyright (c) 2026 Ruben_Artz and Artz Studio.
 *  *
 *  * This file is part of DeluxeMentions.
 *  *
 *  * DeluxeMentions is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * DeluxeMentions is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with DeluxeMentions.  If not, see https://www.gnu.org/licenses/.
 *
 */

package artzstudio.dev.mentions.spigot.database;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.database.utils.CacheMethod;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MysqlMethod implements CacheMethod {
    private static HikariDataSource ds;
    private final HikariConfig hikariConfig = new HikariConfig();

    @Override
    public Connection getConnection() throws SQLException {
        if (ds == null) return null;
        return ds.getConnection();
    }

    @Override
    public void init(DeluxeMentions plugin, Cache cacheInstance) {
        String host = plugin.getConfigYaml().getString("MENTION.DATABASE.HOST");
        String username = plugin.getConfigYaml().getString("MENTION.DATABASE.USER");
        String password = plugin.getConfigYaml().getString("MENTION.DATABASE.PASSWORD");
        String database = plugin.getConfigYaml().getString("MENTION.DATABASE.DATABASE");
        int minCount = plugin.getConfigYaml().getInt("MENTION.DATABASE.MIN_CONNECTIONS");
        int maxCount = plugin.getConfigYaml().getInt("MENTION.DATABASE.MAX_CONNECTIONS");
        long maxLifeTime = plugin.getConfigYaml().getLong("MENTION.DATABASE.MAX_LIFETIME_MS");

        String url = "jdbc:mysql://" + host + "/" + database + "?" + setParameters(plugin);

        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(maxCount);
        hikariConfig.setMinimumIdle(minCount);
        hikariConfig.setMaxLifetime(maxLifeTime);
        hikariConfig.setIdleTimeout(900000);

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(hikariConfig);
        ds.setLeakDetectionThreshold(60 * 1000);

        try (Connection conn = getConnection()) {
            if (conn != null && conn.isValid(1)) {
                plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_CONNECTED")
                        .replace("{Database}", "Mysql"));
            }
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_MYSQL_NOT_CONNECTED"));
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    private String setParameters(DeluxeMentions plugin) {
        List<String> parameterList = plugin.getConfigYaml().getStringList("MENTION.DATABASE.PARAMETERS");

        StringBuilder stringBuilder = new StringBuilder();

        for (String parameter : parameterList) {
            if (!stringBuilder.isEmpty()) {
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