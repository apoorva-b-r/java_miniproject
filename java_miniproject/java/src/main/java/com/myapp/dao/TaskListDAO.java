package com.myapp.dao;

import com.myapp.model.TaskList;
import com.myapp.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskListDAO {

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
}