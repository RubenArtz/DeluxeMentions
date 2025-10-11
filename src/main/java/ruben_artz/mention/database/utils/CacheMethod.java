package ruben_artz.mention.database.utils;

import ruben_artz.mention.DeluxeMentions;
import ruben_artz.mention.database.Cache;

import java.sql.Connection;
import java.sql.SQLException;

public interface CacheMethod {
    Connection getConnection() throws SQLException;

    void init(DeluxeMentions plugin, Cache cacheInstance);

    void close(Connection connection) throws SQLException;

    void shutdown();
}