package com.example.javafx_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddTaskController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker deadlinePicker;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private Button lowButton;

    @FXML
    private Button mediumButton;

    @FXML
    private Button highButton;

    @FXML
    private Button urgentButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button backButton;

    private String selectedPriority = "Low"; // Default

    private Space currentSpace = null;

    private boolean isEditMode = false;
    private Task taskToEdit = null;

    public void setCurrentSpace(Space space) {
        this.currentSpace = space;
    }

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
    }

    public void setTaskToEdit(Task task) {
        this.taskToEdit = task;
    }

    public void setupForEdit() {
        if (isEditMode && taskToEdit != null) {
            titleField.setText(taskToEdit.getTitle());
            descriptionArea.setText(taskToEdit.getDescription());
            deadlinePicker.setValue(taskToEdit.getDeadline());
            categoryComboBox.setValue(taskToEdit.getCategory());
            // Set priority
            String priority = taskToEdit.getPriority();
            switch (priority.toLowerCase()) {
                case "low" -> selectPriorityButton(lowButton, "Low");
                case "medium" -> selectPriorityButton(mediumButton, "Medium");
                case "high" -> selectPriorityButton(highButton, "High");
                case "urgent" -> selectPriorityButton(urgentButton, "Urgent");
            }
        }
    }

    @FXML
    private void initialize() {

        selectPriorityButton(lowButton, "Low");

        categoryComboBox.getItems().addAll("Study", "Work", "Personal", "Health", "Other");
    }

    @FXML
    private void selectPriority(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String priority = clickedButton.getText();

        resetPriorityButtons();

        selectPriorityButton(clickedButton, priority);
        selectedPriority = priority;
    }

    private void resetPriorityButtons() {
        lowButton.getStyleClass().remove("low-selected");
        mediumButton.getStyleClass().remove("medium-selected");
        highButton.getStyleClass().remove("high-selected");
        urgentButton.getStyleClass().remove("urgent-selected");
    }

    private void selectPriorityButton(Button button, String priority) {
        switch (priority.toLowerCase()) {
            case "low" -> button.getStyleClass().add("low-selected");
            case "medium" -> button.getStyleClass().add("medium-selected");
            case "high" -> button.getStyleClass().add("high-selected");
            case "urgent" -> button.getStyleClass().add("urgent-selected");
        }
    }

    @FXML
    private void saveTask() {
        if (titleField.getText().trim().isEmpty()) {
            showToast("Title is required!");
            return;
        }

        Task task = new Task();
        if (isEditMode && taskToEdit != null) {
            task.setId(taskToEdit.getId());
            task.setUserId(taskToEdit.getUserId());
            task.setCompleted(taskToEdit.isCompleted());
            task.setCreatedAt(taskToEdit.getCreatedAt());
        } else {
            task.setUserId(UserManager.currentUser);
            task.setCompleted(false);
        }
        task.setTitle(titleField.getText().trim());
        task.setDescription(descriptionArea.getText().trim());
        task.setDeadline(deadlinePicker.getValue());
        task.setCategory(categoryComboBox.getValue());
        task.setPriority(selectedPriority);

        if (isEditMode) {
            if (currentSpace != null) {
                currentSpace.getSpaceTasks().remove(taskToEdit);
                currentSpace.getSpaceTasks().add(task);
                // Save to MongoDB (cloud-first)
                if (DatabaseManager.isConnected()) {
                    new Thread(() -> DatabaseManager.saveSpace(currentSpace)).start();
                } else {
                    showToast("Not connected to cloud. Changes may not be saved.");
                }
            } else {
                TaskService.updateTask(task);
            }
        } else {
            if (currentSpace != null) {
                currentSpace.getSpaceTasks().add(task);

                if (DatabaseManager.isConnected()) {
                    new Thread(() -> DatabaseManager.saveSpace(currentSpace)).start();
                } else {
                    showToast("Not connected to cloud. Changes may not be saved.");
                }
            } else {
                TaskService.createTask(task, UserManager.currentUser);
            }
        }

        closeWindow();

    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void goBackToDashboard() {
        closeWindow();
    }

    private void showToast(String message) {
        if (saveButton != null && saveButton.getScene() != null) {
            Toast.show((Stage) saveButton.getScene().getWindow(), message);
        }
    }
}
