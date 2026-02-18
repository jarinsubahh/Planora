package com.example.javafx_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleBackToHome(ActionEvent event) {
        // This takes you back to the Landing Page
        PlanoraLandingPage landing = new PlanoraLandingPage();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        landing.start(stage);
    }
    // Inside SignInController.java
    @FXML
    private void onLoginButtonClick() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (UserManager.validate(user, pass)) {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();

            Label loggedInLabel = new Label("Logged in");
            StackPane loggedInRoot = new StackPane(loggedInLabel);
            Scene loggedInScene = new Scene(loggedInRoot, 1000, 700);

            Stage loggedInStage = new Stage();
            loggedInStage.setTitle("Planora - Logged In");
            loggedInStage.setScene(loggedInScene);
            loggedInStage.show();

            currentStage.close();
        } else {
            // Show error...
        }
    }

    @FXML
    private void goToSignUp() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("signup-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
