package com.myapp.service;

import com.myapp.model.Event;

import javax.swing.*;
import java.util.List;

public class ReminderChecker extends Thread {
    private final int userId;
    private final EventReminderService reminderService;

    public ReminderChecker(int userId) {
        this.userId = userId;
        this.reminderService = new EventReminderService();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Fetch due reminders for this user
                List<Event> dueReminders = reminderService.fetchDueReminders(userId);

                for (Event e : dueReminders) {
                    // Show popup
                    JOptionPane.showMessageDialog(
                            null,
                            "⏰ Reminder: " + e.getTitle() +
                                    "\nStarts at: " + e.getStartTime() +
                                    (e.getSubjectName() != null ? "\nSubject: " + e.getSubjectName() : ""),
                            "Event Reminder",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    // Mark as shown so it doesn’t repeat
                    reminderService.markReminderShown(e.getId());
                }

                // Check every 1 minute
                Thread.sleep(60_000);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}