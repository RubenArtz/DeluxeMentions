package ruben_artz.main.spigot.database;

import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.other.ProjectUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Module {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public static void setData(UUID uuid) {
        final String prepared = "INSERT INTO "+plugin.table+" (UUID, EXCLUDETIMER, MENTION) VALUES (?, ?, ?)";
        try {
            if (ProjectUtil.ifIsMysql()) {
                try (Connection connection = MySQL.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(prepared);
                    statement.setString(1, uuid.toString());
                    statement.setString(2, String.valueOf(false));
                    statement.setString(3, String.valueOf(true));
                    statement.executeUpdate();
                    MySQL.close(connection);
                }
            } else {
                SQLite.checkConnection();
                try (Connection connection = SQLite.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(prepared);
                    statement.setString(1, uuid.toString());
                    statement.setString(2, String.valueOf(false));
                    statement.setString(3, String.valueOf(true));
                    statement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean getBool(UUID uuid, String column) {
        final String prepared = "SELECT * FROM "+plugin.table+" WHERE (UUID=?)";
        try {
            if (ProjectUtil.ifIsMysql()) {
                try (Connection connection = MySQL.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(prepared);
                    statement.setString(1, uuid.toString());
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return resultSet.getString(column).equals("true");
                    }
                    MySQL.close(connection);
                }
            } else {
                SQLite.checkConnection();
                try (Connection connection = SQLite.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(prepared);
                    statement.setString(1, uuid.toString());
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return resultSet.getString(column).equals("true");
                    }
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public static void set(UUID uuid, String column, boolean boolValue) {
        final String prepared = "UPDATE "+ plugin.table +" SET "+column+"=? WHERE (UUID=?)";
        try {
            if (ProjectUtil.ifIsMysql()) {
                try (Connection connection = MySQL.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(prepared);
                    statement.setString(1, String.valueOf(boolValue));
                    statement.setString(2, uuid.toString());
                    statement.executeUpdate();
                    MySQL.close(connection);
                }
            } else {
                SQLite.checkConnection();
                try (Connection connection = SQLite.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(prepared);
                    statement.setString(1, String.valueOf(boolValue));
                    statement.setString(2, uuid.toString());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean ifNotExists(UUID uuid) {
        final String prepared = "SELECT * FROM "+plugin.table+" WHERE (UUID=?)";
        try {
            if (ProjectUtil.ifIsMysql()) {
                try (Connection connection = MySQL.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(prepared);
                    statement.setString(1, uuid.toString());
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) return false;
                    MySQL.close(connection);
                }
            } else {
                SQLite.checkConnection();
                try (Connection connection = SQLite.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(prepared);
                    statement.setString(1, uuid.toString());
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) return false;
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return true;
    }
}
