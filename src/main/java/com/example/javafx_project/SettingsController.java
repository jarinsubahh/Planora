package com.example.javafx_project;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bson.Document;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;

public class SettingsController {

    private Runnable onBack;

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private static final DateTimeFormatter MEMBER_SINCE_FORMAT =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.ENGLISH);

    @FXML
    private BorderPane rootPane;

    @FXML
    private Button backButton;

    @FXML
    private VBox contentVBox;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label memberSinceLabel;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label passwordMessageLabel;

    @FXML
    private Button updatePasswordButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private void initialize() {
        loadAccountInfo();
        playEnterFade();
    }

    private void playEnterFade() {
        if (rootPane == null) {
            return;
        }
        Platform.runLater(() -> {
            FadeTransition ft = new FadeTransition(Duration.millis(420), rootPane);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        });
    }

    private void loadAccountInfo() {
        String user = UserManager.currentUser;
        if (user == null) {
            usernameLabel.setText("—");
            emailLabel.setText("—");
            memberSinceLabel.setText("—");
            return;
        }
        usernameLabel.setText(user);
        Document doc = UserManager.getUserDocument(user);
        if (doc == null) {
            emailLabel.setText("—");
            memberSinceLabel.setText("—");
            return;
        }
        String email = doc.getString("email");
        emailLabel.setText(email != null && !email.isBlank() ? email : "—");

        String created = doc.getString("created_at");
        if (created == null || created.isBlank()) {
            memberSinceLabel.setText("—");
        } else {
            try {
                Instant instant = Instant.parse(created);
                memberSinceLabel.setText(MEMBER_SINCE_FORMAT.format(instant.atZone(ZoneId.systemDefault())));
            } catch (Exception e) {
                memberSinceLabel.setText(created);
            }
        }
    }

    @FXML
    private void handleBack() {
        if (onBack != null) {
            onBack.run();
            return;
        }
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/dashboard-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdatePassword() {
        hidePasswordMessage();
        String user = UserManager.currentUser;
        if (user == null) {
            showPasswordError("You are not signed in.");
            return;
        }
        String current = currentPasswordField.getText();
        String next = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isEmpty() || next.isEmpty() || confirm.isEmpty()) {
            showPasswordError("Please fill in all password fields.");
            return;
        }
        if (!next.equals(confirm)) {
            showPasswordError("New password and confirmation do not match.");
            return;
        }
        if (next.equals(current)) {
            showPasswordError("New password must be different from your current password.");
            return;
        }

        boolean ok = UserManager.updatePassword(user, current, next);
        if (ok) {
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            showPasswordSuccess("Password updated.");
        } else {
            showPasswordError("Current password is incorrect or could not be updated.");
        }
    }

    private void hidePasswordMessage() {
        passwordMessageLabel.setVisible(false);
        passwordMessageLabel.setManaged(false);
        passwordMessageLabel.setText("");
        passwordMessageLabel.getStyleClass().removeAll("hint-label");
        if (!passwordMessageLabel.getStyleClass().contains("error-label")) {
            passwordMessageLabel.getStyleClass().add("error-label");
        }
    }

    private void showPasswordError(String msg) {
        passwordMessageLabel.setText(msg);
        passwordMessageLabel.getStyleClass().removeAll("hint-label");
        if (!passwordMessageLabel.getStyleClass().contains("error-label")) {
            passwordMessageLabel.getStyleClass().add("error-label");
        }
        passwordMessageLabel.setVisible(true);
        passwordMessageLabel.setManaged(true);
    }

    private void showPasswordSuccess(String msg) {
        passwordMessageLabel.setText(msg);
        passwordMessageLabel.getStyleClass().removeAll("error-label");
        if (!passwordMessageLabel.getStyleClass().contains("hint-label")) {
            passwordMessageLabel.getStyleClass().add("hint-label");
        }
        passwordMessageLabel.setVisible(true);
        passwordMessageLabel.setManaged(true);
    }

    @FXML
    private void handleDeleteAccountClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete account");
        alert.setHeaderText(null);
        alert.setContentText(
                "Are you sure you want to permanently delete your account?\nThis action cannot be undone.");
        ButtonType cancel = new ButtonType("Cancel");
        ButtonType confirm = new ButtonType("Confirm Delete");
        alert.getButtonTypes().setAll(cancel, confirm);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != confirm) {
            return;
        }

        String user = UserManager.currentUser;
        if (user == null) {
            goToLogin();
            return;
        }

        boolean deleted = UserManager.deleteAccount(user);
        if (deleted) {
            goToLogin();
        } else {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Error");
            err.setHeaderText(null);
            err.setContentText("Could not delete your account. Please try again.");
            err.showAndWait();
        }
    }

    private void goToLogin() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/signin-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
