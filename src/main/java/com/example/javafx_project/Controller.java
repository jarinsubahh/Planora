package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private Label label;

    @FXML
    private TextField usernameField;

    // Handles the "Click Me" button
    @FXML
    private void handleClick() {
        label.setText("Button clicked!");
    }

    // Handles the "Login" button
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        if (username == null || username.isEmpty()) {
            label.setText("Please enter a username.");
        } else {
            label.setText("Welcome, " + username + "!");
        }
    }
}
