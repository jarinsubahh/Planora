package com.example.javafx_project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

public class Task implements Serializable {
    private int id;
    private String userId;
    private String title;
    private String description;
    private LocalDate deadline;
    private String category;
    private String priority;
    private boolean completed;
    private LocalDateTime createdAt;

    public Task() {}

    public Task(int id, String userId, String title, String description, LocalDate deadline,
                String category, String priority, boolean completed, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.category = category;
        this.priority = priority;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    public Task(String title, String description, String priority, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.completed = false; // New tasks are not completed by default
        this.id = (int) (System.currentTimeMillis() % 1000000000L);
        this.createdAt = java.time.LocalDateTime.now();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}