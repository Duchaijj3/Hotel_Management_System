package com.hotel.util;



import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            Properties properties = new Properties();

            InputStream input = DBConnection.class
                    .getClassLoader()
                    .getResourceAsStream("database.properties");

            if (input == null) {
                throw new RuntimeException(
                        "Cannot find database.properties"
                );
            }

            properties.load(input);
            input.close();

            URL = properties.getProperty("db.url");
            USERNAME = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to initialize database connection", e
            );
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
        );
    }
}
