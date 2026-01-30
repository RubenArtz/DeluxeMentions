/*
 *
 * Copyright (c) 2026 Ruben_Artz and Artz Studio.
 *
 * This file is part of DeluxeMentions.
 *
 * DeluxeMentions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DeluxeMentions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DeluxeMentions.  If not, see https://www.gnu.org/licenses/.
 *
 */

package artzstudio.dev.mentions.spigot.database;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.database.utils.CacheMethod;
import artzstudio.dev.mentions.spigot.database.utils.UnClosableConnection;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class H2Method implements CacheMethod {
    private Connection conn;
    private DeluxeMentions plugin;
    private Cache cacheInstance;

    @Override
    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                plugin.getLogger().warning("H2 connection is dead or null, making a new one");
                init(plugin, cacheInstance);
            }

            if (conn == null) {
                throw new SQLException("Could not establish H2 connection even after retry.");
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

        try {
            Field field = DbException.class.getDeclaredField("MESSAGES");
            field.setAccessible(true);
            Properties props = (Properties) field.get(null);
            if (props == null) props = new Properties();

            var stream = getClass().getResourceAsStream("/h2_messages.prop");
            if (stream != null) {
                props.load(stream);
            }
        } catch (IllegalAccessException | NoSuchFieldException | IOException ignored) {
        }

        File junkFile = new File(plugin.getDataFolder(), "cache.trace.db");
        if (junkFile.exists()) {
            if (!junkFile.delete()) {
                plugin.getLogger().warning("Failed to delete junk trace file!");
            }
        }

        String dbPath = plugin.getDataFolder().getAbsolutePath() + File.separator + "cache" + File.separator + "cache";
        String url = "jdbc:h2:file:" + dbPath + ";TRACE_LEVEL_FILE=0";

        try {
            conn = new JdbcConnection(url, new Properties(), null, null, false);
            printSuccessMessage();

        } catch (SQLException e) {
            plugin.getLogger().warning("Detected database error (Possible relocation change or corruption).");
            plugin.getLogger().warning("Error: " + e.getMessage());
            plugin.getLogger().warning("Attempting to RESET cache database...");

            deleteCacheFolder();

            try {
                conn = new JdbcConnection(url, new Properties(), null, null, false);
                plugin.getLogger().info("Database reset successful! Connection established.");
                printSuccessMessage();
            } catch (SQLException fatalError) {
                plugin.getLogger().severe("FATAL: Could not create H2 database even after delete. Plugin functionality will be limited.");

                conn = null;

                throw new RuntimeException(fatalError);
            }
        }
    }

    private void printSuccessMessage() {
        if (plugin != null) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_CONNECTED")
                    .replace("{Database}", "H2"));
        }
    }

    private void deleteCacheFolder() {
        File cacheFolder = new File(plugin.getDataFolder(), "cache");
        if (cacheFolder.exists() && cacheFolder.isDirectory()) {
            File[] files = cacheFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        plugin.getLogger().warning("Could not delete file: " + file.getName());
                    }
                }
            }
            if (!cacheFolder.delete()) {
                plugin.getLogger().warning("Could not delete cache folder.");
            }
        }
    }

    @Override
    public void close(Connection connection) throws SQLException {
    }

    @Override
    public void shutdown() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}