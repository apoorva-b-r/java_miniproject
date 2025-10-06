package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.myapp.model.User;
import com.myapp.model.Event;
import com.myapp.dao.EventDAO;

public class DashboardView extends JPanel {
    private final User loggedInUser;
    private final JLabel lblTotal, lblUpcoming, lblCompleted;
    private final JTextArea upcomingArea;
    private final JTextArea statsArea;

    public DashboardView(User user) {
        this.loggedInUser = user;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Stats labels
        lblTotal = new JLabel();
        lblUpcoming = new JLabel();
        lblCompleted = new JLabel();

        JPanel statsLabelsPanel = new JPanel(new GridLayout(3,1));
        statsLabelsPanel.add(lblTotal);
        statsLabelsPanel.add(lblUpcoming);
        statsLabelsPanel.add(lblCompleted);

        // --- Upcoming events textarea
        upcomingArea = new JTextArea();
        upcomingArea.setEditable(false);
        upcomingArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        upcomingArea.setBorder(BorderFactory.createTitledBorder("🗓️ Upcoming Events"));

        // --- Stats textarea
        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        statsArea.setBorder(BorderFactory.createTitledBorder("📊 Your Stats"));

        // --- Quote
        JTextArea quoteArea = new JTextArea(getRandomQuote());
        quoteArea.setEditable(false);
        quoteArea.setFont(new Font("Serif", Font.ITALIC, 16));
        quoteArea.setBorder(BorderFactory.createTitledBorder("💪 Motivation"));
        quoteArea.setBackground(new Color(250, 250, 250));

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(new JScrollPane(upcomingArea));
        centerPanel.add(statsArea);
        centerPanel.add(quoteArea);

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername() + " 👋", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));

        add(welcomeLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        refreshDashboard(); // load initial data
    }

    public void refreshDashboard() {
        try {
            EventDAO dao = new EventDAO();

            int total = dao.countEvents(loggedInUser.getId());
            int upcoming = dao.countUpcomingEvents(loggedInUser.getId());
            int completed = dao.countCompletedEvents(loggedInUser.getId());

            lblTotal.setText("Total Events: " + total);
            lblUpcoming.setText("Upcoming Events: " + upcoming);
            lblCompleted.setText("Completed Events: " + completed);

            // update upcoming events textarea
            List<Event> events = dao.getUpcomingEvents(loggedInUser.getId(), 5);
            if (events.isEmpty()) {
                upcomingArea.setText("You have no upcoming events. Add one now!");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Event e : events) {
                    sb.append(String.format("- %1$td %1$tB %1$tY — \"%2$s\" (%3$s)%n",
                            e.getStartTime(), e.getTitle(), e.getStatus()));
                }
                upcomingArea.setText(sb.toString());
            }

            // update stats textarea
            statsArea.setText(String.format(
                    "✅ Total Events: %d%n📅 Upcoming: %d%n✔️ Completed: %d",
                    total, upcoming, completed
            ));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error refreshing dashboard: " + e.getMessage());
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