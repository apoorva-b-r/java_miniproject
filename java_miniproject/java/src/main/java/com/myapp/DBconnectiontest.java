package com.myapp;
import java.sql.Connection;
import com.myapp.db.DatabaseConnection;
public class DBconnectiontest
{
    public static void main( String[] args )
    {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Connection successful!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
