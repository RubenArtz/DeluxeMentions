package ruben_artz.main.spigot.database.utils;

import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.database.Cache;

import java.sql.Connection;
import java.sql.SQLException;

public interface CacheMethod {
    Connection getConnection() throws SQLException;

    void init(DeluxeMentions plugin, Cache cacheInstance);

    void close(Connection connection) throws SQLException;

    void shutdown();
}