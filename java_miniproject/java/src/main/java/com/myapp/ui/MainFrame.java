package com.myapp.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Java Mini");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton btnAddEvent = new JButton("Add Event");
        JButton btnViewCalendar = new JButton("View Calendar");

        JPanel panel = new JPanel();
        panel.add(btnAddEvent);
        panel.add(btnViewCalendar);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }
}
