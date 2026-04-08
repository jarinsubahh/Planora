package com.example.javafx_project;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class FocusModeController {

    @FXML private ComboBox<Task> taskSelector;
    @FXML private ComboBox<String> durationBox;
    @FXML private ToggleButton rainMode;
    @FXML private ToggleButton pianoMode;
    @FXML private ToggleButton natureMode;
    @FXML private Button startButton;

    private String selectedMode;

    @FXML
    public void initialize() {
        new Thread(() -> {
            try {
                var tasks = TaskService.getTasksByUser(UserManager.currentUser).stream()
                        .filter(t -> !t.isCompleted())
                        .collect(java.util.stream.Collectors.toList());

                Platform.runLater(() -> taskSelector.setItems(FXCollections.observableArrayList(tasks)));
            } catch (Exception e) {
                System.err.println("Error loading tasks: " + e.getMessage());
            }
        }).start();

        taskSelector.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                setText(empty || task == null ? null : task.getTitle());
            }
        });

        taskSelector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                setText(empty || task == null ? "Select Task" : task.getTitle());
            }
        });

        durationBox.setItems(FXCollections.observableArrayList("15 min", "25 min", "50 min"));
        durationBox.setValue("25 min");

        ToggleGroup group = new ToggleGroup();
        rainMode.setToggleGroup(group);
        pianoMode.setToggleGroup(group);
        natureMode.setToggleGroup(group);

        group.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == rainMode) selectedMode = "RAIN";
            else if (selected == pianoMode) selectedMode = "NIGHT";
            else if (selected == natureMode) selectedMode = "NATURE";
            updateModeToggleStyles();
        });

        selectedMode = "RAIN";
        rainMode.setSelected(true);
        updateModeToggleStyles();

        startButton.setOnAction(e -> startFocus());
    }

    private int getDurationSeconds() {
        return switch (durationBox.getValue()) {
            case "15 min" -> 15 * 60;
            case "50 min" -> 50 * 60;
            default -> 25 * 60;
        };
    }

    @FXML
    private void startFocus() {
        Task selectedTask = taskSelector.getValue();
        if (selectedTask == null) {
            Toast.show((Stage) startButton.getScene().getWindow(), "Please select a task to focus on.");
            return;
        }

        if (selectedMode == null) {
            Toast.show((Stage) startButton.getScene().getWindow(), "Please choose a focus mode.");
            return;
        }

        Stage ownerStage = (Stage) startButton.getScene().getWindow();
        int durationSeconds = getDurationSeconds();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/focus_session.fxml"));
            Parent focusRoot = loader.load();
            focusRoot.getStylesheets().add(getClass().getResource("/com/example/javafx_project/focus_session.css").toExternalForm());

            FocusSessionController controller = loader.getController();
            controller.setTaskData(selectedTask, selectedMode, durationSeconds, ownerStage);

            Scene scene = new Scene(focusRoot, 1200, 700);
            scene.setFill(Color.TRANSPARENT);

            Stage focusStage = new Stage(StageStyle.TRANSPARENT);
            focusStage.initOwner(ownerStage);
            focusStage.initModality(Modality.APPLICATION_MODAL);
            focusStage.setScene(scene);

            ownerStage.hide();
            focusStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateModeToggleStyles() {
        rainMode.getStyleClass().remove("mode-toggle-selected");
        pianoMode.getStyleClass().remove("mode-toggle-selected");
        natureMode.getStyleClass().remove("mode-toggle-selected");
        Toggle selected = rainMode.getToggleGroup().getSelectedToggle();
        if (selected == rainMode) rainMode.getStyleClass().add("mode-toggle-selected");
        else if (selected == pianoMode) pianoMode.getStyleClass().add("mode-toggle-selected");
        else if (selected == natureMode) natureMode.getStyleClass().add("mode-toggle-selected");
    }
}
