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
    private JPanel eventsPanel;

    public CalendarView(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        loadEventsFromDatabase(user); // ✅ load initially
    }
    // ✅ NEW: reloads data whenever needed
    public void loadEventsFromDatabase(User user) {
        try {
            EventDAO dao = new EventDAO();
            List<Event> events = dao.getEventsByUserId(loggedInUser.getId());

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

    public void refreshEvents() {
    eventsPanel = new JPanel();
    eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
    add(eventsPanel, BorderLayout.CENTER); // or wherever it should appear
    try {
        EventDAO dao = new EventDAO();
        List<Event> upcomingEvents = dao.getUpcomingEvents(loggedInUser.getId(),5);

        // Clear old content
        eventsPanel.removeAll();

        // Re-populate the panel
        for (Event e : upcomingEvents) {
            JLabel lbl = new JLabel(e.getTitle() + " - " + e.getStartTime());
            eventsPanel.add(lbl);
        }

        // Repaint and revalidate the panel to update the UI
        eventsPanel.revalidate();
        eventsPanel.repaint();

    } catch (Exception ex) {
        ex.printStackTrace();
    }
    }
}
