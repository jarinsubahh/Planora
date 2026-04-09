package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class ResetPasswordController {
    @FXML private Label userEmailLabel;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    
    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
        userEmailLabel.setText("Reset password for: " + email);
    }

    @FXML
    private void initialize() {
        messageLabel.setVisible(false);
    }

    @FXML
    private void handleResetPassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill in all fields", "error");
            return;
        }

        if (newPassword.length() < 6) {
            showMessage("Password must be at least 6 characters", "error");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showMessage("Passwords do not match", "error");
            return;
        }

        boolean success = UserManager.updatePassword(userEmail, newPassword);
        
        if (success) {
            showMessage("Password reset successfully! Redirecting to sign in...", "success");
            
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::goToSignIn);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showMessage("Failed to reset password. Please try again.", "error");
        }
    }

    @FXML
    private void handleBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/javafx_project/signin-view.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            
            Stage stage = (Stage) userEmailLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error redirecting to sign in", "error");
        }
    }

    private void goToSignIn() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/javafx_project/signin-view.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            
            Stage stage = (Stage) userEmailLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error redirecting to sign in", "error");
        }
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setStyle(type.equals("error") ? 
            "-fx-text-fill: #FF6B6B;" : 
            "-fx-text-fill: #51CF66;");
    }
}
