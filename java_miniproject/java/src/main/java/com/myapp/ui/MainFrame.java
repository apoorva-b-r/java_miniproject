package com.myapp.ui;

import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final DashboardView dashboardView;
    private final CalendarView calendarView;
    private final EventsTab eventsTab;
    private final TasksTab tasksTab;
    private final User loggedInUser;

    public MainFrame(User user) {
        this.loggedInUser = user;

        dashboardView = new DashboardView(loggedInUser);
        calendarView = new CalendarView(loggedInUser);
        eventsTab = new EventsTab(loggedInUser, this);
        tasksTab = new TasksTab(loggedInUser);

        setTitle("Study Productivity App");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        JButton btnHome = new JButton("🏠 Dashboard");
        JButton btnEvents = new JButton("📅 Events");
        JButton btnTasks = new JButton("🧾 Tasks");
        JButton btnProfile = new JButton("👤 Profile");
        JButton btnLogout = new JButton("🚪 Logout");

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
        sidePanel.add(Box.createVerticalGlue());

        contentPanel.add(dashboardView, "dashboard");
        contentPanel.add(eventsTab, "events");
        contentPanel.add(tasksTab, "tasks");
        contentPanel.add(calendarView, "calendar");

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

        setVisible(true);
    }

    public void refreshAllViews() {
        dashboardView.refreshDashboard();
        calendarView.refreshEvents();
        eventsTab.refreshEvents();
        tasksTab.refreshTasks();
    }
}