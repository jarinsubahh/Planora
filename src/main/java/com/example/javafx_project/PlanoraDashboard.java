package com.example.javafx_project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class PlanoraDashboard extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Root background gradient
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#f8f5f9")),
                new Stop(1, Color.web("#f2f6f8"))
        };
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);

        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Sidebar
        VBox sidebar = new VBox(18);
        sidebar.setPrefSize(190, 600);
        sidebar.setPadding(new Insets(20, 0, 20, 20));
        sidebar.setId("sidebar");

        // Logo
        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label sparkle = new Label("✨");
        sparkle.setId("logoIcon");
        Label logoText = new Label("PLANORA");
        logoText.setId("logoText");
        logoBox.getChildren().addAll(sparkle, logoText);

        // Nav items
        VBox navItems = new VBox(18);
        navItems.setPadding(new Insets(40, 0, 0, 0));

        Label dashboardItem = createNavItem("📊 Dashboard", true);
        Label todayItem = createNavItem("🌸 Today", false);
        Label upcomingItem = createNavItem("📅 Upcoming", false);
        Label completedItem = createNavItem("✅ Completed", false);
        Label analyticsItem = createNavItem("📈 Analytics", false);
        Label focusItem = createNavItem("🌙 Focus Mode", false);
        Label rewardsItem = createNavItem("🎀 Rewards", false);

        navItems.getChildren().addAll(dashboardItem, todayItem, upcomingItem,
                completedItem, analyticsItem, focusItem, rewardsItem);

        // Bottom section
        VBox bottomItems = new VBox(18);
        bottomItems.setAlignment(Pos.BOTTOM_LEFT);
        bottomItems.setPadding(new Insets(0, 0, 30, 0));
        Label settingsItem = createNavItem("⚙ Settings", false);
        Label logoutItem = createNavItem("🚪 Logout", false);
        bottomItems.getChildren().addAll(settingsItem, logoutItem);

        sidebar.getChildren().addAll(logoBox, navItems, bottomItems);

        // Main content
        VBox mainContent = new VBox(20);
        mainContent.setPrefSize(610, 600);
        mainContent.setPadding(new Insets(30));
        mainContent.setId("mainContent");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        VBox greetingBox = new VBox(5);
        Label greeting = new Label("Good Evening, Planner 🌸");
        greeting.setId("greetingText");
        Label date = new Label("Wednesday, February 18");
        date.setId("dateText");
        greetingBox.getChildren().addAll(greeting, date);

        // Progress circle placeholder
        Circle progressCircle = new Circle(30);
        progressCircle.setId("progressCircle");
        Label percent = new Label("25%");
        percent.setId("progressText");
        StackPane progressPane = new StackPane(progressCircle, percent);

        // Add Task button
        Button addTaskBtn = new Button("Add Task");
        addTaskBtn.setId("addTaskBtn");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(greetingBox, spacer, progressPane, addTaskBtn);

        // Stat cards row
        HBox statCards = new HBox(18);
        statCards.setAlignment(Pos.CENTER_LEFT);

        VBox todayCard = createStatCard("🌸", "3", "Today");
        VBox upcomingCard = createStatCard("⏳", "1", "Upcoming");
        VBox streakCard = createStatCard("🔥", "2", "Streak");
        VBox starsCard = createStatCard("⭐", "15", "Stars");

        statCards.getChildren().addAll(todayCard, upcomingCard, streakCard, starsCard);

        // Tasks section placeholder
        VBox tasksSection = new VBox(10);
        Label tasksHeader = new Label("🌸 Today’s Tasks");
        tasksHeader.setId("tasksHeader");
        tasksSection.getChildren().add(tasksHeader);

        mainContent.getChildren().addAll(header, statCards, tasksSection);

        // Layout
        HBox layout = new HBox(sidebar, mainContent);
        root.getChildren().add(layout);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());

        primaryStage.setTitle("PLANORA - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Label createNavItem(String text, boolean active) {
        Label item = new Label(text);
        item.setPrefSize(150, 40);
        item.setPadding(new Insets(0, 0, 0, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setId(active ? "navItemActive" : "navItem");
        return item;
    }

    private VBox createStatCard(String icon, String number, String label) {
        VBox card = new VBox(6);
        card.setPrefSize(120, 100);
        card.setAlignment(Pos.CENTER);
        card.setId("statCard");

        Label iconLabel = new Label(icon);
        Label numberLabel = new Label(number);
        numberLabel.setId("statNumber");
        Label textLabel = new Label(label);
        textLabel.setId("statLabel");

        card.getChildren().addAll(iconLabel, numberLabel, textLabel);
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
