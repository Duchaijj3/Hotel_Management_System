package com.hotel.util;



import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties PROPERTIES = new Properties();


    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            InputStream input = DBConnection.class
                    .getClassLoader()
                    .getResourceAsStream("database.properties");

            if (input != null) { PROPERTIES.load(input); input.close(); }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to initialize database connection", e
            );
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = setting("HMS_DB_URL", "db.url");
        String username = setting("HMS_DB_USERNAME", "db.username");
        String password = setting("HMS_DB_PASSWORD", "db.password");
        if (blank(url) || blank(username) || password == null) throw new SQLException("Database is not configured");
        return DriverManager.getConnection(url, username, password);
    }
    private static String setting(String env, String property) { String value=System.getenv(env); return blank(value)?PROPERTIES.getProperty(property):value; }
    private static boolean blank(String value) { return value==null || value.trim().isEmpty(); }
}
