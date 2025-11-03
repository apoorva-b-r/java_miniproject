package com.myapp.model;

import java.time.LocalDateTime;

public class Task {
    private int id;
    private int userId;
    private int listId;
    private String title;
    private String description;
    private String status;
    private boolean isDone;
    private LocalDateTime createdAt;

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; } // ✅ add getter
    public void setStatus(String status) { this.status = status; } // ✅ add setter

    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
