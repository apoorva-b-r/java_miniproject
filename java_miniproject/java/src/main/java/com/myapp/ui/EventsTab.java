package com.myapp.ui;

import com.myapp.dao.EventDAO;
import com.myapp.model.Event;
import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventsTab extends JPanel {
    private final User loggedInUser;
    private final MainFrame mainFrame;
    private JPanel calendarPanel;
    private YearMonth currentMonth;

    public EventsTab(User user, MainFrame mainFrame) {
        this.loggedInUser = user;
        this.mainFrame = mainFrame;
        this.currentMonth = YearMonth.now();

        setLayout(new BorderLayout());

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton prevBtn = new JButton("⏪");
        JButton nextBtn = new JButton("⏩");
        JLabel monthLabel = new JLabel(currentMonth.getMonth() + " " + currentMonth.getYear(), SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton addEventBtn = new JButton("➕ Add Event");
        addEventBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        topPanel.add(prevBtn, BorderLayout.WEST);
        topPanel.add(monthLabel, BorderLayout.CENTER);
        topPanel.add(nextBtn, BorderLayout.EAST);
        topPanel.add(addEventBtn, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Calendar panel
        calendarPanel = new JPanel();
        add(calendarPanel, BorderLayout.CENTER);

        // Load initial calendar
        loadCalendar();

        // Month navigation
        prevBtn.addActionListener(_ -> {
            currentMonth = currentMonth.minusMonths(1);
            monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
            loadCalendar();
        });

        nextBtn.addActionListener(_ -> {
            currentMonth = currentMonth.plusMonths(1);
            monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
            loadCalendar();
        });

        // Add Event button
        addEventBtn.addActionListener(_ -> new AddEventForm(mainFrame, loggedInUser).setVisible(true));
    }

    private void loadCalendar() {
        calendarPanel.removeAll();
        calendarPanel.setLayout(new GridLayout(0, 7));

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            calendarPanel.add(lbl);
        }

        try {
            EventDAO dao = new EventDAO();
            List<Event> events = dao.getEventsByUserId(loggedInUser.getId());
            Map<LocalDate, List<Event>> eventsByDate = events.stream()
                    .collect(Collectors.groupingBy(e -> e.getStartTime().toLocalDate()));

            LocalDate firstOfMonth = currentMonth.atDay(1);
            int blanks = firstOfMonth.getDayOfWeek().getValue() - 1;
            for (int i = 0; i < blanks; i++) calendarPanel.add(new JLabel(""));

            int daysInMonth = currentMonth.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = currentMonth.atDay(day);
                JPanel dayCell = new JPanel();
                dayCell.setLayout(new BoxLayout(dayCell, BoxLayout.Y_AXIS));
                dayCell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                dayCell.setBackground(Color.WHITE);

                JLabel dayLabel = new JLabel(String.valueOf(day));
                dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                dayCell.add(dayLabel);

                List<Event> dayEvents = eventsByDate.get(date);
                if (dayEvents != null) {
                    for (Event e : dayEvents) {
                        JButton eventBtn = new JButton(e.getTitle());
                        eventBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                        eventBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        eventBtn.setMargin(new Insets(2, 2, 2, 2));
                        eventBtn.addActionListener(_ -> showEventDetails(e));
                        dayCell.add(eventBtn);
                    }
                }

                calendarPanel.add(dayCell);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void showEventDetails(Event e) {
        JOptionPane.showMessageDialog(this,
                "Title: " + e.getTitle() + "\n" +
                        "Description: " + e.getDescription() + "\n" +
                        "Start: " + e.getStartTime() + "\n" +
                        "End: " + e.getEndTime() + "\n" +
                        "Reminder: " + e.getReminderBeforeMinutes() + " mins before",
                "Event Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshEvents() {
        loadCalendar();
    }
}
