package com.example.javafx_project;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class FocusSessionController {

    @FXML private StackPane focusSessionRoot;
    @FXML private Pane animationPane;
    @FXML private Label timerLabel;
    @FXML private Label taskTitleLabel;
    @FXML private Button pauseButton;
    @FXML private Button resumeButton;
    @FXML private ToggleButton soundToggle;
    @FXML private Button exitButton;

    private Timeline timeline;
    private int timeRemaining;
    private String selectedMode;
    private Stage previousStage;
    private Task currentTask;
    private boolean isSessionEnded = false;

    private MediaPlayer rainPlayer, naturePlayer, nightPlayer;

    @FXML
    public void initialize() {
        // Clip fix for animation pane
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(animationPane.widthProperty());
        clip.heightProperty().bind(animationPane.heightProperty());
        animationPane.setClip(clip);

        // Button actions
        pauseButton.setOnAction(e -> pauseTimer());
        resumeButton.setOnAction(e -> resumeTimer());
        exitButton.setOnAction(e -> exitFocusSession());
        soundToggle.selectedProperty().addListener((obs, old, selected) -> handleSoundToggle(selected));

        // Load audio files asynchronously
        new Thread(this::loadAudioFiles).start();

        // Set sound on by default
        soundToggle.setSelected(true);

        // Initially disable pause/resume
        pauseButton.setDisable(true);
        resumeButton.setDisable(true);
    }

    private void loadAudioFiles() {
        try {
            if (getClass().getResource("/com/example/javafx_project/rain.mpeg") != null) {
                rainPlayer = new MediaPlayer(new Media(getClass().getResource("/com/example/javafx_project/rain.mpeg").toString()));
                rainPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                System.out.println("Rain sound loaded");
            }
            if (getClass().getResource("/com/example/javafx_project/night.mpeg") != null) {
                nightPlayer = new MediaPlayer(new Media(getClass().getResource("/com/example/javafx_project/night.mpeg").toString()));
                nightPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                System.out.println("Night sound loaded");
            }
            if (getClass().getResource("/com/example/javafx_project/nature.mpeg") != null) {
                naturePlayer = new MediaPlayer(new Media(getClass().getResource("/com/example/javafx_project/nature.mpeg").toString()));
                naturePlayer.setCycleCount(MediaPlayer.INDEFINITE);
                System.out.println("Nature sound loaded");
            }
        } catch (Exception e) {
            System.err.println("Error loading audio files: " + e.getMessage());
        }
    }

    private void handleSoundToggle(boolean selected) {
        if (selected) {
            if ("RAIN".equals(selectedMode) && rainPlayer != null) rainPlayer.play();
            else if ("NIGHT".equals(selectedMode) && nightPlayer != null) nightPlayer.play();
            else if ("NATURE".equals(selectedMode) && naturePlayer != null) naturePlayer.play();
        } else {
            if (rainPlayer != null) rainPlayer.pause();
            if (nightPlayer != null) nightPlayer.pause();
            if (naturePlayer != null) naturePlayer.pause();
        }
    }

    public void setTaskData(Task task, String mode, int durationSeconds, Stage previousStage) {
        this.currentTask = task;
        this.selectedMode = mode;
        this.previousStage = previousStage;
        this.timeRemaining = durationSeconds;
        this.isSessionEnded = false;

        taskTitleLabel.setText(task != null ? task.getTitle() : "Untitled Task");
        updateTimer();

        // Apply styling based on mode
        applyModeStyles();

        // Start animation and timer
        startAnimation();
        startTimer();
    }

    private void startAnimation() {
        // Stop any playing sounds first
        stopAllSounds();

        // Play sound if enabled
        if (soundToggle.isSelected()) {
            playSound();
        }

        // Create animation based on mode
        switch (selectedMode) {
            case "RAIN" -> createRainAnimation();
            case "NIGHT" -> createNightAnimation();
            case "NATURE" -> createNatureAnimation();
        }
    }

    private void playSound() {
        try {
            if ("RAIN".equals(selectedMode) && rainPlayer != null) {
                rainPlayer.play();
                System.out.println("Playing rain sound");
            } else if ("NIGHT".equals(selectedMode) && nightPlayer != null) {
                nightPlayer.play();
                System.out.println("Playing night sound");
            } else if ("NATURE".equals(selectedMode) && naturePlayer != null) {
                naturePlayer.play();
                System.out.println("Playing nature sound");
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    private void stopAllSounds() {
        if (rainPlayer != null) rainPlayer.stop();
        if (naturePlayer != null) naturePlayer.stop();
        if (nightPlayer != null) nightPlayer.stop();
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimer();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        
        // Set proper completion handler
        timeline.setOnFinished(e -> {
            if (!isSessionEnded) {
                isSessionEnded = true;
                onSessionTimerEnded();
            }
        });

        // Also add a check as safety net
        timeline.setCycleCount(timeRemaining + 1);
        
        timeline.play();

        pauseButton.setDisable(false);
        resumeButton.setDisable(true);
    }

    private void updateTimer() {
        int min = timeRemaining / 60;
        int sec = timeRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", min, sec));
    }

    private void pauseTimer() {
        if (timeline != null) timeline.pause();
        pauseButton.setDisable(true);
        resumeButton.setDisable(false);
    }

    private void resumeTimer() {
        if (timeline != null) timeline.play();
        pauseButton.setDisable(false);
        resumeButton.setDisable(true);
    }

    /**
     * Called when the focus session timer reaches zero.
     * Stops all sounds and shows the session end dialog.
     */
    private void onSessionTimerEnded() {
        Platform.runLater(() -> {
            // Disable pause/resume buttons
            pauseButton.setDisable(true);
            resumeButton.setDisable(true);

            // Stop all sounds
            stopAllSounds();

            // Show session end dialog
            showSessionEndDialog();
        });
    }

    /**
     * Shows the session end dialog asking if work was finished.
     * Handles task completion and navigation back to focus mode.
     */
    private void showSessionEndDialog() {
        Stage currentStage = (Stage) timerLabel.getScene().getWindow();

        Stage dialogStage = FocusSessionEndDialog.show(
            currentStage,
            // On YES: Mark task completed and navigate back
            () -> {
                if (currentTask != null) {
                    // Mark task as completed in database
                    TaskService.markCompleted(currentTask.getTitle(), UserManager.currentUser);
                }
                returnToFocusMode(currentStage);
            },
            // On NO: Just navigate back without changes
            () -> {
                returnToFocusMode(currentStage);
            }
        );

        dialogStage.show();
    }

    /**
     * Returns to the focus mode main screen, resetting state.
     */
    private void returnToFocusMode(Stage currentStage) {
        // Stop any remaining sounds
        stopAllSounds();

        // Stop timeline if still running
        if (timeline != null && timeline.getStatus().equals(Animation.Status.RUNNING)) {
            timeline.stop();
        }

        // Close current session stage
        currentStage.close();

        // Show previous stage (Focus Mode main page)
        if (previousStage != null) {
            previousStage.show();
        }
    }

    // 🌧 Rain animation
    private void createRainAnimation() {
        animationPane.getChildren().clear();
        double width = animationPane.getWidth() > 0 ? animationPane.getWidth() : 1200;
        double height = animationPane.getHeight() > 0 ? animationPane.getHeight() : 700;

        Rectangle bg = new Rectangle(width, height);
        bg.setFill(Color.web("#6B8EBC"));
        animationPane.getChildren().add(bg);

        for (int i = 0; i < 80; i++) {
            javafx.scene.shape.Line rain = new javafx.scene.shape.Line(0, 0, 0, 15);
            rain.setStroke(Color.web("#FFFFFF", 0.6));
            rain.setOpacity(0.65);

            rain.setLayoutX(Math.random() * width);
            rain.setLayoutY(-Math.random() * height * 0.5);

            animationPane.getChildren().add(rain);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5 + Math.random() * 1.5), rain);
            tt.setFromY(rain.getLayoutY());
            tt.setToY(height + 50);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setDelay(Duration.seconds(Math.random() * 2));
            tt.play();
        }
    }

    // 🌙 Night mode
    private void createNightAnimation() {
        animationPane.getChildren().clear();
        double width = animationPane.getWidth() > 0 ? animationPane.getWidth() : 1200;
        double height = animationPane.getHeight() > 0 ? animationPane.getHeight() : 700;

        Rectangle bg = new Rectangle();
        bg.widthProperty().bind(animationPane.widthProperty());
        bg.heightProperty().bind(animationPane.heightProperty());
        bg.setFill(new javafx.scene.paint.LinearGradient(0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.web("#0b1226")),
                new javafx.scene.paint.Stop(1, Color.web("#1b2735"))));
        animationPane.getChildren().add(bg);

        // Stars
        for (int i = 0; i < 60; i++) {
            javafx.scene.shape.Circle star = new javafx.scene.shape.Circle(1 + Math.random() * 2);
            star.setFill(Color.web("#ffffff", 0.9));
            star.setOpacity(0.6 + Math.random() * 0.4);
            star.setLayoutX(Math.random() * width);
            star.setLayoutY(Math.random() * height * 0.6);
            animationPane.getChildren().add(star);

            FadeTransition ft = new FadeTransition(Duration.seconds(2 + Math.random() * 3), star);
            ft.setFromValue(0.2);
            ft.setToValue(1.0);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.setAutoReverse(true);
            ft.setDelay(Duration.seconds(Math.random() * 2));
            ft.play();
        }

        // Glow
        javafx.scene.shape.Circle glow = new javafx.scene.shape.Circle(150);
        glow.setFill(Color.web("#3a2f57", 0.08));
        glow.setLayoutX(width * 0.8);
        glow.setLayoutY(height * 0.2);
        animationPane.getChildren().add(glow);

        TranslateTransition tglow = new TranslateTransition(Duration.seconds(10), glow);
        tglow.setByX(-50);
        tglow.setAutoReverse(true);
        tglow.setCycleCount(Animation.INDEFINITE);
        tglow.play();
    }

    // 🌿 Nature mode
    private void createNatureAnimation() {
        animationPane.getChildren().clear();
        double width = animationPane.getWidth() > 0 ? animationPane.getWidth() : 1200;
        double height = animationPane.getHeight() > 0 ? animationPane.getHeight() : 700;

        Rectangle bg = new Rectangle();
        bg.widthProperty().bind(animationPane.widthProperty());
        bg.heightProperty().bind(animationPane.heightProperty());
        bg.setFill(new javafx.scene.paint.LinearGradient(0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.web("#f0fff4", 0.3)),
                new javafx.scene.paint.Stop(1, Color.web("#e6fff6", 0.15))));
        animationPane.getChildren().add(bg);

        for (int i = 0; i < 24; i++) {
            javafx.scene.text.Text leaf = new javafx.scene.text.Text("🌿");
            leaf.setStyle("-fx-font-size: 28px;");
            leaf.setFill(Color.web("#2e7d32", 0.95));
            leaf.setOpacity(0.9 - Math.random() * 0.35);

            double startX = Math.random() * width;
            double startY = -20 - Math.random() * 200;
            leaf.setLayoutX(startX);
            leaf.setLayoutY(startY);

            animationPane.getChildren().add(leaf);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(5 + Math.random() * 5), leaf);
            tt.setFromY(startY);
            tt.setToY(height + 40);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setDelay(Duration.seconds(Math.random() * 2));

            RotateTransition rt = new RotateTransition(Duration.seconds(3 + Math.random() * 4), leaf);
            rt.setByAngle((Math.random() > 0.5 ? 1 : -1) * (30 + Math.random() * 60));
            rt.setCycleCount(Animation.INDEFINITE);
            rt.setAutoReverse(true);

            TranslateTransition sway = new TranslateTransition(Duration.seconds(2 + Math.random() * 2), leaf);
            sway.setByX((Math.random() > 0.5 ? 1 : -1) * (30 + Math.random() * 60));
            sway.setAutoReverse(true);
            sway.setCycleCount(Animation.INDEFINITE);

            new ParallelTransition(tt, rt, sway).play();
        }
    }

    private void applyModeStyles() {
        // Reset all style classes
        focusSessionRoot.getStyleClass().removeAll("night-mode", "nature-mode");

        if ("NIGHT".equals(selectedMode)) {
            focusSessionRoot.getStyleClass().add("night-mode");
        } else if ("NATURE".equals(selectedMode)) {
            focusSessionRoot.getStyleClass().add("nature-mode");
        }
    }

    private void exitFocusSession() {
        if (!isSessionEnded) {
            // Prevent showing dialog twice
            isSessionEnded = true;
        }

        Stage currentStage = (Stage) exitButton.getScene().getWindow();
        returnToFocusMode(currentStage);
    }
}
