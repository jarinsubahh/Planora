package com.example.javafx_project;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    @FXML
    private void showSpaceInfo() {
        if (currentSpace == null) return;

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(15);
        root.getStyleClass().add("info-popup-card");
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_CENTER);
        root.setPrefWidth(300);

        Label titleLabel = new Label("✨ Space Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #6a4c93;");

        VBox adminBox = new VBox(5);
        Label adminHeader = new Label("👑 ADMINISTRATOR");
        adminHeader.setStyle("-fx-font-size: 10px; -fx-text-fill: #9370DB; -fx-font-weight: bold;");
        Label adminName = new Label(currentSpace.getAdminUsername());
        adminName.getStyleClass().add("info-text-main");
        adminBox.getChildren().addAll(adminHeader, adminName);

        VBox memberBox = new VBox(5);
        Label memberHeader = new Label("👥 MEMBERS");
        memberHeader.setStyle("-fx-font-size: 10px; -fx-text-fill: #9370DB; -fx-font-weight: bold;");
        VBox memberList = new VBox(3);
        for (String member : currentSpace.getMembers()) {
            Label mLabel = new Label("• " + member);
            mLabel.getStyleClass().add("info-text-sub");
            memberList.getChildren().add(mLabel);
        }
        memberBox.getChildren().addAll(memberHeader, memberList);

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("info-close-button");
        closeBtn.setOnAction(e -> popupStage.close());

        root.getChildren().addAll(titleLabel, adminBox, memberBox, closeBtn);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("space-chat.css").toExternalForm());

        popupStage.setScene(scene);
        popupStage.show();
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
            messageBox.setSpacing(10);
            messageBox.setPadding(new Insets(5, 12, 5, 12));

            VBox bubbleContainer = new VBox();
            bubbleContainer.setSpacing(2);

            Label senderLabel = new Label(message.getSenderId());
            senderLabel.getStyleClass().add("message-sender");

            Label messageLabel = new Label(message.getMessageText());
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(250);
            messageLabel.getStyleClass().add("message-bubble");

            bubbleContainer.getChildren().addAll(senderLabel, messageLabel);

            if (isSent) {
                messageBox.setAlignment(Pos.CENTER_RIGHT);
                bubbleContainer.getStyleClass().add("sent-bubble");
                senderLabel.setStyle("-fx-text-fill: transparent;");
            } else {
                messageBox.setAlignment(Pos.CENTER_LEFT);
                bubbleContainer.getStyleClass().add("received-bubble");
            }

            messageBox.getChildren().add(bubbleContainer);
            chatMessagesContainer.getChildren().add(messageBox);
        });
    }

    public void refreshMessages() {
        loadMessages();
    }
}
