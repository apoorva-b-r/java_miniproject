package com.myapp.service;

import com.myapp.dao.ReminderDAO;
import com.myapp.model.Reminder;

import java.util.List;

public class ReminderService {

    private final ReminderDAO reminderDAO;

    public ReminderService() {
        this.reminderDAO = new ReminderDAO();
    }

    public boolean addReminder(String title, String description) {
        Reminder reminder = new Reminder();
        reminder.setTitle(title);
        reminder.setDescription(description);
        return reminderDAO.createReminder(reminder);
    }

    public List<Reminder> getReminders() {
        return reminderDAO.getAllReminders();
    }

    public boolean removeReminder(int id) {
        return reminderDAO.deleteReminder(id);
    }
}
