package com.myapp.dao;

import com.myapp.db.DatabaseConnection;
import com.myapp.model.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    public void save(Event event) throws SQLException {
        String sql = "INSERT INTO events(user_id, title, description, start_time, end_time, reminder_before_minutes, status) VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, event.getUserId());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
            stmt.setInt(6, event.getReminderBeforeMinutes());
            stmt.setString(7, event.getStatus());

            stmt.executeUpdate();
        }
        System.out.println("Saving event: " + event.getTitle() + ", user_id=" + event.getUserId());

    }
       // Fetch all events
    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY start_time";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Event e = new Event();  // no-arg constructor
            e.setUserId(rs.getInt("user_id"));
            e.setTitle(rs.getString("title"));
            e.setDescription(rs.getString("description"));
            e.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
            e.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
            e.setReminderBeforeMinutes(rs.getInt("reminder_before_minutes"));
            e.setStatus(rs.getString("status"));
            events.add(e);
        }
    }
    return events;
}
// Fetch all events for a specific user
public List<Event> getEventsByUserId(int userId) throws SQLException {
    List<Event> events = new ArrayList<>();
    String sql = "SELECT * FROM events WHERE user_id = ? ORDER BY start_time";

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Event e = new Event();
                e.setUserId(rs.getInt("user_id"));
                e.setTitle(rs.getString("title"));
                e.setDescription(rs.getString("description"));
                e.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                e.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                e.setReminderBeforeMinutes(rs.getInt("reminder_before_minutes"));
                e.setStatus(rs.getString("status"));
                events.add(e);
            }
        }
    }
    return events;
}

    // Fetch upcoming events for a specific user
public List<Event> getUpcomingEvents(int userId, int limit) throws SQLException {
    List<Event> events = new ArrayList<>();
    String sql = "SELECT * FROM events WHERE user_id = ? AND start_time >= NOW() ORDER BY start_time ASC LIMIT ?";

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        stmt.setInt(2, limit);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Event e = new Event();
            e.setUserId(rs.getInt("user_id"));
            e.setTitle(rs.getString("title"));
            e.setDescription(rs.getString("description"));
            e.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
            e.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
            e.setReminderBeforeMinutes(rs.getInt("reminder_before_minutes"));
            e.setStatus(rs.getString("status"));
            events.add(e);
        }
    }
    return events;
}


    // Count total events
    public int countEvents(int userId) throws SQLException {
        return countByCondition(userId, "1=1");
    }

    // Count upcoming events
    public int countUpcomingEvents(int userId) throws SQLException {
        return countByCondition(userId, "start_time > NOW()");
    }

    // Count completed events
    public int countCompletedEvents(int userId) throws SQLException {
        return countByCondition(userId, "status = 'completed'");
    }

    // Helper for counting events
    private int countByCondition(int userId, String condition) throws SQLException {
        String sql = "SELECT COUNT(*) FROM events WHERE user_id = ? AND " + condition;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}
