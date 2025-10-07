package com.myapp.model;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private int userId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int reminderBeforeMinutes;
    private LocalDateTime originalStartTime;  
    private LocalDateTime createdAt;

    // Constructor
    public Event() {
        this.userId = 0;
        this.title = "";
        this.description = "";
        this.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now().plusHours(1);
        this.reminderBeforeMinutes = 24;
        this.originalStartTime = this.startTime;
        this.createdAt = LocalDateTime.now();
    }
    

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getReminderBeforeMinutes() { return reminderBeforeMinutes; }
    public LocalDateTime getOriginalStartTime() { return originalStartTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }


        // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setReminderBeforeMinutes(int reminderBeforeMinutes) { this.reminderBeforeMinutes = reminderBeforeMinutes; }
    public void setOriginalStartTime(LocalDateTime originalStartTime) { this.originalStartTime = originalStartTime; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

