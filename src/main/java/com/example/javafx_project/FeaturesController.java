package com.example.javafx_project;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FeaturesController {
    @FXML
    private Button backButton;

    @FXML
    private AnchorPane mainContainer;

    @FXML
    private GridPane cardsGrid;

    @FXML
    private VBox card1;

    @FXML
    private VBox card2;

    @FXML
    private VBox card3;

    @FXML
    private VBox card4;

    @FXML
    private VBox card5;

    @FXML
    private VBox card6;

    @FXML
    public void initialize() {
        // Add hover animations to all feature cards
        addCardHoverAnimation(card1);
        addCardHoverAnimation(card2);
        addCardHoverAnimation(card3);
        addCardHoverAnimation(card4);
        addCardHoverAnimation(card5);
        addCardHoverAnimation(card6);
    }
    private void addFloatingBackground() {
        // Create the glowy circle
        Circle bgCircle = new Circle(300, Color.web("#FFD6E8", 0.2));
        bgCircle.getStyleClass().add("glow-circle");

        // mainContainer is now recognized
        mainContainer.getChildren().add(0, bgCircle);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(15), bgCircle);
        transition.setFromX(-100);
        transition.setToX(100);
        transition.setFromY(50);
        transition.setToY(-50);
        transition.setAutoReverse(true);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();
    }
    private void addCardHoverAnimation(VBox card) {
        card.setStyle(card.getStyle() + "; -fx-cursor: hand;");
        
        card.setOnMouseEntered(event -> {
            playHoverAnimation(card, true);
        });
        
        card.setOnMouseExited(event -> {
            playHoverAnimation(card, false);
        });
    }
    private void applyCardAnimations() {
        // Apply scale hover effect to every card in the grid
        for (Node node : cardsGrid.getChildren()) {
            if (node instanceof javafx.scene.layout.VBox) {
                node.setOnMouseEntered(e -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
                    st.setToX(1.05);
                    st.setToY(1.05);
                    st.play();
                });
                node.setOnMouseExited(e -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
                    st.setToX(1.0);
                    st.setToY(1.0);
                    st.play();
                });
            }
        }
    }

    /**
     * Play scale and translate animations on card hover
     */
    private void playHoverAnimation(VBox card, boolean isHovering) {
        // Scale transition
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), card);
        scaleTransition.setToX(isHovering ? 1.03 : 1.0);
        scaleTransition.setToY(isHovering ? 1.03 : 1.0);

        // Translate transition (move up on hover)
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), card);
        translateTransition.setToY(isHovering ? -8 : 0);

        // Play both animations simultaneously
        scaleTransition.play();
        translateTransition.play();
    }

    /**
     * Handle back button click - navigate to landing page
     */
    @FXML
    private void handleBackButton() {
        try {
            // Create a new PlanoraLandingPage instance
            PlanoraLandingPage landingPage = new PlanoraLandingPage();
            Stage stage = (Stage) backButton.getScene().getWindow();
            
            // Start the landing page on the current stage
            landingPage.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading landing page: " + e.getMessage());
        }
    }
}
