package com.myapp.ui;

import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final CalendarView calendarView;
    private final DashboardView dashboardView;
    private final User loggedInUser;
    private final MainFrame mainFrame = this;

    public MainFrame(User user) {
        this.loggedInUser = user; // store the logged-in user
        calendarView = new CalendarView(loggedInUser); // pass user if needed for filtering
        dashboardView = new DashboardView(loggedInUser);

        setTitle("Study Productivity App");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Sidebar
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        JButton calendarBtn = new JButton("📅 Calendar View");
        JButton addEventBtn = new JButton("➕ Add Event");
        JButton btnProfile = new JButton("Profile"); // 👈 New Button
        JButton btnLogout = new JButton("Logout");
        JButton homeBtn = new JButton("🏠 Home");
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(homeBtn);
        sidePanel.add(Box.createRigidArea(new Dimension(0,10)));

        sidePanel.add(btnProfile); // 👈 Add here
        sidePanel.add(btnLogout);
        sidePanel.add(calendarBtn);
        sidePanel.add(Box.createRigidArea(new Dimension(0,10)));
        sidePanel.add(addEventBtn);

        // --- Content area
        contentPanel.add(dashboardView, "dashboard");
        contentPanel.add(calendarView, "calendar");

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "dashboard");

        // --- Button actions
        homeBtn.addActionListener(_ -> cardLayout.show(contentPanel, "dashboard"));
        calendarBtn.addActionListener(_ -> cardLayout.show(contentPanel, "calendar"));
        addEventBtn.addActionListener(_ -> {
            AddEventForm form = new AddEventForm(mainFrame, loggedInUser);
            form.setVisible(true);
        });
        btnProfile.addActionListener(_ -> {
            new ProfileView(loggedInUser).setVisible(true); // 👈 Opens profile
        });

        btnLogout.addActionListener(_ -> {
            dispose();
            new SignInForm().setVisible(true);
        });

        setVisible(true);
    }
    public void refreshAllViews() {
        dashboardView.refreshDashboard(); // ✅ reload counts
        calendarView.refreshAllTabs(); // ✅ reload table
    }
}