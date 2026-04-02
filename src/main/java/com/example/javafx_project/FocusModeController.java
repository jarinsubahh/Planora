package com.example.javafx_project;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Platform;


public class FocusModeController {

    @FXML private ComboBox<Task> taskSelector;
    @FXML private ComboBox<String> durationBox;

    @FXML private ToggleButton rainMode, pianoMode, natureMode, soundToggle;

    @FXML private Button startButton, pauseButton, resumeButton, exitButton;

    @FXML private Label timerLabel, taskTitleLabel;

    @FXML private Pane animationPane;
    @FXML private Pane setupPane, focusPane;

    private Timeline timeline;
    private int timeRemaining;
    private String selectedMode;

    private Runnable onExit;

    private MediaPlayer rainPlayer, naturePlayer, nightPlayer;

    @FXML
    public void initialize() {

        // CLIP FIX (VERY IMPORTANT)
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(animationPane.widthProperty());
        clip.heightProperty().bind(animationPane.heightProperty());
        animationPane.setClip(clip);

        // Load tasks asynchronously to avoid blocking UI
        new Thread(() -> {
            try {
                var tasks = TaskService.getTasksByUser(UserManager.currentUser).stream()
                        .filter(t -> !t.isCompleted())
                        .collect(java.util.stream.Collectors.toList());
                
                Platform.runLater(() -> {
                    taskSelector.setItems(FXCollections.observableArrayList(tasks));
                });
            } catch (Exception e) {
                System.err.println("Error loading tasks: " + e.getMessage());
            }
        }).start();

        taskSelector.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Task t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? null : t.getTitle());
            }
        });

        taskSelector.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Task t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? "Select Task" : t.getTitle());
            }
        });

        // Duration
        durationBox.setItems(FXCollections.observableArrayList("15 min", "25 min", "50 min"));
        durationBox.setValue("25 min");

        // Mode selection
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

        startButton.setOnAction(e -> startFocus());
        pauseButton.setOnAction(e -> pauseTimer());
        resumeButton.setOnAction(e -> resumeTimer());
        exitButton.setOnAction(e -> exitFocusMode());

        // Load audio files asynchronously
        new Thread(() -> {
            try {
                if (getClass().getResource("/com/example/javafx_project/rain.mpeg") != null) {
                    rainPlayer = new MediaPlayer(new Media(getClass().getResource("/com/example/javafx_project/rain.mpeg").toString()));
                    rainPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    System.out.println("Rain sound loaded");
                } else {
                    System.err.println("rain.mpeg not found");
                }
                if (getClass().getResource("/com/example/javafx_project/night.mpeg") != null) {
                    nightPlayer = new MediaPlayer(new Media(getClass().getResource("/com/example/javafx_project/night.mpeg").toString()));
                    nightPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    System.out.println("Night sound loaded");
                } else {
                    System.err.println("night.mpeg not found");
                }
                if (getClass().getResource("/com/example/javafx_project/nature.mpeg") != null) {
                    naturePlayer = new MediaPlayer(new Media(getClass().getResource("/com/example/javafx_project/nature.mpeg").toString()));
                    naturePlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    System.out.println("Nature sound loaded");
                } else {
                    System.err.println("nature.mpeg not found");
                }
            } catch (Exception e) {
                System.err.println("Error loading audio files: " + e.getMessage());
            }
        }).start();

        // Sound toggle listener
        soundToggle.selectedProperty().addListener((obs, old, selected) -> {
            if (selected) {
                if ("RAIN".equals(selectedMode) && rainPlayer != null) rainPlayer.play();
                else if ("NIGHT".equals(selectedMode) && nightPlayer != null) nightPlayer.play();
                else if ("NATURE".equals(selectedMode) && naturePlayer != null) naturePlayer.play();
            } else {
                if (rainPlayer != null) rainPlayer.pause();
                if (nightPlayer != null) nightPlayer.pause();
                if (naturePlayer != null) naturePlayer.pause();
            }
        });

        // Set sound on by default
        soundToggle.setSelected(true);
    }

    private int getDuration() {
        return switch (durationBox.getValue()) {
            case "15 min" -> 15 * 60;
            case "50 min" -> 50 * 60;
            default -> 25 * 60;
        };
    }

    private void startFocus() {

        if (taskSelector.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a task to focus on.");
            alert.showAndWait();
            return;
        }

        if (selectedMode == null) return;

        timeRemaining = getDuration();

        setupPane.setVisible(false);
        focusPane.setVisible(true);

        taskTitleLabel.setText(taskSelector.getValue().getTitle());

        // Add top padding to focusPane to make room for exit button
        focusPane.setStyle("-fx-padding: 60 0 0 0;");

        startAnimation();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimer();

            if (timeRemaining <= 0) {
                timeline.stop();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Enable pause, disable resume
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

    private void startAnimation() {
        // Stop any playing sounds
        if (rainPlayer != null) rainPlayer.stop();
        if (naturePlayer != null) naturePlayer.stop();
        if (nightPlayer != null) nightPlayer.stop();

        // Play sound if enabled
        if (soundToggle.isSelected()) {
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

        switch (selectedMode) {
            case "RAIN" -> createRainAnimation();
            case "NIGHT" -> createNightAnimation();
            case "NATURE" -> createNatureAnimation();
        }
        applyModeStyles();
    }

//    // 🌧 FIXED RAIN
//    private void createRainAnimation() {
//        animationPane.getChildren().clear();
//
//        for (int i = 0; i < 80; i++) {
//            javafx.scene.shape.Line rain = new javafx.scene.shape.Line(0, 0, 0, 12);
//            rain.setStyle("-fx-stroke: rgba(120,120,255,0.5);");
//
//            rain.setLayoutX(Math.random() * 600);
//            rain.setLayoutY(Math.random() * 400);
//
//            animationPane.getChildren().add(rain);
//
//            TranslateTransition tt = new TranslateTransition(Duration.seconds(1), rain);
//            tt.setFromY(-50);
//            tt.setToY(400);
//            tt.setCycleCount(Animation.INDEFINITE);
//            tt.setDelay(Duration.seconds(Math.random()));
//            tt.play();
//        }
//    }

    // 🌧 Rain animation (uses actual pane size)
    private void createRainAnimation() {
        animationPane.getChildren().clear();
        double width = animationPane.getWidth() > 0 ? animationPane.getWidth() : animationPane.getPrefWidth();
        double height = animationPane.getHeight() > 0 ? animationPane.getHeight() : animationPane.getPrefHeight();
// --- ADD THESE LINES FOR THE PRUSSIAN BLUE BACKGROUND ---
        javafx.scene.shape.Rectangle bg = new javafx.scene.shape.Rectangle(width, height);
        bg.setFill(Color.web("#6B8EBC")); // Light Prussian Blue hex code
        animationPane.getChildren().add(bg);
        for (int i = 0; i < 80; i++) {
            javafx.scene.shape.Line rain = new javafx.scene.shape.Line(0, 0, 0, 15);
            rain.setStroke(Color.web("#FFFFFF", 0.6));//"#7A73FF",0.35
            rain.setOpacity(0.65);

            rain.setLayoutX(Math.random() * width);
            // start slightly above view for natural fall
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


    // 🌙 Night mode - dim background + twinkling stars
    private void createNightAnimation() {
        animationPane.getChildren().clear();
        double width = animationPane.getWidth() > 0 ? animationPane.getWidth() : animationPane.getPrefWidth();
        double height = animationPane.getHeight() > 0 ? animationPane.getHeight() : animationPane.getPrefHeight();

        // Background rectangle (dark gradient)
        javafx.scene.shape.Rectangle bg = new javafx.scene.shape.Rectangle();
        bg.widthProperty().bind(animationPane.widthProperty());
        bg.heightProperty().bind(animationPane.heightProperty());
        bg.setFill(new javafx.scene.paint.LinearGradient(0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web("#0b1226")),
                new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web("#1b2735"))));
        animationPane.getChildren().add(bg);

        // Stars
        for (int i = 0; i < 60; i++) {
            javafx.scene.shape.Circle star = new javafx.scene.shape.Circle(1 + Math.random() * 2);
            star.setFill(javafx.scene.paint.Color.web("#ffffff", 0.9));
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

        // Soft subtle moving glow
        javafx.scene.shape.Circle glow = new javafx.scene.shape.Circle(150);
        glow.setFill(javafx.scene.paint.Color.web("#3a2f57", 0.08));
        glow.setLayoutX(width * 0.8);
        glow.setLayoutY(height * 0.2);
        animationPane.getChildren().add(glow);

        TranslateTransition tglow = new TranslateTransition(Duration.seconds(10), glow);
        tglow.setByX(-50);
        tglow.setAutoReverse(true);
        tglow.setCycleCount(Animation.INDEFINITE);
        tglow.play();
    }

    // 🌿 Nature - falling leaves with soft green background
    private void createNatureAnimation() {
        animationPane.getChildren().clear();
        double width = animationPane.getWidth() > 0 ? animationPane.getWidth() : animationPane.getPrefWidth();
        double height = animationPane.getHeight() > 0 ? animationPane.getHeight() : animationPane.getPrefHeight();

        // Soft translucent green background
        javafx.scene.shape.Rectangle bg = new javafx.scene.shape.Rectangle();
        bg.widthProperty().bind(animationPane.widthProperty());
        bg.heightProperty().bind(animationPane.heightProperty());
        bg.setFill(new javafx.scene.paint.LinearGradient(0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web("#f0fff4", 0.3)),
                new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web("#e6fff6", 0.15))));
        animationPane.getChildren().add(bg);

        for (int i = 0; i < 24; i++) {

            javafx.scene.text.Text leaf = new javafx.scene.text.Text("🌿");
            leaf.setStyle("-fx-font-size: 28px;");
            leaf.setFill(javafx.scene.paint.Color.web("#2e7d32", 0.95));
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
        try {
            if ("NIGHT".equals(selectedMode)) {
                timerLabel.setStyle("-fx-text-fill: white; -fx-font-size:64px; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.6), 6, 0, 0, 2);");
                taskTitleLabel.setStyle("-fx-text-fill: #D9E6FF; -fx-font-size:18px;");
                pauseButton.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-background-radius:12;");
                resumeButton.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-background-radius:12;");
                soundToggle.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-text-fill: white; -fx-background-radius:12;");
            } else if ("NATURE".equals(selectedMode)) {
                timerLabel.setStyle("-fx-text-fill: #234D3A; -fx-font-size:64px;");
                taskTitleLabel.setStyle("-fx-text-fill: #2E4D3A; -fx-font-size:18px;");
                pauseButton.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #0f172a; -fx-background-radius:12;");
                resumeButton.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #0f172a; -fx-background-radius:12;");
                soundToggle.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-text-fill: #0f172a; -fx-background-radius:12;");
            } else {
                timerLabel.setStyle("-fx-text-fill: #6D6AAE; -fx-font-size:64px;");
                taskTitleLabel.setStyle("-fx-text-fill: #4C4A7A; -fx-font-size:18px;");
                pauseButton.setStyle("-fx-background-color: #C4B5FD; -fx-text-fill: #111827; -fx-background-radius:12;");
                resumeButton.setStyle("-fx-background-color: #C4B5FD; -fx-text-fill: #111827; -fx-background-radius:12;");
                soundToggle.setStyle("-fx-background-color: #C4B5FD; -fx-text-fill: #111827; -fx-background-radius:12;");
            }
        } catch (Exception e) {
            // ignore
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

    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    private void exitFocusMode() {
        if (timeline != null) timeline.stop();
        if (rainPlayer != null) rainPlayer.stop();
        if (naturePlayer != null) naturePlayer.stop();
        if (nightPlayer != null) nightPlayer.stop();
        // Clear animations and reset UI
        animationPane.getChildren().clear();
        focusPane.setVisible(false);
        setupPane.setVisible(true);
        // Reset focusPane style
        focusPane.setStyle("");
        // Reset buttons
        pauseButton.setDisable(true);
        resumeButton.setDisable(true);
        // Stop sound toggle
        soundToggle.setSelected(false);
        updateModeToggleStyles();
        // Always exit to dashboard
        //if (onExit != null) {
            //onExit.run();}
    }
}
