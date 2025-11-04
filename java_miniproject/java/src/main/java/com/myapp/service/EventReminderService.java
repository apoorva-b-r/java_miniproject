package com.myapp.service;

import com.myapp.dao.EventDAO;
import com.myapp.model.Event;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class EventReminderService {
    private final EventDAO eventDAO = new EventDAO();

    public List<Event> fetchDueReminders(int userId) throws Exception {
        return eventDAO.getDueReminders(userId, Timestamp.valueOf(LocalDateTime.now()));
    }
    public List<Event> fetchTodayReminders(int userId) throws SQLException {
    return eventDAO.getEventsForToday(userId);
    }

    public void markReminderShown(int eventId) throws Exception {
        eventDAO.markReminderShown(eventId);
    }
}