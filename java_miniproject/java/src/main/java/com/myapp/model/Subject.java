package com.myapp.model;

import java.time.LocalDateTime;

public class Subject {
    private int id;
    private int userId;
    private String name;
    private String color;
    private LocalDateTime createdAt;
    private String syllabus;

    public Subject() {}

    public Subject(int id, int userId, String name, String color, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.color = color;
        this.createdAt = createdAt;
    }
    public Subject(int id, String name, String color) {
    this.id = id;
    this.name = name;
    this.color = color;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getSyllabus() { return syllabus; }
    public void setSyllabus(String syllabus) { this.syllabus = syllabus; }

    
    @Override
    public String toString() {
        return name;
    }
}