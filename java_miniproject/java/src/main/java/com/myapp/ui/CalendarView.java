package com.myapp.ui;

import com.myapp.dao.EventDAO;
import com.myapp.model.Event;
import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CalendarView extends JPanel {

    private JTable eventTable;
    private EventTableModel tableModel;
    private final User loggedInUser;

    public CalendarView(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        loadEventsFromDatabase(); // load initially
    }

    // ✅ No-argument method, uses stored loggedInUser
    public void loadEventsFromDatabase() {
        try {
            EventDAO dao = new EventDAO();
            List<Event> events = dao.getEventsByUserId(loggedInUser.getId());

            if (tableModel == null) {
                tableModel = new EventTableModel(events);
                eventTable = new JTable(tableModel);
                add(new JScrollPane(eventTable), BorderLayout.CENTER);
            } else {
                tableModel.setEvents(events);
                tableModel.fireTableDataChanged();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading events: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
