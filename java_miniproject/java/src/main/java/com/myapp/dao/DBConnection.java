package com.myapp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database URL, username, password
    private static final String URL = "jdbc:postgresql://localhost:5432/minty";
    private static final String USER = "myuser";
    private static final String PASSWORD = "coolcoolcool";

    private DBConnection() {}

    // Method to get a database connection
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load driver class (optional for newer JDBC versions)
            Class.forName("org.postgresql.Driver"); // use "com.mysql.cj.jdbc.Driver" for MySQL

            // Get connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database.");
            e.printStackTrace();
        }
        return connection;
    }

    // Method to close the connection
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
