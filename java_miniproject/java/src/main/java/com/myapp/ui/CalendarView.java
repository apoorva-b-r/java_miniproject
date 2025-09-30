package com.myapp.ui;

import com.myapp.dao.EventDAO;
import com.myapp.model.Event;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CalendarView extends JFrame {

    private JTable eventTable;
    private EventTableModel tableModel;

    public CalendarView() {
        setTitle("Calendar View");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            // Load events from DB (for now all events; later filter by user)
            EventDAO dao = new EventDAO();
            List<Event> events = dao.getAllEvents();

            // Pass events into table model
            tableModel = new EventTableModel(events);
            eventTable = new JTable(tableModel);

            add(new JScrollPane(eventTable), BorderLayout.CENTER);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading events: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }
}
