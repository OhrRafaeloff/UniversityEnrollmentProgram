package com.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    /**
     * Establishes a new connection to the database using the configuration stored
     * in app.config.
     *
     * @return a {@link Connection} object if successful, or null if the connection
     *         fails.
     */
    public static Connection connect() {
        Connection connection = null;
        try {
            System.out.println("Attempting to connect to the database...");

            // Load properties from app.config
            Properties properties = new Properties();
            FileInputStream fis = new FileInputStream("app.config");
            properties.load(fis);

            String jdbcUrl = properties.getProperty("url");
            String dbUser = properties.getProperty("user");
            String dbPassword = properties.getProperty("password");

            // Establish a new connection
            connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
            System.out.println("Connected to the database!");
            // Failure exceptions
        } catch (IOException e) {
            System.out.println("Failed to load database configuration.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * Closes the provided connection to the database.
     * 
     * @param connection the {@link Connection} object to close.
     */
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
