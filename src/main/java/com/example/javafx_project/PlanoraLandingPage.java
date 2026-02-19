package com.example.javafx_project;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.TextAlignment;

public class PlanoraLandingPage extends Application {

    @Override
    public void start(Stage primaryStage) {

        StackPane root = new StackPane();
        root.setId("root");

        Circle bgCircle = new Circle(250, Color.web("#FFD6E8", 0.15));
        bgCircle.setTranslateX(-300);
        bgCircle.setTranslateY(-150);

        TranslateTransition bgMove = new TranslateTransition(Duration.seconds(12), bgCircle);
        bgMove.setFromX(-300);
        bgMove.setToX(-240);
        bgMove.setAutoReverse(true);
        bgMove.setCycleCount(Animation.INDEFINITE);
        bgMove.play();

        root.getChildren().add(bgCircle);

        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setSpacing(40);
        mainContainer.setPadding(new Insets(20, 40, 20, 40));

        HBox navbar = new HBox();
        navbar.setId("navbar");
        navbar.setPrefHeight(70);
        navbar.setAlignment(Pos.CENTER);
        navbar.setPadding(new Insets(0, 25, 0, 25));

        HBox logoSection = new HBox(8);
        logoSection.setAlignment(Pos.CENTER_LEFT);

        Circle logoCircle = new Circle(16, Color.web("#FAD6E8"));
        logoCircle.setEffect(new DropShadow(8, Color.web("#FFD6E8")));

        Label logoIcon = new Label("✨");
        logoIcon.setId("logoIcon");

        StackPane logoStack = new StackPane(logoCircle, logoIcon);

        Label logoText = new Label("PLANORA");
        logoText.setId("logoText");

        logoSection.getChildren().addAll(logoStack, logoText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox navLinks = new HBox(25);
        navLinks.setAlignment(Pos.CENTER_RIGHT);

        Label homeLink = new Label("Home");
        homeLink.setId("homeLink");

        Label featuresLink = new Label("Features");
        featuresLink.setId("featuresLink");

        Button signInBtn = new Button("Sign In");
        signInBtn.setId("signInBtn");

        navLinks.getChildren().addAll(homeLink, featuresLink, signInBtn);
        navbar.getChildren().addAll(logoSection, spacer, navLinks);

        VBox heroSection = new VBox(25);
        heroSection.setAlignment(Pos.CENTER);
        heroSection.setPadding(new Insets(35, 0, 0, 0));

        Circle decoCircle = new Circle(22, Color.web("#FAD6E8"));
        decoCircle.setEffect(new DropShadow(10, Color.web("#FFD6E8")));

        Label decoIcon = new Label("✨");
        decoIcon.setId("decoIcon");

        StackPane iconCircle = new StackPane(decoCircle, decoIcon);

        VBox headingBox = new VBox(-8);
        headingBox.setAlignment(Pos.CENTER);

        Label heading1 = new Label("Plan Smart.");
        heading1.getStyleClass().add("headingGradient");

        Label heading2 = new Label("Glow Bright.");
        heading2.getStyleClass().add("headingGradient");

        headingBox.getChildren().addAll(heading1, heading2);

        Label description = new Label(
                "PLANORA helps you organize your life with beauty and clarity.\n" +
                        "Achieve your dreams in a workspace that feels like magic."
        );
        description.setId("descriptionText");
        description.setWrapText(true);
        description.setMaxWidth(480);
        description.setTextAlignment(TextAlignment.CENTER);
        description.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(25);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button getStartedBtn = new Button("🌸 Get Started");
        getStartedBtn.setId("getStartedBtn");

        Button learnMoreBtn = new Button("🌿 Learn More");
        learnMoreBtn.setId("learnMoreBtn");

        buttonBox.getChildren().addAll(getStartedBtn, learnMoreBtn);

        heroSection.getChildren().addAll(iconCircle, headingBox, description, buttonBox);

        mainContainer.getChildren().addAll(navbar, heroSection);
        root.getChildren().add(mainContainer);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/com/example/javafx_project/styles.css").toExternalForm());

        primaryStage.setTitle("PLANORA - Landing Page");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        addHoverAnimation(signInBtn);
        signInBtn.setOnAction(e -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/com/example/javafx_project/signin-view.fxml"));
                Scene signInScene = new Scene(loader.load(), 800, 600);
                Stage stage = (Stage) signInBtn.getScene().getWindow();
                stage.setScene(signInScene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        FadeTransition fade = new FadeTransition(Duration.millis(900), heroSection);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void addHoverAnimation(Button button) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(180), button);
        scaleUp.setToX(1.08);
        scaleUp.setToY(1.08);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(180), button);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        button.setOnMouseEntered(e -> scaleUp.playFromStart());
        button.setOnMouseExited(e -> scaleDown.playFromStart());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
