package com.myapp.dao;

import com.myapp.db.DatabaseConnection;
import com.myapp.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public List<Subject> getSubjectsByUserId(int userId) throws SQLException{
        return getSubjectsByUser(userId);
    }

    public void createSubject(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects (user_id, name, color, syllabus) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, subject.getUserId());
            stmt.setString(2, subject.getName());
            stmt.setString(3, subject.getColor());
            stmt.setString(4, subject.getSyllabus());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) subject.setId(rs.getInt(1));
            }
        }
    }

    public List<Subject> getSubjectsByUser(int userId) throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT id, user_id, name, color, syllabus, created_at FROM subjects WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Subject s = new Subject();
                    s.setId(rs.getInt("id"));
                    s.setUserId(rs.getInt("user_id"));
                    s.setName(rs.getString("name"));
                    s.setColor(rs.getString("color"));
                    s.setSyllabus(rs.getString("syllabus"));
                    s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    subjects.add(s);
                }
            }
        }

        return subjects;
    }

    public void deleteSubject(int subjectId) throws SQLException {
        String sql = "DELETE FROM subjects WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            stmt.executeUpdate();
        }
    }

    public void updateSubject(Subject subject) throws SQLException {
        String sql = "UPDATE subjects SET name = ?, color = ?, syllabus = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getColor());
            stmt.setString(3, subject.getSyllabus());
            stmt.setInt(4, subject.getId());
            stmt.executeUpdate();
        }
    }

    public Subject getSubjectById(int id) throws SQLException {
        String sql = "SELECT id, user_id, name, color, syllabus, created_at FROM subjects WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Subject s = new Subject();
                    s.setId(rs.getInt("id"));
                    s.setUserId(rs.getInt("user_id"));
                    s.setName(rs.getString("name"));
                    s.setColor(rs.getString("color"));
                    s.setSyllabus(rs.getString("syllabus"));
                    s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return s;
                }
            }
        }
        return null;
    }
}
