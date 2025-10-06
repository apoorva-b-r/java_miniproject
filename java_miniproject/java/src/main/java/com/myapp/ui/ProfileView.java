package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.User;

public class ProfileView extends JFrame {

    public ProfileView(User user) {
        setTitle("Profile");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));

        JLabel lblHeader = new JLabel("User Profile", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel lblUsername = new JLabel("Username: " + user.getUsername());
        JLabel lblUserId = new JLabel("User ID: " + user.getId());

        // You can expand later:
        // JLabel lblEmail = new JLabel("Email: " + user.getEmail());
        // JLabel lblCreatedAt = new JLabel("Account Created: " + user.getCreatedAt());

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(_ -> dispose());

        panel.add(lblHeader);
        panel.add(lblUsername);
        panel.add(lblUserId);
        panel.add(new JLabel()); // spacer
        panel.add(btnClose);

        add(panel);
        setVisible(true);
    }
}

