package com.example.javafx_project;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import java.util.List;

public class ChatController {

    @FXML
    private VBox chatMessagesContainer;
    
    @FXML
    private ScrollPane chatScrollPane;
    
    @FXML
    private TextArea messageInputField;
    
    @FXML
    private Button sendButton;

    private Space currentSpace;

    @FXML
    private void initialize() {
        // Setup scroll pane to auto-scroll
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Setup send button
        sendButton.setOnAction(e -> sendMessage());
        
        // Allow Enter key to send (with Shift+Enter for new line)
        messageInputField.setWrapText(true);
        messageInputField.setPrefRowCount(3);
    }

    public void setSpace(Space space) {
        this.currentSpace = space;
        loadMessages();
    }

    private void loadMessages() {
        if (currentSpace == null) return;

        chatMessagesContainer.getChildren().clear();

        // Load messages from database
        List<Message> messages = DatabaseManager.getMessagesBySpaceId(currentSpace.getSpaceName());

        for (Message message : messages) {
            addMessageToUI(message, false);
        }

        // Auto-scroll to bottom
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    @FXML
    private void sendMessage() {
        String text = messageInputField.getText().trim();
        if (text.isEmpty() || currentSpace == null) {
            return;
        }

        // Create message object
        Message message = new Message(currentSpace.getSpaceName(), UserManager.currentUser, text);

        // Save to database (async to avoid UI blocking)
        new Thread(() -> {
            DatabaseManager.saveMessage(message);
        }).start();

        // Add to UI immediately
        addMessageToUI(message, true);

        // Clear input field
        messageInputField.clear();
        messageInputField.requestFocus();

        // Auto-scroll to bottom
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    private void addMessageToUI(Message message, boolean isSent) {
        Platform.runLater(() -> {
            HBox messageBox = new HBox();
            messageBox.setSpacing(8);
            messageBox.setPadding(new Insets(8, 12, 8, 12));

            VBox bubbleContainer = new VBox();
            bubbleContainer.setSpacing(4);

            // Sender name label
            Label senderLabel = new Label(message.getSenderId());
            senderLabel.getStyleClass().add("message-sender");

            // Message bubble
            Label messageLabel = new Label(message.getMessageText());
            messageLabel.setWrapText(true);
            messageLabel.getStyleClass().add("message-bubble");

            // Time label
            Label timeLabel = new Label(message.getFormattedTime());
            timeLabel.getStyleClass().add("message-time");

            bubbleContainer.getChildren().addAll(senderLabel, messageLabel, timeLabel);

            if (isSent) {
                // Sent messages - align right
                messageBox.setAlignment(Pos.CENTER_RIGHT);
                bubbleContainer.getStyleClass().add("sent-bubble");
                messageBox.getChildren().add(bubbleContainer);
            } else {
                // Received messages - align left
                messageBox.setAlignment(Pos.CENTER_LEFT);
                bubbleContainer.getStyleClass().add("received-bubble");
                messageBox.getChildren().add(bubbleContainer);
            }

            chatMessagesContainer.getChildren().add(messageBox);
        });
    }

    public void refreshMessages() {
        loadMessages();
    }
}
