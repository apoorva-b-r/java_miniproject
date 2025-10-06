package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.User;

public class DashboardView extends JPanel {

    public DashboardView(User user) {
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel(
            "Welcome, " + user.getUsername() + "!", SwingConstants.CENTER
        );
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextArea tips = new JTextArea(
            """
            📘 Dashboard

            - View upcoming events in the Calendar section.
            - Add new study sessions or deadlines in Add Event.
            - Check your profile for account details.

            Stay productive! 💪
            """
        );
        tips.setEditable(false);
        tips.setFont(new Font("Monospaced", Font.PLAIN, 14));
        tips.setBackground(new Color(245, 245, 245));

        add(welcomeLabel, BorderLayout.NORTH);
        add(new JScrollPane(tips), BorderLayout.CENTER);
    }
}

