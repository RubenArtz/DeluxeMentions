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
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    private final Map<UUID, Set<UUID>> localBlockCache = new ConcurrentHashMap<>();

    private final String CREATE_TABLE_MYSQL = "CREATE TABLE IF NOT EXISTS " + plugin.table + " (" +
            "UUID VARCHAR(45) NOT NULL," +
            "EXCLUDETIMER VARCHAR(45) NOT NULL," +
            "MENTION VARCHAR(45) NOT NULL," +
            "BLOCKED_PLAYERS LONGTEXT," +
            "PRIMARY KEY (UUID))";

    private final String CREATE_TABLE_H2 = "CREATE TABLE IF NOT EXISTS " + plugin.table + " (" +
            "UUID VARCHAR(200), " +
            "EXCLUDETIMER VARCHAR(200), " +
            "MENTION VARCHAR(200), " +
            "BLOCKED_PLAYERS TEXT)";

    private final String UPDATE_BLOCKED = "UPDATE " + plugin.table + " SET BLOCKED_PLAYERS=? WHERE UUID=?";
    private final String SELECT_BLOCKED = "SELECT BLOCKED_PLAYERS FROM " + plugin.table + " WHERE UUID=?";
    private final String ADD_INFORMATION = "INSERT INTO " + plugin.table + " (UUID, EXCLUDETIMER, MENTION) VALUES (?, ?, ?)";
    private final String SELECT_BOOL = "SELECT * FROM " + plugin.table + " WHERE (UUID=?)";
    private final String SET_BOOL = "UPDATE " + plugin.table + " SET %0=? WHERE (UUID=?)";

    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<List<String>>() {
    }.getType();

    @Getter
    CacheMethod method;

    public Cache() {
        if (UtilityFunctions.ifIsMysql()) {
            method = new MysqlMethod();
        } else {
            method = new H2Method();
        }
        method.init(plugin, this);
        createTable();
    }

    public void loadCache(UUID playerUUID) {
        List<String> rawList = getBlockedListFromDB(playerUUID);

        Set<UUID> blockedSet = ConcurrentHashMap.newKeySet();
        for (String s : rawList) {
            try {
                blockedSet.add(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {
            }
        }

        localBlockCache.put(playerUUID, blockedSet);
    }

    public void unloadCache(UUID playerUUID) {
        localBlockCache.remove(playerUUID);
    }

    public boolean isBlockedLocally(UUID playerUUID, UUID targetUUID) {
        if (localBlockCache.containsKey(playerUUID)) {
            return localBlockCache.get(playerUUID).contains(targetUUID);
        }
        return isBlocked(playerUUID, targetUUID);
    }

    private void createTable() {
        try (Connection connection = method.getConnection(); PreparedStatement ps = connection.prepareStatement(UtilityFunctions.ifIsMysql() ? CREATE_TABLE_MYSQL : CREATE_TABLE_H2)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
    }

    public void addDatabase(UUID uuid) {
        try (Connection connection = method.getConnection(); PreparedStatement ps = connection.prepareStatement(ADD_INFORMATION)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, String.valueOf(false));
            ps.setString(3, String.valueOf(true));
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
    }

    public boolean setBool(UUID uuid, String column) {
        try (Connection connection = method.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + column + " FROM " + plugin.table + " WHERE UUID=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(column).equals("true");
                }
            }
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
        return false;
    }

    public void set(UUID uuid, String column, boolean boolValue) {
        try (Connection connection = method.getConnection(); PreparedStatement ps = connection.prepareStatement(SET_BOOL.replace("%0", column))) {
            ps.setString(1, String.valueOf(boolValue));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
    }

    public List<String> getBlockedList(UUID playerUUID) {
        if (localBlockCache.containsKey(playerUUID)) {
            List<String> list = new ArrayList<>();
            for (UUID u : localBlockCache.get(playerUUID)) {
                list.add(u.toString());
            }
            return list;
        }
        return getBlockedListFromDB(playerUUID);
    }

    private List<String> getBlockedListFromDB(UUID playerUUID) {
        List<String> blockedPlayers = new ArrayList<>();
        try (Connection connection = method.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BLOCKED)) {
            ps.setString(1, playerUUID.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    String json = resultSet.getString("BLOCKED_PLAYERS");
                    if (json != null && !json.isEmpty()) {
                        blockedPlayers = gson.fromJson(json, listType);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
        return blockedPlayers;
    }

    private void saveBlockedList(UUID playerUUID, List<String> blockedList) {
        String json = gson.toJson(blockedList);
        try (Connection connection = method.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_BLOCKED)) {
            ps.setString(1, json);
            ps.setString(2, playerUUID.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
    }

    public void addBlockedPlayer(UUID playerUUID, UUID targetUUID) {
        List<String> blocked = getBlockedListFromDB(playerUUID);
        if (!blocked.contains(targetUUID.toString())) {
            blocked.add(targetUUID.toString());
            saveBlockedList(playerUUID, blocked);

            if (localBlockCache.containsKey(playerUUID)) {
                localBlockCache.get(playerUUID).add(targetUUID);
            }
        }
    }

    public void removeBlockedPlayer(UUID playerUUID, UUID targetUUID) {
        List<String> blocked = getBlockedListFromDB(playerUUID);
        if (blocked.contains(targetUUID.toString())) {
            blocked.remove(targetUUID.toString());
            saveBlockedList(playerUUID, blocked);

            if (localBlockCache.containsKey(playerUUID)) {
                localBlockCache.get(playerUUID).remove(targetUUID);
            }
        }
    }

    public boolean isBlocked(UUID playerUUID, UUID targetUUID) {
        return getBlockedList(playerUUID).contains(targetUUID.toString());
    }

    public boolean ifNotExists(UUID uuid) {
        try (Connection connection = method.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BOOL)) {
            ps.setString(1, uuid.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) return false;
            }
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
        return true;
    }
}