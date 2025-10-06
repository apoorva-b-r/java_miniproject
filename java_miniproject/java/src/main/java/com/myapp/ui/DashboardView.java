package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.myapp.model.User;
import com.myapp.model.Event;
import com.myapp.dao.EventDAO;

public class DashboardView extends JPanel {

    public DashboardView(User user) {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername() + " 👋", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBackground(Color.WHITE);

        // --- Upcoming Events
        JTextArea upcomingArea = new JTextArea();
        upcomingArea.setEditable(false);
        upcomingArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        upcomingArea.setBorder(BorderFactory.createTitledBorder("🗓️ Upcoming Events"));
        populateUpcomingEvents(upcomingArea, user);

        // --- Stats
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        statsArea.setBorder(BorderFactory.createTitledBorder("📊 Your Stats"));
        populateStats(statsArea, user);

        // --- Quote
        JTextArea quoteArea = new JTextArea(getRandomQuote());
        quoteArea.setEditable(false);
        quoteArea.setFont(new Font("Serif", Font.ITALIC, 16));
        quoteArea.setBorder(BorderFactory.createTitledBorder("💪 Motivation"));
        quoteArea.setBackground(new Color(250, 250, 250));

        centerPanel.add(new JScrollPane(upcomingArea));
        centerPanel.add(statsArea);
        centerPanel.add(quoteArea);

        add(welcomeLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void populateUpcomingEvents(JTextArea area, User user) {
        try {
            EventDAO dao = new EventDAO();
            List<Event> events = dao.getUpcomingEvents(user.getId(), 3);

            if (events.isEmpty()) {
                area.setText("You have no upcoming events. Add one now!");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Event e : events) {
                sb.append(String.format("- %1$td %1$tB %1$tY — \"%2$s\" (%3$s)%n",
                        e.getStartTime(), e.getTitle(), e.getStatus()));
            }
            area.setText(sb.toString());
        } catch (Exception e) {
            area.setText("Error loading events: " + e.getMessage());
        }
    }

    private void populateStats(JTextArea area, User user) {
        try {
            EventDAO dao = new EventDAO();
            int total = dao.countEvents(user.getId());
            int upcoming = dao.countUpcomingEvents(user.getId());
            int completed = dao.countCompletedEvents(user.getId());

            area.setText(String.format(
                "✅ Total Events: %d%n📅 Upcoming: %d%n✔️ Completed: %d",
                total, upcoming, completed
            ));
        } catch (Exception e) {
            area.setText("Error loading stats: " + e.getMessage());
        }
    }

    private String getRandomQuote() {
        String[] quotes = {
            "“Discipline is choosing what you want most over what you want now.”",
            "“The secret of getting ahead is getting started.” — Mark Twain",
            "“It always seems impossible until it’s done.” — Nelson Mandela",
            "“Don’t watch the clock; do what it does. Keep going.” — Sam Levenson",
            "“Small steps every day lead to big results.”"
        };
        int idx = (int) (Math.random() * quotes.length);
        return quotes[idx];
    }
}
