package com.example.javafx_project;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TimerCompletionDialog {


    public static Stage show(Stage owner, Runnable onYes, Runnable onNo) {
        // Main container
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #F3F0FF, #EDE9FE);" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #DDD6FE;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 20;"
        );

        // Main title
        Label titleLabel = new Label("Time Ended");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #6D6AAE;"
        );
        titleLabel.setAlignment(Pos.CENTER);

        Label questionLabel = new Label("Have you finished your work?");
        questionLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: #4C4A7A;" +
            "-fx-font-weight: 600;"
        );
        questionLabel.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button yesButton = createStyledButton("Yes", true);
        yesButton.setStyle(
            "-fx-background-color: #C4B5FD;" +
            "-fx-text-fill: #2D1B4E;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: 10;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 700;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 120px;" +
            "-fx-effect: dropshadow(gaussian, rgba(122, 115, 255, 0.4), 10, 0, 0, 2);"
        );

        Button noButton = createStyledButton("No", false);
        noButton.setStyle(
            "-fx-background-color: #E9D5FF;" +
            "-fx-text-fill: #6D6AAE;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: 10;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 700;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 120px;" +
            "-fx-effect: dropshadow(gaussian, rgba(168, 85, 247, 0.3), 10, 0, 0, 2);"
        );

        buttonBox.getChildren().addAll(yesButton, noButton);

        root.getChildren().addAll(titleLabel, questionLabel, buttonBox);

        Scene dialogScene = new Scene(root, 450, 250);
        dialogScene.setFill(Color.TRANSPARENT);

        Stage dialogStage = new Stage(StageStyle.TRANSPARENT);
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(dialogScene);
        dialogStage.setTitle("Focus Session Complete");

        // Button actions
        yesButton.setOnAction(e -> {
            dialogStage.close();
            if (onYes != null) onYes.run();
        });

        noButton.setOnAction(e -> {
            dialogStage.close();
            if (onNo != null) onNo.run();
        });

        dialogStage.setOnShown(e -> {
            dialogStage.setX(owner.getX() + (owner.getWidth() - dialogStage.getWidth()) / 2);
            dialogStage.setY(owner.getY() + (owner.getHeight() - dialogStage.getHeight()) / 2);
        });

        return dialogStage;
    }

    private static Button createStyledButton(String text, boolean isPrimary) {
        Button button = new Button(text);
        button.setPrefHeight(48);

        // Hover effect
        button.setOnMouseEntered(e -> {
            if (isPrimary) {
                button.setStyle(
                    "-fx-background-color: #B8A4F3;" +
                    "-fx-text-fill: #2D1B4E;" +
                    "-fx-padding: 12 28 12 28;" +
                    "-fx-background-radius: 10;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: 700;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 120px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(122, 115, 255, 0.6), 14, 0, 0, 3);"
                );
            } else {
                button.setStyle(
                    "-fx-background-color: #DDD6FE;" +
                    "-fx-text-fill: #4C4A7A;" +
                    "-fx-padding: 12 28 12 28;" +
                    "-fx-background-radius: 10;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: 700;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 120px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(168, 85, 247, 0.5), 14, 0, 0, 3);"
                );
            }
        });

        // Exit hover
        button.setOnMouseExited(e -> {
            if (isPrimary) {
                button.setStyle(
                    "-fx-background-color: #C4B5FD;" +
                    "-fx-text-fill: #2D1B4E;" +
                    "-fx-padding: 12 28 12 28;" +
                    "-fx-background-radius: 10;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: 700;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 120px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(122, 115, 255, 0.4), 10, 0, 0, 2);"
                );
            } else {
                button.setStyle(
                    "-fx-background-color: #E9D5FF;" +
                    "-fx-text-fill: #6D6AAE;" +
                    "-fx-padding: 12 28 12 28;" +
                    "-fx-background-radius: 10;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: 700;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 120px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(168, 85, 247, 0.3), 10, 0, 0, 2);"
                );
            }
        });

        return button;
    }
}
