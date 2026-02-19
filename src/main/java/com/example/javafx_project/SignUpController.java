package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class SignUpController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleSignUp() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        // Validation
        if (user.isEmpty() || pass.isEmpty() || !pass.equals(confirmPasswordField.getText())) {
            errorLabel.setText("Check your passwords and try again.");
            errorLabel.setVisible(true);
            return;
        }

        // Register user
        if (UserManager.register(user, pass)) {
            errorLabel.setVisible(false);
            showSignUpSuccessPopup();
        } else {
            errorLabel.setText("Username already exists.");
            errorLabel.setVisible(true);
        }
    }

    private void showSignUpSuccessPopup() {
        ButtonType goToSignInButton = new ButtonType("Go to Sign In", ButtonBar.ButtonData.OK_DONE);
        ButtonType stayHereButton = new ButtonType("Stay Here", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Account Created");
        alert.setHeaderText("Congratulations! You're all set up!");
        alert.setContentText("Your account was created successfully.");
        alert.getButtonTypes().setAll(goToSignInButton, stayHereButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == goToSignInButton) {
            goToSignIn();
        }
    }

    @FXML
    private void goToSignIn() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/javafx_project/signin-view.fxml")
            );
            stage.setScene(new Scene(loader.load(), 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
