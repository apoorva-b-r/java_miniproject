package com.myapp;

import com.myapp.db.DatabaseConnection;
import com.myapp.ui.MainFrame;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        try {
            DatabaseConnection.getConnection();
            System.out.println("✅ Connection test successful!");
        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            e.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}