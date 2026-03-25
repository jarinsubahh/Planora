package com.example.javafx_project;

import java.io.Serializable;

public class Invitation implements Serializable {
    public String spaceName;
    public String recipientUsername;
    public String senderUsername;

    public Invitation(String spaceName, String recipient, String sender) {
        this.spaceName = spaceName;
        this.recipientUsername = recipient;
        this.senderUsername = sender;
    }
}