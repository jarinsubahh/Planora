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

    @FXML
    private void initialize() {
        // Set default priority
        selectPriorityButton(lowButton, "Low");

        // Set category items
        categoryComboBox.getItems().addAll("Study", "Work", "Personal", "Health", "Other");
    }

    @FXML
    private void selectPriority(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String priority = clickedButton.getText();

        // Reset all buttons
        resetPriorityButtons();

        // Select the clicked one
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
            showAlert("Error", "Title is required!");
            return;
        }

        Task task = new Task();
        task.setUserId(UserManager.currentUser);
        task.setTitle(titleField.getText().trim());
        task.setDescription(descriptionArea.getText().trim());
        task.setDeadline(deadlinePicker.getValue());
        task.setCategory(categoryComboBox.getValue());
        task.setPriority(selectedPriority);
        task.setCompleted(false);

        TaskService.createTask(task);

        // Close the modal
        closeWindow();

        // Refresh dashboard if needed (can be handled by dashboard controller)
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}