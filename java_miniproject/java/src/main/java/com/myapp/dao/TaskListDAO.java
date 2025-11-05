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
    String sql = "SELECT id, user_id, title, subject_id, created_at FROM task_lists WHERE user_id = ? ORDER BY created_at DESC";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            TaskList list = new TaskList();
            list.setId(rs.getInt("id"));
            list.setUserId(rs.getInt("user_id"));
            list.setTitle(rs.getString("title"));
            list.setSubjectId((Integer) rs.getObject("subject_id"));
            list.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            lists.add(list);
        }
    }
    return lists;
}

    // In TaskDAO.java
public TaskList createTaskList(TaskList list) throws SQLException {
    // ✅ Check if list already exists for this user & scope
    String checkSql;
    if (list.getSubjectId() != null && list.getSubjectId() > 0) {
        checkSql = "SELECT id FROM task_lists WHERE user_id = ? AND subject_id = ? AND title = ?";
    } else {
        checkSql = "SELECT id FROM task_lists WHERE user_id = ? AND subject_id IS NULL AND title = ?";
    }

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

        checkStmt.setInt(1, list.getUserId());
        if (list.getSubjectId() != null && list.getSubjectId() > 0) {
            checkStmt.setInt(2, list.getSubjectId());
            checkStmt.setString(3, list.getTitle());
        } else {
            checkStmt.setString(2, list.getTitle());
        }

        ResultSet existing = checkStmt.executeQuery();
        if (existing.next()) {
            // ✅ Return the existing list instead of creating a duplicate
            list.setId(existing.getInt("id"));
            System.out.println("⚠️ List already exists, skipping duplicate: " + list.getTitle());
            return list;
        }
    }

    // ✅ Proceed to create only if not existing
    String insertSql;
    if (list.getSubjectId() != null && list.getSubjectId() > 0) {
        insertSql = "INSERT INTO task_lists (user_id, subject_id, title) VALUES (?, ?, ?) RETURNING id, created_at";
    } else {
        insertSql = "INSERT INTO task_lists (user_id, title) VALUES (?, ?) RETURNING id, created_at";
    }

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(insertSql)) {

        stmt.setInt(1, list.getUserId());
        if (list.getSubjectId() != null && list.getSubjectId() > 0) {
            stmt.setInt(2, list.getSubjectId());
            stmt.setString(3, list.getTitle());
        } else {
            stmt.setString(2, list.getTitle());
        }

        ResultSet rs = stmt.executeQuery();
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
    if (list.getSubjectId() != null && list.getSubjectId() > 0) {
    sql = "INSERT INTO task_lists (user_id, subject_id, title) VALUES (?, ?, ?) RETURNING id, created_at";
} else {
    sql = "INSERT INTO task_lists (user_id, title) VALUES (?, ?) RETURNING id, created_at";
}
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, list.getUserId());
        if (list.getSubjectId() != null && list.getSubjectId() > 0) {
            stmt.setInt(2, list.getSubjectId());
            stmt.setString(3, list.getTitle());
        } else {
            stmt.setString(2, list.getTitle());
        }
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            list.setId(rs.getInt("id"));
            list.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
    }
    return list;
}

public boolean deleteTaskList(int listId, int userId) throws SQLException {
    String sql = "DELETE FROM task_lists WHERE id = ? AND user_id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, listId);
        ps.setInt(2, userId);
        int rows = ps.executeUpdate();
        return rows > 0; // ✅ true if deleted
    }
}

}