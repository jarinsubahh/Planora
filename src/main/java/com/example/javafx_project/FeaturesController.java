package com.example.javafx_project;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class FeaturesController {
    @FXML
    private Button backButton;
    
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

    /**
     * Add hover animation to a feature card
     * Scales up (1.03x) and translates up (-8px) with smooth 0.3s transition
     */
    private void addCardHoverAnimation(VBox card) {
        card.setStyle(card.getStyle() + "; -fx-cursor: hand;");
        
        card.setOnMouseEntered(event -> {
            playHoverAnimation(card, true);
        });
        
        card.setOnMouseExited(event -> {
            playHoverAnimation(card, false);
        });
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
