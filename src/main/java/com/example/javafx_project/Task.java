package com.example.javafx_project;

import java.io.Serializable;
import java.time.LocalDate;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private LocalDate deadline;
    private String category;
    private String priority;
    private boolean completed;

    public Task(String title, String description, LocalDate deadline,
                String category, String priority) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.category = category;
        this.priority = priority;
        this.completed = false;
    }

    // Getters & Setters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDeadline() { return deadline; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }
}
