package com.myapp.dao;

import com.myapp.model.TaskList;
import com.myapp.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskListDAO {
        // Save a new list
    public TaskList saveList(TaskList list) throws SQLException {
        String sql = "INSERT INTO task_lists(user_id, title) VALUES (?, ?) RETURNING id, created_at";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, list.getUserId());
            ps.setString(2, list.getTitle());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                list.setId(rs.getInt("id"));
                list.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
        }
        return list;
    }

    // Load all lists for a user
    public List<TaskList> getListsByUser(int userId) throws SQLException {
        List<TaskList> lists = new ArrayList<>();
        String sql = "SELECT id, title, created_at FROM task_lists WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TaskList l = new TaskList();
                l.setId(rs.getInt("id"));
                l.setUserId(userId);
                l.setTitle(rs.getString("title"));
                l.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                lists.add(l);
            }
        }
        return lists;
    }

    // In TaskDAO.java
public TaskList createTaskList(TaskList list) throws SQLException {
    String sql = "INSERT INTO task_lists (user_id, title) VALUES (?, ?) RETURNING id, created_at";
    try (var conn = DatabaseConnection.getConnection();
        var stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, list.getUserId());
        stmt.setString(2, list.getTitle());

        var rs = stmt.executeQuery();
        if (rs.next()) {
            list.setId(rs.getInt("id"));
            list.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return list;
    }
}

    public void save(TaskList list) throws SQLException {
        String sql = "INSERT INTO task_lists (user_id, title) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, list.getUserId());
            ps.setString(2, list.getTitle());
            ps.executeUpdate();
        }
    }

    public List<TaskList> getListsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM task_lists WHERE user_id = ? ORDER BY created_at DESC";
        List<TaskList> lists = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TaskList list = new TaskList();
                list.setId(rs.getInt("id"));
                list.setUserId(rs.getInt("user_id"));
                list.setTitle(rs.getString("title"));
                list.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                lists.add(list);
            }
        }

        return lists;
    }

    // ✅ Fetch all task lists for a specific subject
public List<TaskList> getListsBySubject(int userId, int subjectId) throws SQLException {
    List<TaskList> lists = new ArrayList<>();
    String sql = "SELECT id, title, created_at FROM task_lists WHERE user_id = ? AND subject_id = ? ORDER BY created_at DESC";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ps.setInt(2, subjectId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            TaskList list = new TaskList();
            list.setId(rs.getInt("id"));
            list.setUserId(userId);
            list.setSubjectId(subjectId);
            list.setTitle(rs.getString("title"));
            list.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            lists.add(list);
        }
    }
    return lists;
}

// ✅ Create a new task list tied to a subject
public TaskList createTaskListForSubject(TaskList list) throws SQLException {
    String sql = "INSERT INTO task_lists (user_id, subject_id, title) VALUES (?, ?, ?) RETURNING id, created_at";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, list.getUserId());
        stmt.setInt(2, list.getSubjectId());
        stmt.setString(3, list.getTitle());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            list.setId(rs.getInt("id"));
            list.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
    }
    return list;
}
}