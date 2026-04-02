package com.example.javafx_project;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Invitation implements Serializable {
    public String spaceName;
    public String recipientUsername;
    public String senderUsername;
    public String status; // "pending", "accepted", "declined"
    public String createdAt;
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Invitation(String spaceName, String recipient, String sender) {
        this.spaceName = spaceName;
        this.recipientUsername = recipient;
        this.senderUsername = sender;
        this.status = "pending";
        this.createdAt = LocalDateTime.now().format(DATETIME_FORMAT);
    }

    public Invitation(String spaceName, String recipient, String sender, String status, String createdAt) {
        this.spaceName = spaceName;
        this.recipientUsername = recipient;
        this.senderUsername = sender;
        this.status = status;
        this.createdAt = createdAt;
    }
}