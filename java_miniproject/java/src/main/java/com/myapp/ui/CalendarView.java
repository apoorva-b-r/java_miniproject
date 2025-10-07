package com.myapp.ui;

import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;

public class CalendarView extends JPanel {
    private final User loggedInUser;
    private final MainFrame mainFrame;
    private EventsTab eventsTab;
    private TasksTab tasksTab;

    public CalendarView(User user, MainFrame mainFrame) {
        this.loggedInUser = user;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        eventsTab = new EventsTab(loggedInUser, mainFrame);
        tasksTab = new TasksTab(loggedInUser);

        tabbedPane.addTab("📅 Events", eventsTab);
        tabbedPane.addTab("✅ Tasks", tasksTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public EventsTab getEventsTab() {
        return eventsTab;
    }

    public void refreshAllTabs() {
        eventsTab.refreshEvents();
        tasksTab.reloadTasks();
    }
}
