package com.myapp.model;

import java.time.LocalDateTime;

public class Event {
    private int userId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int reminderBeforeMinutes;
    private String status;

    // Constructor
    public Event() {
        this.userId = 0;
        this.title = "";
        this.description = "";
        this.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now().plusHours(1);
        this.reminderBeforeMinutes = 10;
        this.status = "scheduled";
    }
    

    // Getters
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getReminderBeforeMinutes() { return reminderBeforeMinutes; }
    public String getStatus() { return status; }

        // --- Setters ---
    public void setUserId(int userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setReminderBeforeMinutes(int reminderBeforeMinutes) { this.reminderBeforeMinutes = reminderBeforeMinutes; }
    public void setStatus(String status) { this.status = status; }
}

