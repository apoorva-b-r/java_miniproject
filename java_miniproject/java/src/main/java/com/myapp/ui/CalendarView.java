package com.myapp.ui;

import com.myapp.dao.EventDAO;
import com.myapp.model.Event;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CalendarView extends JPanel {

    private JTable eventTable;
    private EventTableModel tableModel;

    public CalendarView() {
        setLayout(new BorderLayout());
        loadEventsFromDatabase(); // ✅ load initially
    }

    // ✅ NEW: reloads data whenever needed
    public void loadEventsFromDatabase() {
        try {
            EventDAO dao = new EventDAO();
            List<Event> events = dao.getAllEvents();

            if (tableModel == null) {
                tableModel = new EventTableModel(events);
                eventTable = new JTable(tableModel);
                add(new JScrollPane(eventTable), BorderLayout.CENTER);
            } else {
                tableModel.setEvents(events);  // ✅ update the model’s data
                tableModel.fireTableDataChanged(); // ✅ refresh the table
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading events: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
