package com.example.javafx_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class SignInController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleBackToHome(ActionEvent event) {
        PlanoraLandingPage landing = new PlanoraLandingPage();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        landing.start(stage);
    }


    @FXML
    private void onLoginButtonClick() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            System.err.println("Username or password is empty");
            return;
        }

        if (UserManager.validate(user, pass)) {
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/dashboard-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
                stage.setScene(scene);
            } catch (Exception e) {
                System.err.println("Error loading dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Invalid username or password");
        }
    }

    @FXML
    private void goToSignUp() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/example/javafx_project/signup-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}