package java.com.hotel;



import java.com.hotel.util.DBConnection;

import java.sql.Connection;

public class DBConnectionTest {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            System.out.println("================================");
            System.out.println("DATABASE CONNECTION SUCCESSFUL");
            System.out.println("================================");

            System.out.println("Database: "
                    + connection.getCatalog());

            System.out.println("Driver: "
                    + connection.getMetaData().getDriverName());

            System.out.println("URL: "
                    + connection.getMetaData().getURL());

        } catch (Exception e) {

            System.out.println("================================");
            System.out.println("DATABASE CONNECTION FAILED");
            System.out.println("================================");

            e.printStackTrace();
        }
    }
}