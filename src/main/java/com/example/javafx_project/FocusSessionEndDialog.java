package com.example.javafx_project;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Polished post-session dialog with lavender aesthetic.
 * Displays when focus timer completes, asking if work was finished.
 */
public class FocusSessionEndDialog {

    /**
     * Shows the session end dialog with lavender aesthetic.
     * 
     * @param owner Parent stage
     * @param onYes Callback when Yes is clicked
     * @param onNo Callback when No is clicked
     * @return The dialog Stage
     */
    public static Stage show(Stage owner, Runnable onYes, Runnable onNo) {
        // Root container with glassmorphism effect
        StackPane dialogRoot = new StackPane();
        
        // Background blur/overlay effect
        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.color(0, 0, 0, 0.3));
        overlay.widthProperty().bind(dialogRoot.widthProperty());
        overlay.heightProperty().bind(dialogRoot.heightProperty());
        dialogRoot.getChildren().add(overlay);

        // Main dialog content VBox
        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(40));
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle(
            "-fx-background-color: linear-gradient(135deg, #C8A2FF 0%, #E6D6FF 100%);" +
            "-fx-background-radius: 24;" +
            "-fx-border-color: rgba(230, 214, 255, 0.5);" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 24;" +
            "-fx-effect: dropshadow(gaussian, rgba(168, 85, 247, 0.4), 20, 0, 0, 8);"
        );
        contentBox.setPrefWidth(480);
        contentBox.setPrefHeight(280);

        // Title
        Label titleLabel = new Label("Time Ended");
        titleLabel.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        titleLabel.setAlignment(Pos.CENTER);

        // Question
        Label questionLabel = new Label("Have you finished your work?");
        questionLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: rgba(255, 255, 255, 0.95);" +
            "-fx-font-weight: 500;"
        );
        questionLabel.setAlignment(Pos.CENTER);
        questionLabel.setWrapText(true);

        // Buttons container
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(8, 0, 0, 0));

        // Yes Button - Primary (solid lavender)
        Button yesButton = new Button("Yes");
        styleYesButton(yesButton);
        yesButton.setMinWidth(120);
        yesButton.setPrefHeight(48);

        // No Button - Secondary (outline style)
        Button noButton = new Button("No");
        styleNoButton(noButton);
        noButton.setMinWidth(120);
        noButton.setPrefHeight(48);

        buttonBox.getChildren().addAll(yesButton, noButton);

        contentBox.getChildren().addAll(titleLabel, questionLabel, buttonBox);
        dialogRoot.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        // Create scene and stage
        Scene dialogScene = new Scene(dialogRoot, 600, 400);
        dialogScene.setFill(Color.TRANSPARENT);

        Stage dialogStage = new Stage(StageStyle.TRANSPARENT);
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(dialogScene);
        dialogStage.setTitle("Focus Session Complete");

        // Button actions with fade-out animation
        yesButton.setOnAction(e -> {
            animateDialogClose(dialogStage, () -> {
                if (onYes != null) onYes.run();
            });
        });

        noButton.setOnAction(e -> {
            animateDialogClose(dialogStage, () -> {
                if (onNo != null) onNo.run();
            });
        });

        // Show with fade-in and scale animation
        dialogStage.setOnShown(e -> {
            // Center the dialog
            dialogStage.setX(owner.getX() + (owner.getWidth() - 600) / 2);
            dialogStage.setY(owner.getY() + (owner.getHeight() - 400) / 2);

            // Fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), dialogRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            // Scale-in animation for content
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), contentBox);
            scaleIn.setFromX(0.85);
            scaleIn.setFromY(0.85);
            scaleIn.setToX(1);
            scaleIn.setToY(1);

            fadeIn.play();
            scaleIn.play();
        });

        return dialogStage;
    }

    private static void styleYesButton(Button button) {
        button.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #8B5CF6;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: 12;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 700;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 8, 0, 0, 2);"
        );

        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                "-fx-text-fill: #7C3AED;" +
                "-fx-padding: 12 28 12 28;" +
                "-fx-background-radius: 12;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 700;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(124, 58, 237, 0.4), 12, 0, 0, 4);"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #8B5CF6;" +
                "-fx-padding: 12 28 12 28;" +
                "-fx-background-radius: 12;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 700;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 8, 0, 0, 2);"
            );
        });
    }

    private static void styleNoButton(Button button) {
        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(255, 255, 255, 0.6);" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 700;" +
            "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.15);" +
                "-fx-text-fill: white;" +
                "-fx-padding: 12 28 12 28;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(255, 255, 255, 0.8);" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 12;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 700;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.3), 12, 0, 0, 3);"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 12 28 12 28;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(255, 255, 255, 0.6);" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 12;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 700;" +
                "-fx-cursor: hand;"
            );
        });
    }

    private static void animateDialogClose(Stage stage, Runnable onComplete) {
        StackPane root = (StackPane) stage.getScene().getRoot();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            stage.close();
            if (onComplete != null) onComplete.run();
        });

        fadeOut.play();
    }
}
