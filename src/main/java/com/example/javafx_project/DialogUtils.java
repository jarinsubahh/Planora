package com.example.javafx_project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class DialogUtils {

    private DialogUtils() {
    }

    public static boolean confirm(Stage owner, String title, String message, String confirmText, String cancelText) {
        if (owner == null) {
            return false;
        }

        final boolean[] confirmed = {false};

        Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #6a4c93;");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #3a2a51;");
        messageLabel.setMaxWidth(340);

        Button cancelBtn = new Button(cancelText);
        cancelBtn.setStyle(
            "-fx-background-color: #ece6f6;" +
            "-fx-text-fill: #4f3a74;" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 8 16;" +
            "-fx-font-weight: bold;"
        );
        cancelBtn.setOnAction(e -> dialog.close());

        Button confirmBtn = new Button(confirmText);
        confirmBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #b56cff, #8f48ff);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 8 16;" +
            "-fx-font-weight: bold;"
        );
        confirmBtn.setOnAction(e -> {
            confirmed[0] = true;
            dialog.close();
        });

        HBox actions = new HBox(10, cancelBtn, confirmBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(14, titleLabel, messageLabel, actions);
        root.setPadding(new Insets(18));
        root.setAlignment(Pos.TOP_LEFT);
        root.setStyle(
            "-fx-background-color: rgba(255,255,255,0.98);" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: rgba(143,72,255,0.25);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(70,25,120,0.25), 24, 0.2, 0, 8);"
        );

        Scene scene = new Scene(root, 380, 190);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();

        return confirmed[0];
    }
}
