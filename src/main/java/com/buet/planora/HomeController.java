package com.buet.planora;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class HomeController {

    @FXML
    private FlowPane taskPane;

    public void initialize() {
        // Add sample tasks dynamically
        for (int i = 1; i <= 5; i++) {
            VBox card = new VBox();
            card.getStyleClass().add("task-card");
            card.setPrefSize(250, 150);
            card.getChildren().add(new Label("Task " + i));
            card.getChildren().add(new Label("Description of task " + i));

            addHoverEffect(card);
            taskPane.getChildren().add(card);
        }
    }

    private void addHoverEffect(VBox card) {
        card.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        card.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }
}
