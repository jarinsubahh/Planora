package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Simple forgot password controller with basic code generation and sending
 */
public class ForgotPasswordController {
    @FXML private TextField emailField;
    @FXML private TextField codeInputField;
    @FXML private Label messageLabel;
    @FXML private Button sendCodeBtn;
    @FXML private Label codeDisplayLabel;
    @FXML private VBox codeSection;
    
    private String generatedCode;
    private String resetEmail;

    @FXML
    private void initialize() {
        codeSection.setVisible(false);
        messageLabel.setVisible(false);
    }

    @FXML
    private void handleSendCode() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            showMessage("Please enter your email address", "error");
            return;
        }
        
        if (!isValidEmail(email)) {
            showMessage("Please enter a valid email address", "error");
            return;
        }
        
        // Check if email exists in system
        if (!UserManager.emailExists(email)) {
            showMessage("No account found with this email", "error");
            return;
        }
        
        // Generate code
        generatedCode = generateResetCode();
        resetEmail = email;
        
        // Send code (basic implementation - just display it)
        sendCodeToEmail(email, generatedCode);
        
        // Show code section
        codeSection.setVisible(true);
        codeDisplayLabel.setText("Your reset code: " + generatedCode);
        showMessage("Code sent to " + email, "success");
        
        // Disable email field and send button
        emailField.setDisable(true);
        sendCodeBtn.setDisable(true);
    }

    @FXML
    private void handleVerifyCode() {
        String enteredCode = codeInputField.getText();
        
        if (enteredCode.isEmpty()) {
            showMessage("Please enter the code", "error");
            return;
        }
        
        if (enteredCode.equals(generatedCode)) {
            showMessage("Code verified! Redirecting to reset password...", "success");
            // Small delay then navigate
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::goToResetPassword);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showMessage("Invalid code. Please try again.", "error");
        }
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/example/javafx_project/signin-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            stage.setScene(scene);
            configureStage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate a simple 6-digit reset code
     */
    private String generateResetCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    /**
     * Basic email sending (currently just logs and displays)
     * In production, this would integrate with an email service like SendGrid, Gmail API, etc.
     */
    private void sendCodeToEmail(String email, String code) {
        // Log to console (in production, use email service)
        System.out.println("═══════════════════════════════════════");
        System.out.println("PASSWORD RESET CODE");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Email: " + email);
        System.out.println("Code: " + code);
        System.out.println("═══════════════════════════════════════");
        
        // TODO: For production, integrate with:
        // - SendGrid API
        // - Gmail SMTP
        // - AWS SES
        // - Firebase Cloud Messaging
        // Example with JavaMail:
        /*
        try {
            SendEmailUtil.sendResetCode(email, code);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
        */
    }

    /**
     * Navigate to password reset window
     */
    private void goToResetPassword() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/javafx_project/reset-password.fxml"));
            javafx.scene.Parent root = loader.load();
            
            ResetPasswordController controller = loader.getController();
            controller.setUserEmail(resetEmail);
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 1200, 700);
            stage.setScene(scene);
            configureStage(stage);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error redirecting to reset password", "error");
        }
    }

    private void configureStage(Stage stage) {
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.setWidth(1200);
        stage.setHeight(700);
        stage.centerOnScreen();
    }

    /**
     * Basic email validation
     */
    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailPattern);
    }

    /**
     * Display message to user
     */
    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setStyle(type.equals("error") ? 
            "-fx-text-fill: #FF6B6B;" : 
            "-fx-text-fill: #51CF66;");
    }
}
