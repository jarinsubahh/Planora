package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class SignUpController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Label errorLabel;

    /** Basic email shape: local@domain.tld (domain must contain a dot after @). */
    private static boolean isValidEmailFormat(String email) {
        if (email == null) {
            return false;
        }
        String trimmed = email.trim();
        int at = trimmed.indexOf('@');
        if (at <= 0 || at == trimmed.length() - 1) {
            return false;
        }
        String domain = trimmed.substring(at + 1);
        int dot = domain.lastIndexOf('.');
        return dot > 0 && dot < domain.length() - 1;
    }

    @FXML
    private void handleSignUp() {
        String user = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (user.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            errorLabel.setVisible(true);
            return;
        }
        if (!isValidEmailFormat(email)) {
            errorLabel.setText("Please enter a valid email address.");
            errorLabel.setVisible(true);
            return;
        }
        if (!pass.equals(confirm)) {
            errorLabel.setText("Check your passwords and try again.");
            errorLabel.setVisible(true);
            return;
        }

        UserManager.RegistrationResult result = UserManager.register(user, pass, email);
        switch (result) {
            case SUCCESS -> {
                errorLabel.setVisible(false);
                showSignUpSuccessPopup();
            }
            case USERNAME_TAKEN -> {
                errorLabel.setText("Username already exists.");
                errorLabel.setVisible(true);
            }
            case DATABASE_ERROR -> {
                errorLabel.setText("Could not save your account. Please try again.");
                errorLabel.setVisible(true);
            }
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
            stage.setScene(new Scene(loader.load(), 1000, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
