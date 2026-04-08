package com.example.javafx_project;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;

public class Toast {

    public static void show(Stage owner, String message) {
        if (owner == null || message == null || message.isBlank()) {
            return;
        }

        Label label = new Label(message);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        label.setWrapText(true);

        StackPane root = new StackPane(label);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("toast-node");
        root.setStyle(
            "-fx-background-color: rgba(138, 43, 226, 0.7);" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 15 25;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(147, 112, 219, 0.8), 15, 0.5, 0, 0);"
        );
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        URL stylesheet = Toast.class.getResource("/com/example/javafx_project/styles.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        Stage toastStage = new Stage();
        toastStage.initOwner(owner);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.setAlwaysOnTop(true);
        toastStage.setScene(scene);
        toastStage.sizeToScene();

        toastStage.setOnShown(e -> {
            toastStage.setX(owner.getX() + owner.getWidth() / 2 - toastStage.getWidth() / 2);
            toastStage.setY(owner.getY() + owner.getHeight() * 0.8);
        });

        toastStage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(2.5));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition sequence = new SequentialTransition(fadeIn, stay, fadeOut);
        sequence.setOnFinished(event -> toastStage.close());
        sequence.play();
    }
}
