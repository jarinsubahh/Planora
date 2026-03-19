package com.example.javafx_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.YearMonth;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    @FXML
    private VBox taskListVBox;

    @FXML
    private VBox mainContent;

    @FXML
    private Label todayStatsLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label upcomingStatsLabel;

    @FXML
    private Label streakStatsLabel;

    @FXML
    private Label totalStatsLabel;

    private List<Task> currentTasks;

    // Calendar integration helpers
    private java.util.List<javafx.scene.Node> originalMainChildren;
    private java.util.List<String> originalStylesheets;

    @FXML
    private void initialize() {
        // capture original main content children so calendar can replace and restore
        originalMainChildren = new java.util.ArrayList<>(mainContent.getChildren());
        // Set current date in header
        try {
            if (dateLabel != null) {
                dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } else {
                // Fallback: lookup by style class if fx:id not set in FXML
                try {
                    Label df = (Label) mainContent.lookup(".date");
                    if (df != null) {
                        df.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        // originalStylesheets will be captured lazily in handleFocusMode
        // Load all tasks by default
        loadTasks();
        updateStatistics();
    }

    @FXML
    private void handleDashboard() {
        // If calendar view is active, restore original main content
        restoreOriginalMainContent();
        loadTasks();
        updateStatistics();
    }

    @FXML
    private void handleToday() {
        restoreOriginalMainContent();
        loadTodayTasks();
        updateStatistics();
    }

    @FXML
    private void handleUpcoming() {
        restoreOriginalMainContent();
        loadUpcomingTasks();
        updateStatistics();
    }

    @FXML
    private void handleCompleted() {
        restoreOriginalMainContent();
        loadCompletedTasks();
        updateStatistics();
    }

    @FXML
    private void handleAnalytics() {
        restoreOriginalMainContent();
        // Handle Analytics navigation
        System.out.println("Analytics clicked");
    }



    // Expose task card creation for calendar to reuse same card design
    public HBox createTaskCardForCalendar(Task task) {
        return createTaskCard(task);
    }

    // Calendar state
    private YearMonth currentYearMonth;
    private java.time.LocalDate selectedDate;
    private GridPane calendarGrid;
    private VBox calendarPageRoot;
    private VBox dateTasksVBox;

    @FXML
    private void handleCalendar() {
        // Always clear mainContent first to prevent duplicates
        mainContent.getChildren().clear();

        // Initialize month
        currentYearMonth = YearMonth.now();

        // Build calendar page root
        calendarPageRoot = new VBox(20);
        calendarPageRoot.setPadding(new Insets(20));

        // Build header and grid
        buildCalendarView();

        // Add to mainContent
        mainContent.getChildren().add(calendarPageRoot);
    }

    private void buildCalendarView() {
        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER);

        Button prev = new Button("<");
        Button next = new Button(">");
        Label monthLabel = new Label();
        monthLabel.getStyleClass().add("calendar-month-label");
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());

        prev.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
            buildCalendarGrid();
        });
        next.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
            buildCalendarGrid();
        });

        header.getChildren().addAll(prev, monthLabel, next);

        // Calendar grid container
        calendarGrid = new GridPane();
        calendarGrid.setHgap(20);
        calendarGrid.setVgap(20);
        calendarGrid.setPadding(new Insets(20));

        ScrollPane calendarScroll = new ScrollPane(calendarGrid);
        calendarScroll.setFitToWidth(true);
        calendarScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        calendarScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Assemble
        calendarPageRoot.getChildren().clear();
        calendarPageRoot.getChildren().addAll(header, calendarScroll);

        buildCalendarGrid();
    }

    private void buildCalendarGrid() {
        calendarGrid.getChildren().clear();

        // Weekday headers
        String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (int i = 0; i < 7; i++) {
            Label l = new Label(days[i]);
            l.setStyle("-fx-font-weight: 600; -fx-text-fill: #666;");
            calendarGrid.add(l, i, 0);
            GridPane.setHalignment(l, javafx.geometry.HPos.CENTER);
        }

        java.time.LocalDate firstDay = currentYearMonth.atDay(1);
        int startIndex = firstDay.getDayOfWeek().getValue() % 7; // Sunday=0
        int length = currentYearMonth.lengthOfMonth();

        int rowOffset = 1;
        int day = 1;
        int totalCells = startIndex + length;
        int rows = (int) Math.ceil(totalCells / 7.0);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < 7; c++) {
                int cellIndex = r * 7 + c;
                if (cellIndex < startIndex || day > length) {
                    StackPane empty = new StackPane();
                    empty.setPrefSize(80, 80);
                    calendarGrid.add(empty, c, r + rowOffset);
                } else {
                    java.time.LocalDate date = currentYearMonth.atDay(day);

                    StackPane dateCell = new StackPane();
                    dateCell.setPrefSize(80, 80);
                    dateCell.setStyle("-fx-background-color: #F8F8FF; -fx-background-radius: 20; -fx-border-radius: 20;");

                    VBox content = new VBox(6);
                    content.setAlignment(Pos.CENTER);
                    Label dayLabel = new Label(String.valueOf(day));
                    dayLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #2E2E3A;");

                    if (hasTaskOnDate(date)) {
                        Circle dot = new Circle(3, javafx.scene.paint.Color.web("#7A73FF"));
                        content.getChildren().addAll(dayLabel, dot);
                    } else {
                        content.getChildren().add(dayLabel);
                    }

                    dateCell.getChildren().add(content);

                    // Hover effect
                    dateCell.setOnMouseEntered(ev -> dateCell.setStyle("-fx-background-color: #EFEFFF; -fx-background-radius: 20;"));
                    dateCell.setOnMouseExited(ev -> dateCell.setStyle("-fx-background-color: #F8F8FF; -fx-background-radius: 20;"));

                    // Click -> show tasks for this date (replace calendar)
                    dateCell.setOnMouseClicked(ev -> showTasksForDate(date));

                    calendarGrid.add(dateCell, c, r + rowOffset);
                    day++;
                }
            }
        }
    }

    private void showTasksForDate(java.time.LocalDate date) {
        // Clear calendar and show task view
        calendarPageRoot.getChildren().clear();

        Label title = new Label("Tasks for " + date.format(java.time.format.DateTimeFormatter.ofPattern("MMMM d")));
        title.getStyleClass().add("task-title");

        VBox tasksArea = new VBox(10);
        tasksArea.setPadding(new Insets(10));

        // Only show pending (not completed) tasks for the selected date
        List<Task> tasks = TaskService.getTasksByUser(UserManager.currentUser).stream()
                .filter(t -> t.getDeadline() != null && t.getDeadline().equals(date) && !t.isCompleted())
                .toList();

        if (tasks.isEmpty()) {
            Label empty = new Label("No tasks scheduled for this date.");
            empty.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
            tasksArea.setAlignment(Pos.CENTER);
            tasksArea.getChildren().add(empty);
        } else {
            for (Task t : tasks) {
                tasksArea.getChildren().add(createTaskCard(t));
            }
        }

        // Add a spacer and the tasks
        calendarPageRoot.getChildren().addAll(title, tasksArea);
    }

    private boolean hasTaskOnDate(java.time.LocalDate date) {
        // Only consider tasks that are not completed
        return TaskService.getTasksByUser(UserManager.currentUser).stream()
                .anyMatch(t -> t.getDeadline() != null && t.getDeadline().equals(date) && !t.isCompleted());
    }

    private void restoreOriginalMainContent() {
        if (originalMainChildren == null) return;
        mainContent.getChildren().clear();
        mainContent.getChildren().addAll(originalMainChildren);
        if (originalStylesheets != null) {
            mainContent.getScene().getStylesheets().clear();
            mainContent.getScene().getStylesheets().addAll(originalStylesheets);
        }
        // Update header date in case app has been open across days
        try {
            if (dateLabel != null) {
                dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } else {
                try {
                    Label df = (Label) mainContent.lookup(".date");
                    if (df != null) df.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        // Refresh content
        loadTasks();
        updateStatistics();
    }

    @FXML
    private void handleFocusMode() {
        restoreOriginalMainContent();
        try {
            if (originalStylesheets == null) {
                originalStylesheets = new java.util.ArrayList<>(mainContent.getScene().getStylesheets());
            }
            // Prepare focus mode stylesheet
            String focusCss = getClass().getResource("/com/example/javafx_project/focus-mode.css").toExternalForm();

            // Load focus mode FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/focus-mode.fxml"));
            Parent focusRoot = loader.load();

            // Get controller and wire exit callback (will close stage and restore)
            FocusModeController controller = loader.getController();

            Scene focusScene = new Scene(focusRoot, 800, 600);
            focusScene.getStylesheets().add(focusCss);

            Stage focusStage = new Stage();
            focusStage.setTitle("Focus Mode");
            focusStage.setScene(focusScene);
            focusStage.initModality(Modality.APPLICATION_MODAL);
            focusStage.initOwner(mainContent.getScene().getWindow());
            focusStage.setResizable(false);

            controller.setOnExit(() -> {
                focusStage.close();
                // restore styles and content
                restoreOriginalMainContent();
            });

            focusStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    private void handleSettings() {
        restoreOriginalMainContent();
        // Handle Settings navigation
        System.out.println("Settings clicked");
    }

    @FXML
    private void handleLogout() {
        UserManager.logout();
        try {
            javafx.scene.Scene scene = taskListVBox.getScene();
            if (scene == null) {
                scene = mainContent.getScene();
            }
            if (scene == null) {
                System.err.println("Error during logout: no active scene");
                return;
            }
            Stage stage = (Stage) scene.getWindow();
            PlanoraLandingPage landing = new PlanoraLandingPage();
            landing.start(stage);
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTask() {
        restoreOriginalMainContent();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/addTask-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/javafx_project/addTask.css").toExternalForm());

            Stage modalStage = new Stage();
            modalStage.setTitle("Add New Task");
            modalStage.setScene(scene);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            // Refresh tasks and stats after adding
            loadTasks();
            updateStatistics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        currentTasks = TaskService.getTasksByUser(UserManager.currentUser);
        List<Task> nonCompletedTasks = currentTasks.stream()
            .filter(t -> !t.isCompleted())
            .toList();
        displayTasks(nonCompletedTasks);
    }

    private void loadTodayTasks() {
        currentTasks = TaskService.getTodayTasks(UserManager.currentUser);
        displayTasks(currentTasks);
    }

    private void loadUpcomingTasks() {
        currentTasks = TaskService.getUpcomingTasks(UserManager.currentUser);
        displayTasks(currentTasks);
    }

    private void loadCompletedTasks() {
        currentTasks = TaskService.getCompletedTasks(UserManager.currentUser);
        displayTasks(currentTasks);
    }

    private void displayTasks(List<Task> tasks) {
        taskListVBox.getChildren().clear();
        for (Task task : tasks) {
            taskListVBox.getChildren().add(createTaskCard(task));
        }
    }

    private HBox createTaskCard(Task task) {
        HBox taskCard = new HBox();
        taskCard.getStyleClass().add("task-card");
        taskCard.setSpacing(15);

        // Priority strip
        Rectangle priorityStrip = new Rectangle(6, 120);
        priorityStrip.setArcWidth(10);
        priorityStrip.setArcHeight(10);
        priorityStrip.setFill(getPriorityColor(task.getPriority()));

        // Content VBox
        VBox contentBox = new VBox();
        contentBox.setSpacing(5);

        Label titleLabel = new Label(task.getTitle());
        titleLabel.getStyleClass().add("task-title");

        Label descLabel = new Label(task.getDescription() != null ? task.getDescription() : "");
        descLabel.getStyleClass().add("task-description");

        // Priority badge
        Label priorityBadge = new Label(task.getPriority());
        priorityBadge.getStyleClass().add("priority-badge");
        priorityBadge.getStyleClass().add("priority-" + task.getPriority().toLowerCase());

        // Deadline
        Label deadlineLabel = new Label(task.getDeadline() != null ?
            "Due: " + task.getDeadline().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "");
        deadlineLabel.getStyleClass().add("task-deadline");

        contentBox.getChildren().addAll(titleLabel, descLabel, priorityBadge, deadlineLabel);

        // Buttons
        VBox buttonBox = new VBox();
        buttonBox.setSpacing(5);

        Button doneButton = new Button("✔ Done");
        doneButton.getStyleClass().add("done-button");
        doneButton.setOnAction(e -> markTaskDone(task));

        Button deleteButton = new Button("🗑 Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteTask(task));

        buttonBox.getChildren().addAll(doneButton, deleteButton);

        taskCard.getChildren().addAll(priorityStrip, contentBox, buttonBox);
        return taskCard;
    }

    private Color getPriorityColor(String priority) {
        return switch (priority.toLowerCase()) {
            case "low" -> Color.web("#7DD3FC");
            case "medium" -> Color.web("#FCD34D");
            case "high" -> Color.web("#FB923C");
            case "urgent" -> Color.web("#F472B6");
            default -> Color.web("#94A3B8");
        };
    }

    private void markTaskDone(Task task) {
        TaskService.markCompleted(task.getId());
        loadTasks();
        updateStatistics();
    }

    private void deleteTask(Task task) {
        TaskService.deleteTask(task.getId());
        loadTasks();
        updateStatistics();
    }

    private void updateStatistics() {
        List<Task> allTasks = TaskService.getTasksByUser(UserManager.currentUser);
        LocalDate today = LocalDate.now();

        long todayCount = allTasks.stream()
            .filter(t -> t.getDeadline() != null && 
                         t.getDeadline().equals(today) && 
                         !t.isCompleted())
            .count();

        long upcomingCount = allTasks.stream()
            .filter(t -> t.getDeadline() != null && 
                         t.getDeadline().isAfter(today) && 
                         !t.isCompleted())
            .count();

        long totalCount = allTasks.size();

        long streakCount = calculateStreak(allTasks);

        todayStatsLabel.setText(String.valueOf(todayCount));
        upcomingStatsLabel.setText(String.valueOf(upcomingCount));
        totalStatsLabel.setText(String.valueOf(totalCount));
        streakStatsLabel.setText(String.valueOf(streakCount));
    }

    private long calculateStreak(List<Task> allTasks) {
        List<LocalDate> completionDates = allTasks.stream()
            .filter(Task::isCompleted)
            .map(t -> t.getDeadline() != null ? t.getDeadline() : t.getCreatedAt().toLocalDate())
            .distinct()
            .sorted()
            .toList();

        if (completionDates.isEmpty()) {
            return 0;
        }

        long streak = 0;
        LocalDate currentDate = LocalDate.now();

        for (int i = completionDates.size() - 1; i >= 0; i--) {
            LocalDate completionDate = completionDates.get(i);
            if (completionDate.equals(currentDate) || completionDate.equals(currentDate.minusDays(streak))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }
}
