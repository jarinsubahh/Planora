package com.example.javafx_project;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlanoraDashboard extends Application {

    private VBox tasksContainer;
    private Pane overlayPane;
    private List<Task> taskList = new ArrayList<>();
    private Label percent;
    private YearMonth currentYearMonth = YearMonth.now();

    @Override
    public void start(Stage primaryStage) {
        loadTasksFromFile();
        // Gradient background
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

        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label sparkle = new Label("✨");
        sparkle.setId("logoIcon");
        Label logoText = new Label("PLANORA");
        logoText.setId("logoText");
        logoBox.getChildren().addAll(sparkle, logoText);

        VBox navItems = new VBox(18);
        navItems.setPadding(new Insets(40, 0, 0, 0));
        Label dashboardItem = createNavItem("📊 Dashboard", true);
        Label todayItem = createNavItem("🌸 Today", false);
        Label upcomingItem = createNavItem("📅 Upcoming", false);
        Label completedItem = createNavItem("✅ Completed", false);
        Label analyticsItem = createNavItem("📈 Analytics", false);
        Label focusItem = createNavItem("🌙 Focus Mode", false);
        Label calendarItem = createNavItem("📅 Calendar", false);

        todayItem.setOnMouseClicked(e -> showTodayTasks());
        upcomingItem.setOnMouseClicked(e -> showUpcomingTasks());
        completedItem.setOnMouseClicked(e -> showCompletedTasks());
        calendarItem.setOnMouseClicked(e -> showCalendarView());

        navItems.getChildren().addAll(dashboardItem, todayItem, upcomingItem,
                completedItem, analyticsItem, focusItem, calendarItem);

        VBox bottomItems = new VBox(18);
        bottomItems.setAlignment(Pos.BOTTOM_LEFT);
        bottomItems.setPadding(new Insets(0, 0, 30, 0));
        Label settingsItem = createNavItem("⚙ Settings", false);
        Label logoutItem = createNavItem("🚪 Logout", false);
        logoutItem.setOnMouseClicked(e -> {
            try {
                PlanoraLandingPage landing = new PlanoraLandingPage();
                landing.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
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
        Label greeting = new Label("Good Morning, Planner 🌸");
        greeting.setId("greetingText");
        Label date = new Label(LocalDate.now().toString());
        date.setId("dateText");
        greetingBox.getChildren().addAll(greeting, date);

        Circle progressCircle = new Circle(30);
        progressCircle.setId("progressCircle");
        percent = new Label("0%");
        percent.setId("progressText");
        StackPane progressPane = new StackPane(progressCircle, percent);

        Button addTaskBtn = new Button("+ Add Task");
        addTaskBtn.setId("addTaskBtn");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(greetingBox, spacer, progressPane, addTaskBtn);

        // Stat cards
        HBox statCards = new HBox(18);
        statCards.setAlignment(Pos.CENTER_LEFT);
        statCards.getChildren().addAll(
                createStatCard("🌸", "0", "Today"),
                createStatCard("⏳", "0", "Upcoming"),
                createStatCard("🔥", "0", "Streak")
        );

        // Tasks section
        tasksContainer = new VBox(15);
        tasksContainer.setPadding(new Insets(10));
        refreshTaskList();
        saveTasksToFile();
        ScrollPane tasksScroll = new ScrollPane(tasksContainer);
        tasksScroll.setFitToWidth(true);
        tasksScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        tasksScroll.setPrefHeight(300);

        mainContent.getChildren().addAll(header, statCards, tasksScroll);

        HBox layout = new HBox(sidebar, mainContent);

        // Overlay Pane
        overlayPane = new StackPane();
        overlayPane.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
        overlayPane.setVisible(false);

        // Modal container
        VBox modalContainer = new VBox(18);
        modalContainer.setPrefSize(500, 520);
        modalContainer.setPadding(new Insets(30));
        modalContainer.setAlignment(Pos.TOP_LEFT);
        modalContainer.setStyle("-fx-background-color: white; -fx-background-radius: 25; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 30, 0, 0, 10);");

        // Header row
        HBox modalHeader = new HBox();
        Label modalTitle = new Label("Add New Task");
        modalTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2E2A3B;");
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18px;");
        closeBtn.setOnAction(e -> hideOverlay());
        modalHeader.getChildren().addAll(modalTitle, headerSpacer, closeBtn);

        // Title field
        Label titleLabel = new Label("Title");
        TextField titleField = new TextField();
        titleField.setPromptText("What needs to be done?");
        titleField.setStyle("-fx-background-radius: 15; -fx-background-color: #F5F4F8; -fx-padding: 12;");

        // Description
        Label descLabel = new Label("Description");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Add details...");
        descArea.setPrefHeight(80);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-background-radius: 15; -fx-background-color: #F5F4F8; -fx-padding: 12;");

        // Deadline + Category
        HBox deadlineCategoryRow = new HBox(20);
        VBox deadlineBox = new VBox(5);
        Label deadlineLabel = new Label("Deadline");
        DatePicker datePicker = new DatePicker();
        deadlineBox.getChildren().addAll(deadlineLabel, datePicker);

        VBox categoryBox = new VBox(5);
        Label categoryLabel = new Label("Category");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Today", "Upcoming", "Completed");
        categoryBox.getChildren().addAll(categoryLabel, categoryCombo);

        deadlineCategoryRow.getChildren().addAll(deadlineBox, categoryBox);

        // Priority
        Label priorityLabel = new Label("Priority");
        ToggleGroup priorityGroup = new ToggleGroup();
        HBox priorityRow = new HBox(10);
        String[] priorities = {"Low", "Medium", "High", "Urgent"};
        for (String p : priorities) {
            ToggleButton btn = new ToggleButton(p);
            btn.setToggleGroup(priorityGroup);
            btn.setStyle("-fx-background-radius: 15; -fx-padding: 8 16;");
            priorityRow.getChildren().add(btn);
        }

        // Save button
        Button saveBtn = new Button("Save Task");
        saveBtn.setStyle("-fx-background-radius: 20; -fx-background-color: linear-gradient(to right, #A78BFA, #F472B6); "
                + "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 600;");
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        saveBtn.setOnAction(e -> {
            String title = titleField.getText();
            String desc = descArea.getText();
            LocalDate deadline = datePicker.getValue();
            String category = categoryCombo.getValue();
            ToggleButton selectedPriority = (ToggleButton) priorityGroup.getSelectedToggle();
            String priority = selectedPriority != null ? selectedPriority.getText() : "Low";

            if (title.isEmpty()) return;

            Task newTask = new Task(title, desc, deadline, category, priority);
            taskList.add(newTask);

            refreshTaskList();
            saveTasksToFile();
            hideOverlay();
        });

        modalContainer.getChildren().addAll(
                modalHeader,
                titleLabel, titleField,
                descLabel, descArea,
                deadlineCategoryRow,
                priorityLabel, priorityRow,
                saveBtn
        );

        overlayPane.getChildren().add(modalContainer);
        StackPane.setAlignment(modalContainer, Pos.CENTER);

        root.getChildren().addAll(layout, overlayPane);

        addTaskBtn.setOnAction(e -> showOverlay());

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/com/example/javafx_project/dashboard.css").toExternalForm());

        primaryStage.setTitle("PLANORA - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Overlay animations
    private void showOverlay() {
        overlayPane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlayPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), overlayPane);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
    }

    private void hideOverlay() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlayPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> overlayPane.setVisible(false));
        fadeOut.play();
    }

    // Refresh task list
    private void refreshTaskList() {
        tasksContainer.getChildren().clear();
        Label header = new Label("🌸 Today’s Tasks");
        header.setId("tasksHeader");
        tasksContainer.getChildren().add(header);

        int totalToday = 0;
        int completedToday = 0;

        for (Task task : taskList) {
            if (task.getDeadline() != null && task.getDeadline().equals(LocalDate.now())) {
                totalToday++;
                if (task.isCompleted()) completedToday++;
            }
            if (!task.isCompleted()) {
                VBox card = createTaskCard(task);
                tasksContainer.getChildren().add(card);
            }
        }

        int percentValue = totalToday == 0 ? 0 : (completedToday * 100 / totalToday);
        percent.setText(percentValue + "%");
    }

    // Task card design
    private VBox createTaskCard(Task task) {
        HBox card = new HBox();
        card.setPrefSize(520, 130);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 25; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 20, 0, 0, 8);");

        Region strip = new Region();
        strip.setPrefWidth(8);
        strip.setStyle("-fx-background-color:" + getPriorityColor(task.getPriority()) + "; "
                + "-fx-background-radius: 25 0 0 25;");

        VBox content = new VBox(8);
        content.setPadding(new Insets(10));

        Label title = new Label(task.getTitle());
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");
        Label desc = new Label(task.getDescription());
        desc.setStyle("-fx-text-fill:#6B7280; -fx-font-size:13px;");

        Label deadline = new Label(task.getDeadline() != null ? task.getDeadline().toString() : "No deadline");
        Label priorityBadge = new Label(task.getPriority());
        priorityBadge.setStyle("-fx-background-radius:12; -fx-padding:5 10; -fx-background-color:" + getPriorityColor(task.getPriority()) + ";");

        HBox infoRow = new HBox(15, priorityBadge, deadline);

        Button doneBtn = new Button("✔ Done");
        doneBtn.setOnAction(e -> {
            task.setCompleted(true);
            saveTasksToFile();
            refreshTaskList();
        });

        Button deleteBtn = new Button("🗑");
        deleteBtn.setOnAction(e -> { taskList.remove(task); refreshTaskList(); });

        HBox actionRow = new HBox(10, doneBtn, deleteBtn);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().addAll(title, desc, infoRow, actionRow);

        card.getChildren().addAll(strip, content);

        card.setOnMouseEntered(e -> { card.setScaleX(1.02); card.setScaleY(1.02); });
        card.setOnMouseExited(e -> { card.setScaleX(1); card.setScaleY(1); });

        return new VBox(card);
    }

    private String getPriorityColor(String priority) {
        switch (priority) {
            case "Low": return "#DCFCE7";
            case "Medium": return "#DCEEFE";
            case "High": return "#E9D5FF";
            case "Urgent": return "#FECACA";
            default: return "#F5F4F8";
        }
    }

    // Filtering
    private void showTodayTasks() {
        tasksContainer.getChildren().clear();
        for (Task task : taskList) {
            if (task.getDeadline() != null && task.getDeadline().equals(LocalDate.now())) {
                tasksContainer.getChildren().add(createTaskCard(task));
            }
        }
    }

    private void showUpcomingTasks() {
        tasksContainer.getChildren().clear();
        for (Task task : taskList) {
            if (task.getDeadline() != null && task.getDeadline().isAfter(LocalDate.now())) {
                tasksContainer.getChildren().add(createTaskCard(task));
            }
        }
    }

    private void showCompletedTasks() {
        tasksContainer.getChildren().clear();
        for (Task task : taskList) {
            if (task.isCompleted()) {
                tasksContainer.getChildren().add(createTaskCard(task));
            }
        }
    }
    private void saveTasksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tasks.dat"))) {
            oos.writeObject(taskList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private void loadTasksFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tasks.dat"))) {
            taskList = (List<Task>) ois.readObject();
        } catch (Exception e) {
            taskList = new ArrayList<>();
        }
    }

    private void showTasksByDate(LocalDate date) {
        tasksContainer.getChildren().clear();
        for (Task task : taskList) {
            if (task.getDeadline() != null && task.getDeadline().equals(date)) {
                tasksContainer.getChildren().add(createTaskCard(task));
            }
        }
    }

    // Calendar view
    private void showCalendarView() {
        tasksContainer.getChildren().clear();

        BorderPane calendarPane = new BorderPane();
        HBox header = new HBox(20);
        Button prevMonth = new Button("<");
        Button nextMonth = new Button(">");
        Label monthLabel = new Label(currentYearMonth.getMonth() + " " + currentYearMonth.getYear());
        header.setAlignment(Pos.CENTER);
        header.getChildren().addAll(prevMonth, monthLabel, nextMonth);

        prevMonth.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            showCalendarView();
        });
        nextMonth.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            showCalendarView();
        });

        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() % 7;

        int row = 1, col = dayOfWeekOffset;
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            Button dayBtn = new Button(String.valueOf(day));
            dayBtn.setPrefSize(60, 60);
            dayBtn.getStyleClass().add("calendar-button");

            if (date.equals(LocalDate.now())) {
                dayBtn.setStyle("-fx-background-color:#C4B5FD; -fx-background-radius:15; -fx-text-fill:white;");
            }

            boolean hasTasks = taskList.stream().anyMatch(t -> !t.isCompleted() && date.equals(t.getDeadline()));
            if (hasTasks) {
                dayBtn.setText(dayBtn.getText() + " •");
            }

            dayBtn.setOnAction(e -> showTasksByDate(date));

            calendarGrid.add(dayBtn, col, row);

            col++;
            if (col > 6) { col = 0; row++; }
        }

        calendarPane.setTop(header);
        calendarPane.setCenter(calendarGrid);

        tasksContainer.getChildren().add(calendarPane);
    }


    // Utility
    private Label createNavItem(String text, boolean active) {
        Label item = new Label(text);
        item.setPrefSize(150, 40);
        item.setPadding(new Insets(0, 0, 0, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setId(active ? "navItemActive" : "navItem");
        item.setStyle("-fx-font-family: 'Segoe UI Emoji';");
        return item;
    }

    private VBox createStatCard(String icon, String number, String label) {
        VBox card = new VBox(6);
        card.setPrefSize(120, 100);
        card.setAlignment(Pos.CENTER);
        card.setId("statCard");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px; -fx-font-family: 'Segoe UI Emoji';");

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
