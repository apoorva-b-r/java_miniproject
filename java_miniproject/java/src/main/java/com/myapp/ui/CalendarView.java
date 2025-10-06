package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.User;

public class CalendarView extends JPanel {

    private final User loggedInUser;
    private EventsTab eventsTab;
    private TasksTab tasksTab;

    public CalendarView(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create tabs
        eventsTab = new EventsTab(loggedInUser);
        tasksTab = new TasksTab(loggedInUser);

        // Add them to the tabbed pane
        tabbedPane.addTab("📅 Events", eventsTab);
        tabbedPane.addTab("✅ Tasks", tasksTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    // called by MainFrame when refreshing dashboard
    public void refreshAllTabs() {
        eventsTab.reloadEvents();
        tasksTab.reloadTasks();
    }
}