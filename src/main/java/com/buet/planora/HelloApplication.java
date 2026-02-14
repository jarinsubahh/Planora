package com.buet.planora;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the homepage FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home-view.fxml"));

        // Create the scene with your chosen size
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        // Attach the CSS stylesheet
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // Configure the stage (window)
        stage.setTitle("Planora");
        stage.initStyle(StageStyle.DECORATED); // modern decorated window
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
