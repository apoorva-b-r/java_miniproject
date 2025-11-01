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

    // ✅ Loads data into table view
    public void loadEventsFromDatabase(User user) {
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

    // ✅ This method shows colored event boxes
    public void refreshEvents() {
        if (eventsPanel == null) {
            eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            add(new JScrollPane(eventsPanel), BorderLayout.CENTER);
        }

        try {
            EventDAO dao = new EventDAO();
            List<Event> upcomingEvents = dao.getUpcomingEvents(loggedInUser.getId(), 5);

            // Clear old content
            eventsPanel.removeAll();
            eventsPanel.setBackground(Color.WHITE);

            // Add new colored boxes
            for (Event ev : upcomingEvents) {
                JPanel eventBox = new JPanel(new BorderLayout());
                eventBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                String color = ev.getSubjectColor();
                if (color != null && !color.isEmpty()) {
                    try {
                        eventBox.setBackground(Color.decode(color));
                    } catch (Exception e) {
                        eventBox.setBackground(Color.LIGHT_GRAY);
                    }
                } else {
                    eventBox.setBackground(Color.LIGHT_GRAY);
                }

                JLabel lbl = new JLabel("<html><b>" + ev.getTitle() + "</b> (" +
                        (ev.getSubjectName() != null ? ev.getSubjectName() : "No Subject") + ")<br>" +
                        ev.getStartTime() + "</html>");

                lbl.setOpaque(false);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                eventBox.add(lbl, BorderLayout.CENTER);

                eventBox.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(4, 8, 4, 8),
                        BorderFactory.createLineBorder(Color.WHITE, 1)
                ));

                eventsPanel.add(Box.createVerticalStrut(5));
                eventsPanel.add(eventBox);
            }

            // ✅ Update UI
            eventsPanel.revalidate();
            eventsPanel.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
