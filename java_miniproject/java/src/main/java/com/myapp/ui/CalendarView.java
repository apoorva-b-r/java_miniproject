package com.myapp.ui;

import javax.swing.*;
import java.awt.*;

public class CalendarView extends JFrame {

    public CalendarView() {
        setTitle("Calendar View");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea calendarArea = new JTextArea();
        calendarArea.setText("This is where your calendar will appear...");
        calendarArea.setEditable(false);

        add(new JScrollPane(calendarArea), BorderLayout.CENTER);

        setVisible(true);
    }
}
