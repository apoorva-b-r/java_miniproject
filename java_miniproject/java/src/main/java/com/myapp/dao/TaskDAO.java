package com.myapp.dao;

import com.myapp.model.Task;
import com.myapp.model.TaskList;
import com.myapp.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

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

    // Save a task
    public Task saveTask(Task task) throws SQLException {
        String sql = "INSERT INTO tasks(list_id, title, description, status) VALUES (?, ?, ?, ?) RETURNING id, created_at";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, task.getListId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getStatus());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                task.setId(rs.getInt("id"));
                task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
        }
        return task;
    }

    // Get all tasks for a list
    public List<Task> getTasksByListId(int listId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, status, created_at FROM tasks WHERE list_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, listId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Task t = new Task();
                t.setId(rs.getInt("id"));
                t.setListId(listId);
                t.setTitle(rs.getString("title"));
                t.setDescription(rs.getString("description"));
                t.setStatus(rs.getString("status"));
                t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                tasks.add(t);
            }
        }
        return tasks;
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

    // Create a new task in the database
    public void createTask(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (list_id, title, description, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, task.getListId());
            stmt.setString(2, task.getTitle());
            stmt.setString(3, task.getDescription());
            stmt.setString(4, task.getStatus());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // ✅ Updates only the status (completed/scheduled)
public void updateTaskStatus(Task task) throws Exception {
    String sql = "UPDATE tasks SET status = ? WHERE id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, task.getStatus());
        stmt.setInt(2, task.getId());
        stmt.executeUpdate();
    }
}


    // update full task (title, description, status)
    public boolean updateTask(Task task) throws SQLException {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus());
            stmt.setInt(4, task.getId());
            int rows = stmt.executeUpdate();
            System.out.println("TaskDAO.updateTask rows=" + rows + " for id=" + task.getId());
            return rows > 0;
        }
    }

    // delete task
    public boolean deleteTask(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            System.out.println("TaskDAO.deleteTask rows=" + rows + " for id=" + id);
            return rows > 0;
        }
    }


}
