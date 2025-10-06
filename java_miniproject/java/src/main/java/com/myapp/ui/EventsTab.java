package com.myapp.ui;

import com.myapp.dao.EventDAO;
import com.myapp.model.Event;
import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EventsTab extends JPanel {

    private JTable eventTable;
    private EventTableModel tableModel;
    private final User loggedInUser;
    private JPanel eventsPanel;

    public EventsTab(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        loadEventsFromDatabase(); // load initially
    }

    // ✅ load events for the logged-in user
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

    // ✅ refresh recent/upcoming events dynamically
    public void reloadEvents() {
        if (eventsPanel == null) {
            eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            add(eventsPanel, BorderLayout.SOUTH); // optional small section below
        }

        try {
            EventDAO dao = new EventDAO();
            List<Event> upcomingEvents = dao.getUpcomingEvents(loggedInUser.getId(), 5);

            eventsPanel.removeAll();
            for (Event e : upcomingEvents) {
                JLabel lbl = new JLabel("🔸 " + e.getTitle() + " - " + e.getStartTime());
                eventsPanel.add(lbl);
            }

            eventsPanel.revalidate();
            eventsPanel.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

