package com.myapp.dao;

import com.myapp.db.DatabaseConnection;
import com.myapp.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    // --- Save new event ---
    public void save(Event event) throws SQLException {
        String sql = """
                INSERT INTO events(user_id, title, description, start_time, end_time, reminder_before_minutes, original_start_time, subject_id)
                VALUES (?,?,?,?,?,?,?,?)
                """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, event.getUserId());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
            stmt.setInt(6, event.getReminderBeforeMinutes() != 0 ? event.getReminderBeforeMinutes() : 24);
            stmt.setTimestamp(7, event.getOriginalStartTime() != null ? Timestamp.valueOf(event.getOriginalStartTime()) : null);
            
            if (event.getSubjectId() != null)
                stmt.setInt(8, event.getSubjectId());
            else
                stmt.setNull(8, java.sql.Types.INTEGER);
            
            
            stmt.executeUpdate();

            // Set the generated ID in Event object
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) event.setId(rs.getInt(1));
            }
        }
    }

    // --- Update existing event ---
    public void update(Event event) throws SQLException {
        String sql = "UPDATE events SET title = ?, description = ?, start_time = ?, end_time = ?, reminder_before_minutes = ?, original_start_time = ? , subject_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(event.getEndTime()));
            stmt.setInt(5, event.getReminderBeforeMinutes());
            stmt.setTimestamp(6, event.getOriginalStartTime() != null ? Timestamp.valueOf(event.getOriginalStartTime()) : null);

            if (event.getSubjectId() != null)
                stmt.setInt(7, event.getSubjectId());
            else 
                stmt.setNull(7, java.sql.Types.INTEGER);
            
            stmt.setInt(8, event.getId());
            stmt.executeUpdate();
        }
    }

    // --- Delete event by ID ---
    public void delete(int eventId) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        }
    }

    // --- Get all events for a user ---
    public List<Event> getEventsByUserId(int userId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = """
                SELECT e.*, s.name AS subject_name, s.color AS subject_color
                FROM events e
                LEFT JOIN SUBJECTS s ON e.subject_id = s.id
                WHERE e.user_id = ?
                ORDER BY e.start_time
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Event e = new Event();
                    e.setId(rs.getInt("id"));
                    e.setUserId(rs.getInt("user_id"));
                    e.setTitle(rs.getString("title"));
                    e.setDescription(rs.getString("description"));
                    e.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                    e.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                    e.setReminderBeforeMinutes(rs.getInt("reminder_before_minutes"));
                    e.setOriginalStartTime(rs.getTimestamp("original_start_time") != null ?
                        rs.getTimestamp("original_start_time").toLocalDateTime() : null);
                    Object subjId = rs.getObject("subject_id");
                    e.setSubjectId(subjId != null ? (Integer) subjId : null);
                    e.setSubjectName(rs.getString("subject_name"));
                    e.setSubjectColor(rs.getString("subject_color"));
                    events.add(e);
                }
            }
        }
        return events;
    }

    // --- Count all events for a user ---
    public int countEvents(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM events WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // --- Count upcoming events for a user ---
    public int countUpcomingEvents(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM events WHERE user_id = ? AND start_time > NOW()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // --- Get upcoming events (limit n) ---
    public List<Event> getUpcomingEvents(int userId, int limit) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = """
                SELECT e.*, s.name AS subject_name, s.color AS subject_color
                FROM events e
                LEFT JOIN subjects s ON e.subject_id = s.id
                WHERE e.user_id = ? AND e.start_time > NOW()
                ORDER BY e.start_time ASC
                LIMIT ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Event e = new Event();
                    e.setId(rs.getInt("id"));
                    e.setUserId(rs.getInt("user_id"));
                    e.setTitle(rs.getString("title"));
                    e.setDescription(rs.getString("description"));
                    e.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                    e.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                    e.setReminderBeforeMinutes(rs.getInt("reminder_before_minutes"));
                    e.setOriginalStartTime(rs.getTimestamp("original_start_time") != null ?
                            rs.getTimestamp("original_start_time").toLocalDateTime() : null);
                    Object subjId = rs.getObject("subject_id");
                    e.setSubjectId(subjId != null ? (Integer) subjId : null) ;
                    e.setSubjectName(rs.getString("subject_name"));
                    e.setSubjectColor(rs.getString("subject_color"));
                    events.add(e);
                }
            }
        }
        return events;
    }
}
