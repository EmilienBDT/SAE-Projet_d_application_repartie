package fr.iutnc.rmi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Gestionnaire de connexion à la base de données.
 */
public class DatabaseManager {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    ConfigLoader.get("db.url"),
                    ConfigLoader.get("db.user"),
                    ConfigLoader.get("db.password"));
        }
        return connection;
    }
}