package com.myapp.ui;

import com.myapp.model.User;
import com.myapp.service.EventReminderService;
import com.myapp.model.Event;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final DashboardView dashboardView;
    private final CalendarView calendarView;
    private final EventsTab eventsTab;
    private final TasksTab tasksTab;
    private final User loggedInUser;
    private final SubjectsTab subjectsTab;
    private final EventReminderService eventReminderService = new EventReminderService();

    // 🕒 Timer reference
    private java.util.Timer reminderTimer;

    public MainFrame(User user) {
        this.loggedInUser = user;

        dashboardView = new DashboardView(loggedInUser);
        calendarView = new CalendarView(loggedInUser);
        eventsTab = new EventsTab(loggedInUser, this);
        tasksTab = new TasksTab(loggedInUser);
        subjectsTab = new SubjectsTab(loggedInUser, tasksTab);

        setTitle("Study Productivity App");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        startReminderChecker(); // ✅ start reminder system

        // Sidebar
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        JButton btnHome = new JButton("🏠 Dashboard");
        JButton btnEvents = new JButton("📅 Events");
        JButton btnTasks = new JButton("🧾 Tasks");
        JButton btnProfile = new JButton("👤 Profile");
        JButton btnLogout = new JButton("🚪 Logout");
        JButton btnSubjects = new JButton("📚 Subjects");

        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(btnHome);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(btnEvents);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(btnTasks);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(btnProfile);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(btnLogout);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(btnSubjects);
        sidePanel.add(Box.createVerticalGlue());

        contentPanel.add(dashboardView, "dashboard");
        contentPanel.add(eventsTab, "events");
        contentPanel.add(tasksTab, "tasks");
        contentPanel.add(calendarView, "calendar");
        contentPanel.add(subjectsTab, "subjects");

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "dashboard");

        // Button actions
        btnHome.addActionListener(_ -> cardLayout.show(contentPanel, "dashboard"));
        btnEvents.addActionListener(_ -> cardLayout.show(contentPanel, "events"));
        btnTasks.addActionListener(_ -> cardLayout.show(contentPanel, "tasks"));
        btnProfile.addActionListener(_ -> new ProfileView(loggedInUser).setVisible(true));
        btnLogout.addActionListener(_ -> {
            dispose();
            new SignInForm().setVisible(true);
        });
        btnSubjects.addActionListener(_ -> cardLayout.show(contentPanel, "subjects"));

        setVisible(true);
    }

    // ✅ Single correct version of reminder checker
    private void startReminderChecker() {
    if (reminderTimer != null) {
        reminderTimer.cancel();
    }

    reminderTimer = new java.util.Timer(true); // daemon timer
    System.out.println("🟢 Daily reminder checker started...");

    reminderTimer.scheduleAtFixedRate(new java.util.TimerTask() {
        @Override
        public void run() {
            try {
                System.out.println("📅 Checking today's reminders at: " + java.time.LocalDateTime.now());

                // Fetch all reminders due today (you can adjust query logic in EventReminderService)
                List<Event> todaysEvents = eventReminderService.fetchTodayReminders(loggedInUser.getId());

                if (!todaysEvents.isEmpty()) {
                    System.out.println("🎯 Found " + todaysEvents.size() + " event(s) for today.");

                    // Build one popup message
                    StringBuilder message = new StringBuilder("📆 Today's Events:\n\n");
                    for (Event event : todaysEvents) {
                        message.append("• ")
                               .append(event.getTitle())
                               .append(" — 🕒 ")
                               .append(event.getStartTime())
                               .append("\n");
                    }

                    // Show ONE reminder popup for all events
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            MainFrame.this,
                            message.toString(),
                            "⏰ Daily Reminder",
                            JOptionPane.INFORMATION_MESSAGE
                        );

                        // Mark all as shown AFTER showing popup
                        for (Event event : todaysEvents) {
                            try {
                                eventReminderService.markReminderShown(event.getId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    System.out.println("😴 No events for today or all already shown.");
                }
            } catch (Exception ex) {
                System.err.println("❌ Error in reminder checker:");
                ex.printStackTrace();
            }
        }
    }, 0, 60 * 60 * 1000); // 🔁 check every 1 hour
}


    // ✅ Allows restart when new event added
    public void restartReminderChecker() {
        startReminderChecker();
    }

    public void refreshAllViews() {
        dashboardView.refreshDashboard();
        calendarView.refreshEvents();
        eventsTab.refreshEvents();
        tasksTab.refreshTasks();
    }
}
