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

//    @FXML
//    private void onLoginButtonClick() {
//        String user = usernameField.getText();
//        String pass = passwordField.getText();
//
//        if (UserManager.validate(user, pass)) {
//            Stage currentStage = (Stage) usernameField.getScene().getWindow();
//
//            Label loggedInLabel = new Label("Logged in");
//            StackPane loggedInRoot = new StackPane(loggedInLabel);
//            Scene loggedInScene = new Scene(loggedInRoot, 800, 600);
//
//            Stage loggedInStage = new Stage();
//            loggedInStage.setTitle("Planora - Logged In");
//            loggedInStage.setScene(loggedInScene);
//            loggedInStage.show();
//
//            currentStage.close();
//        } else {
//            // TODO: Show error message
//        }
//    }
@FXML
private void onLoginButtonClick() {
    String user = usernameField.getText();
    String pass = passwordField.getText();

    if (UserManager.validate(user, pass)) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Load your existing Dashboard
            PlanoraDashboard dashboard = new PlanoraDashboard();
            dashboard.start(stage);  // reuse the same stage

        } catch (Exception e) {
            e.printStackTrace();
        }
    } else {
        // TODO: Show error message
    }
}

    @FXML
    private void goToSignUp() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/example/javafx_project/signup-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
