package ruben_artz.main.spigot.database;

import lombok.Getter;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.database.utils.CacheMethod;
import ruben_artz.main.spigot.other.ProjectUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Cache {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    private final String CREATE_TABLE_H2 = "CREATE TABLE IF NOT EXISTS " + plugin.table + " (" +
            "UUID VARCHAR(200), " +
            "EXCLUDETIMER VARCHAR(200), " +
            "MENTION VARCHAR(200))";
    private final String CREATE_TABLE_MYSQL = "CREATE TABLE IF NOT EXISTS " + plugin.table + " (" +
            "UUID VARCHAR(45) NOT NULL," +
            "EXCLUDETIMER VARCHAR(45) NOT NULL," +
            "MENTION VARCHAR(45) NOT NULL," +
            "PRIMARY KEY (UUID))";
    private final String ADD_INFORMATION = "INSERT INTO " + plugin.table + " (UUID, EXCLUDETIMER, MENTION) VALUES (?, ?, ?)";
    private final String SELECT_BOOL = "SELECT * FROM " + plugin.table + " WHERE (UUID=?)";
    private final String SET_BOOL = "UPDATE " + plugin.table + " SET %0=? WHERE (UUID=?)";
    @Getter
    CacheMethod method;


    public Cache() {
        if (ProjectUtil.ifIsMysql()) {
            method = new MysqlMethod();
        } else {
            method = new H2Method();
        }

        method.init(plugin, this);

        createTable();
    }

    private void createTable() {

        try (Connection connection = method.getConnection(); PreparedStatement ps = connection.prepareStatement(ProjectUtil.ifIsMysql() ? CREATE_TABLE_MYSQL : CREATE_TABLE_H2)) {
            try {

                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
            }
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
    }

    public void addDatabase(UUID uuid) {

        try (Connection connection = method.getConnection(); PreparedStatement ps = connection.prepareStatement(ADD_INFORMATION)) {
            try {

                ps.setString(1, uuid.toString());
                ps.setString(2, String.valueOf(false));
                ps.setString(3, String.valueOf(true));

                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
            }
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
    }

    public boolean setBool(UUID uuid, String column) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = method.getConnection();
            ps = connection.prepareStatement(SELECT_BOOL);

            ps.setString(1, uuid.toString());

            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString(column).equals("true");
            }
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
            }
        }
        return false;
    }

    public void set(UUID uuid, String column, boolean boolValue) {

        try (Connection connection = method.getConnection(); PreparedStatement ps = connection.prepareStatement(SET_BOOL.replace("%0", column))) {
            try {

                ps.setString(1, String.valueOf(boolValue));
                ps.setString(2, uuid.toString());

                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
            }
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        }
    }

    public boolean ifNotExists(UUID uuid) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = method.getConnection();
            ps = connection.prepareStatement(SELECT_BOOL);

            ps.setString(1, uuid.toString());

            resultSet = ps.executeQuery();

            if (resultSet.next()) return false;
        } catch (SQLException e) {
            plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                plugin.sendConsole(plugin.getPrefix() + plugin.getFileTranslations().getString("MESSAGE_DATABASE_NOT_CONNECTED"));
            }
        }
        return true;
    }
}