package com.myapp.model;

import java.time.LocalDateTime;

public class TaskList {
    private int id;
    private int userId;
    private String title;
    private Integer subjectId;
    private String description;
    private LocalDateTime createdAt;

    public TaskList() {}

    public TaskList(int id, String title, Integer subjectId) {
        this.id = id;
        this.title = title;
        this.subjectId = subjectId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
