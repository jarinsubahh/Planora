package com.example.javafx_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.FlowPane;
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
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.List;

public class DashboardController {

    @FXML
    private VBox taskListVBox;

    @FXML
    private VBox mainContent;

    @FXML
    private Label todayStatsLabel;

    @FXML
    private Label upcomingStatsLabel;

    @FXML
    private Label streakStatsLabel;

    @FXML
    private Label totalStatsLabel;

    @FXML
    private Label greeting;

    @FXML
    private Label headerDateLabel;

    @FXML
    private Label taskSectionTitleLabel;

    @FXML
    private Button dashboardBtn, todayBtn, upcomingBtn, completedBtn, focusBtn,calendarBtn, analyticsBtn,spaceBtn, settingsBtn;

    @FXML
    private Button filterAllBtn, filterStudyBtn, filterWorkBtn, filterPersonalBtn, filterHealthBtn, filterOtherBtn;

    private List<Task> currentTasks;
    private String selectedCategory = "All";

    // Calendar integration helpers
    private java.util.List<javafx.scene.Node> originalMainChildren;

    private Space currentActiveSpace = null;

    @FXML
    private void initialize() {
        // capture original main content children so calendar can replace and restore
        originalMainChildren = new java.util.ArrayList<>(mainContent.getChildren());
        updateHeaderDate();
        updateGreeting();
        // All data is now loaded directly from cloud on-demand
        setTaskSectionHeading("✿ Today's Tasks");
        loadTasks();
    }

    private void updateGreeting() {
        if (greeting != null && UserManager.currentUser != null) {
            greeting.setText("Good Morning, " + UserManager.currentUser + " ❄");
        }
    }

    private void updateHeaderDate() {
        if (headerDateLabel != null) {
            headerDateLabel.setText(LocalDate.now().format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.ENGLISH)));
        }
    }

    private void setTaskSectionHeading(String heading) {
        if (taskSectionTitleLabel != null) {
            taskSectionTitleLabel.setText(heading);
        }
    }


    @FXML
    private void handleDashboard() {
        setActiveButton(dashboardBtn);
        restoreOriginalMainContent();
        loadTasks();
    }

    @FXML
    private void handleToday() {
        setActiveButton(todayBtn);
        restoreOriginalMainContent();
        setTaskSectionHeading("✿ Today's Tasks");
        loadTodayTasks();
    }

    @FXML
    private void handleUpcoming() {
        setActiveButton(upcomingBtn);
        restoreOriginalMainContent();
        setTaskSectionHeading("✿ Upcoming Task");
        loadUpcomingTasks();
    }

    @FXML
    private void handleCompleted() {
        setActiveButton(completedBtn);
        restoreOriginalMainContent();
        setTaskSectionHeading("✿ Completed Task");
        loadCompletedTasks();
    }

    @FXML
    private void handleAnalytics() {
        setActiveButton(analyticsBtn);
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
    private java.util.Map<java.time.LocalDate, Boolean> taskDateCache;

    @FXML
    private void handleCalendar() {
        setActiveButton(calendarBtn);
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

//    private void buildCalendarView() {
//        // Header
//        HBox header = new HBox(20);
//        header.setAlignment(Pos.CENTER);
//
//        Button prev = new Button("<");
//        Button next = new Button(">");
//        Label monthLabel = new Label();
//        monthLabel.getStyleClass().add("calendar-month-label");
//        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
//
//        prev.setOnAction(e -> {
//            currentYearMonth = currentYearMonth.minusMonths(1);
//            monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
//            buildCalendarGrid();
//        });
//        next.setOnAction(e -> {
//            currentYearMonth = currentYearMonth.plusMonths(1);
//            monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
//            buildCalendarGrid();
//        });
//
//        header.getChildren().addAll(prev, monthLabel, next);
//
//        // Calendar grid container
//        calendarGrid = new GridPane();
//        calendarGrid.setHgap(20);
//        calendarGrid.setVgap(20);
//        calendarGrid.setPadding(new Insets(20));
//
//        ScrollPane calendarScroll = new ScrollPane(calendarGrid);
//        calendarScroll.setFitToWidth(true);
//        calendarScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        calendarScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//
//        // Assemble
//        calendarPageRoot.getChildren().clear();
//        calendarPageRoot.getChildren().addAll(header, calendarScroll);
//
//        buildCalendarGrid();
//    }
//
//    private void buildCalendarGrid() {
//        calendarGrid.getChildren().clear();
//
//        // Weekday headers
//        String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
//        for (int i = 0; i < 7; i++) {
//            Label l = new Label(days[i]);
//            l.getStyleClass().add("calendar-weekday-label");
//            calendarGrid.add(l, i, 0);
//            GridPane.setHalignment(l, javafx.geometry.HPos.CENTER);
//        }
//
//        java.time.LocalDate firstDay = currentYearMonth.atDay(1);
//        int startIndex = firstDay.getDayOfWeek().getValue() % 7; // Sunday=0
//        int length = currentYearMonth.lengthOfMonth();
//
//        int rowOffset = 1;
//        int day = 1;
//        int totalCells = startIndex + length;
//        int rows = (int) Math.ceil(totalCells / 7.0);
//
//        for (int r = 0; r < rows; r++) {
//            for (int c = 0; c < 7; c++) {
//                int cellIndex = r * 7 + c;
//                if (cellIndex < startIndex || day > length) {
//                    StackPane empty = new StackPane();
//                    empty.setPrefSize(80, 80);
//                    calendarGrid.add(empty, c, r + rowOffset);
//                } else {
//                    java.time.LocalDate date = currentYearMonth.atDay(day);
//
//                    StackPane dateCell = new StackPane();
//                    dateCell.setPrefSize(80, 80);
//                    dateCell.getStyleClass().add("calendar-date-cell");
//
//                    VBox content = new VBox(6);
//                    content.setAlignment(Pos.CENTER);
//                    Label dayLabel = new Label(String.valueOf(day));
//                    dayLabel.getStyleClass().add("calendar-day-number");
//
//                    if (hasTaskOnDate(date)) {
//                        Circle dot = new Circle(3, javafx.scene.paint.Color.web("#7A73FF"));
//                        content.getChildren().addAll(dayLabel, dot);
//                    } else {
//                        content.getChildren().add(dayLabel);
//                    }
//
//                    dateCell.getChildren().add(content);
//
//                    // Click -> show tasks for this date (replace calendar)
//                    dateCell.setOnMouseClicked(ev -> showTasksForDate(date));
//
//                    calendarGrid.add(dateCell, c, r + rowOffset);
//                    day++;
//                }
//            }
//        }
//    }
//
//    private void showTasksForDate(java.time.LocalDate date) {
//        // Clear calendar and show task view
//        calendarPageRoot.getChildren().clear();
//
//        Label title = new Label("Tasks for " + date.format(java.time.format.DateTimeFormatter.ofPattern("MMMM d")));
//        title.getStyleClass().add("task-title");
//
//        VBox tasksArea = new VBox(10);
//        tasksArea.setPadding(new Insets(10));
//
//        // Only show pending (not completed) tasks for the selected date
//        List<Task> tasks = TaskService.getTasksByUser(UserManager.currentUser).stream()
//                .filter(t -> t.getDeadline() != null && t.getDeadline().equals(date) && !t.isCompleted())
//                .toList();
//
//        if (tasks.isEmpty()) {
//            Label empty = new Label("No tasks scheduled for this date.");
//            empty.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
//            tasksArea.setAlignment(Pos.CENTER);
//            tasksArea.getChildren().add(empty);
//        } else {
//            for (Task t : tasks) {
//                tasksArea.getChildren().add(createTaskCard(t));
//            }
//        }
//
//        // Add a spacer and the tasks
//        calendarPageRoot.getChildren().addAll(title, tasksArea);
//    }
//
//    private boolean hasTaskOnDate(java.time.LocalDate date) {
//        // Only consider tasks that are not completed
//        return TaskService.getTasksByUser(UserManager.currentUser).stream()
//                .anyMatch(t -> t.getDeadline() != null && t.getDeadline().equals(date) && !t.isCompleted());
//    }
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
            l.getStyleClass().add("calendar-weekday-label");
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
                    empty.setPrefSize(80, 80);  // ← Smaller empty cell size (was 80)
                    calendarGrid.add(empty, c, r + rowOffset);
                } else {
                    java.time.LocalDate date = currentYearMonth.atDay(day);

                    StackPane dateCell = new StackPane();
                    dateCell.setPrefSize(80, 80);  // ← Smaller date cell size (was 80)
                    dateCell.getStyleClass().add("calendar-date-cell");

                    VBox content = new VBox(5);
                    content.setAlignment(Pos.CENTER);
                    Label dayLabel = new Label(String.valueOf(day));
                    dayLabel.getStyleClass().add("calendar-day-number");

                    if (hasTaskOnDate(date)) {
                        // Add purple glassy styling class for dates with tasks
                        dateCell.getStyleClass().add("calendar-date-cell-with-task");
                        // Add small dot indicator (optional, can be removed if just using color)
                        Circle dot = new Circle(2.5, javafx.scene.paint.Color.web("#7A73FF"));
                        content.getChildren().addAll(dayLabel, dot);
                    } else {
                        content.getChildren().add(dayLabel);
                    }

                    dateCell.getChildren().add(content);

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
        currentActiveSpace = null;
        updateHeaderDate();
        setTaskSectionHeading("✿ Today's Tasks");
        loadTasks();
    }

    @FXML
    private void handleFocusMode() {
        setActiveButton(focusBtn);
        restoreOriginalMainContent();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/focus-mode.fxml"));
            javafx.scene.Parent focusRoot = loader.load();
            focusRoot.getStylesheets().add(getClass().getResource("/com/example/javafx_project/focus-mode.css").toExternalForm());

            FocusModeController controller = loader.getController();

            mainContent.getChildren().clear();
            mainContent.getChildren().add(focusRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handleSpace() {
        setActiveButton(spaceBtn);
        showSpaceList();
    }

    @FXML
    private void handleNavigateToSettings() {
        setActiveButton(settingsBtn);
        restoreOriginalMainContent();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/settings.fxml"));
            javafx.scene.Parent settingsRoot = loader.load();

            SettingsController controller = loader.getController();
            controller.setOnBack(() -> handleDashboard());

            mainContent.getChildren().clear();
            mainContent.getChildren().add(settingsRoot);
            VBox.setVgrow(settingsRoot, Priority.ALWAYS);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            com.example.javafx_project.PlanoraLandingPage landing = new com.example.javafx_project.PlanoraLandingPage();
            landing.start(stage);
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTask() {
        // Only restore if not in a space
        if (currentActiveSpace == null) {
            restoreOriginalMainContent();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/addTask-view.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/com/example/javafx_project/addTask.css").toExternalForm());

            AddTaskController controller = loader.getController();
            if (currentActiveSpace != null) {
                controller.setCurrentSpace(currentActiveSpace);
            }

            Stage modalStage = new Stage();
            modalStage.setTitle("Add New Task");
            modalStage.setScene(scene);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            if (currentActiveSpace != null) {
                showSpaceDetails(currentActiveSpace);
            } else {
                loadTasks();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        selectedCategory = "All";
        currentTasks = TaskService.getTasksByUser(UserManager.currentUser);
        List<Task> nonCompletedTasks = currentTasks.stream()
            .filter(t -> !t.isCompleted())
            .toList();
        currentTasks = nonCompletedTasks;
        updateFilterButtonsStyle();
        displayTasks(nonCompletedTasks);
        updateStatisticsFromCache(TaskService.getTasksByUser(UserManager.currentUser));
    }

    private void loadTodayTasks() {
        selectedCategory = "All";
        currentTasks = TaskService.getTodayTasks(UserManager.currentUser);
        updateFilterButtonsStyle();
        displayTasks(currentTasks);
        updateStatisticsFromCache(TaskService.getTasksByUser(UserManager.currentUser));
    }

    private void loadUpcomingTasks() {
        selectedCategory = "All";
        currentTasks = TaskService.getUpcomingTasks(UserManager.currentUser);
        updateFilterButtonsStyle();
        displayTasks(currentTasks);
        updateStatisticsFromCache(TaskService.getTasksByUser(UserManager.currentUser));
    }

    private void loadCompletedTasks() {
        selectedCategory = "All";
        currentTasks = TaskService.getCompletedTasks(UserManager.currentUser);
        updateFilterButtonsStyle();
        displayTasks(currentTasks);
        updateStatisticsFromCache(TaskService.getTasksByUser(UserManager.currentUser));
    }

    private void displayTasks(List<Task> tasks) {
        taskListVBox.getChildren().clear();
        for (Task task : tasks) {
            taskListVBox.getChildren().add(createTaskCard(task));
        }
    }

    @FXML
    private void handleFilterCategory(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonText = clickedButton.getText();
        
        // Determine the selected category
        String category = switch (buttonText) {
            case "All" -> "All";
            case "📚 Study" -> "Study";
            case "💼 Work" -> "Work";
            case "💝 Personal" -> "Personal";
            case "💪 Health" -> "Health";
            case "🎯 Other" -> "Other";
            default -> "All";
        };
        
        selectedCategory = category;
        updateFilterButtonsStyle();
        applyFilter();
    }

    private void updateFilterButtonsStyle() {
        // Remove selected style from all buttons
        filterAllBtn.getStyleClass().remove("category-filter-selected");
        filterStudyBtn.getStyleClass().remove("category-filter-selected");
        filterWorkBtn.getStyleClass().remove("category-filter-selected");
        filterPersonalBtn.getStyleClass().remove("category-filter-selected");
        filterHealthBtn.getStyleClass().remove("category-filter-selected");
        filterOtherBtn.getStyleClass().remove("category-filter-selected");
        
        // Add selected style to the active button
        Button selectedButton = switch (selectedCategory) {
            case "All" -> filterAllBtn;
            case "Study" -> filterStudyBtn;
            case "Work" -> filterWorkBtn;
            case "Personal" -> filterPersonalBtn;
            case "Health" -> filterHealthBtn;
            case "Other" -> filterOtherBtn;
            default -> filterAllBtn;
        };
        selectedButton.getStyleClass().add("category-filter-selected");
    }

    private void applyFilter() {
        if (selectedCategory.equals("All")) {
            displayTasks(currentTasks);
        } else {
            List<Task> filtered = currentTasks.stream()
                .filter(task -> task.getCategory() != null && task.getCategory().equals(selectedCategory))
                .toList();
            displayTasks(filtered);
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

        // Only add edit button if not a synced task
        if (!task.getTitle().contains(" [")) {
            Button editButton = new Button("✏ Edit");
            editButton.getStyleClass().add("add-space-btn");
            editButton.setOnAction(e -> handleEditTask(task));
            buttonBox.getChildren().addAll(doneButton, editButton, deleteButton);
        } else {
            buttonBox.getChildren().addAll(doneButton, deleteButton);
        }

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
        TaskService.markCompleted(task.getTitle(), UserManager.currentUser);
        loadTasks();
    }

    private void deleteTask(Task task) {
        TaskService.deleteTask(task.getTitle(), UserManager.currentUser);
        loadTasks();
    }

    private void handleEditTask(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/addTask-view.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/com/example/javafx_project/addTask.css").toExternalForm());

            AddTaskController controller = loader.getController();
            controller.setEditMode(true);
            controller.setTaskToEdit(task);
            controller.setupForEdit();

            Stage modalStage = new Stage();
            modalStage.setTitle("Edit Task");
            modalStage.setScene(scene);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            loadTasks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        List<Task> allTasks = TaskService.getTasksByUser(UserManager.currentUser);
        updateStatisticsFromCache(allTasks);
    }

    private void updateStatisticsFromCache(List<Task> allTasks) {
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
    private VBox createSpaceCard(Space space) {
        VBox card = new VBox(10);
        card.getStyleClass().add("space-card");
        card.setPrefSize(220, 140);
        card.setAlignment(Pos.CENTER);
        Label name = new Label(space.getSpaceName());
        name.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        Label role = new Label(space.isAdmin(UserManager.currentUser) ? "ADMIN" : "MEMBER");
        role.setStyle("-fx-text-fill: #FF6FB5; -fx-font-size: 11;");
        card.getChildren().addAll(name, role);
        card.setOnMouseClicked(e -> showSpaceDetails(space));
        return card;
    }
    private void handleCreateSpace() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Create a shared space");
        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                SpaceManager.addSpace(new Space(name, UserManager.currentUser));
                showSpaceList();
            }
        });
    }
    private void showSpaceList() {
        currentActiveSpace = null; // Clear active space context
        mainContent.getChildren().clear();

        Label title = new Label("Collaborative Spaces");
        title.getStyleClass().add("greeting");

        Button addSpaceBtn = new Button("+ New Space");
        addSpaceBtn.getStyleClass().add("add-space-btn"); // Small & Rounded via CSS
        addSpaceBtn.setOnAction(e -> handleCreateSpace());

        Button invitationsBtn = new Button("📩 Invitations");
        invitationsBtn.getStyleClass().add("add-space-btn");
        invitationsBtn.setOnAction(e -> showInvitations());

        HBox topBar = new HBox(20, title, addSpaceBtn, invitationsBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        FlowPane spaceGrid = new FlowPane(20, 20);
        for (Space space : SpaceManager.getAllSpaces()) {
            if (space.getMembers().contains(UserManager.currentUser)) {
                VBox card = createSpaceCard(space);
                spaceGrid.getChildren().add(card);
            }
        }

        mainContent.getChildren().addAll(topBar, spaceGrid);
    }
    private HBox createSpaceSpecificCard(Task task, Space space) {
        HBox taskCard = new HBox(15);
        taskCard.getStyleClass().add("task-card");

        // --- 1. Task Info (Title, Desc, etc) ---
        VBox contentBox = new VBox(5);
        Label titleLabel = new Label(task.getTitle());
        titleLabel.getStyleClass().add("task-title");
        Label descLabel = new Label(task.getDescription() != null ? task.getDescription() : "");
        contentBox.getChildren().addAll(titleLabel, descLabel);

        // --- 2. Button Box ---
        VBox buttonBox = new VBox(5);

        // Sync Button (Visible to EVERYONE)
        Button syncBtn = new Button("🔄 Sync");
        syncBtn.getStyleClass().add("add-space-btn");
        syncBtn.setOnAction(e -> {
            String syncedTitle = task.getTitle() + " [" + space.getSpaceName() + "]";
            Task existing = TaskService.getTaskByTitle(UserManager.currentUser, syncedTitle);
            if (existing != null) {
                // Update existing synced task
                existing.setDescription(task.getDescription());
                existing.setPriority(task.getPriority());
                existing.setDeadline(task.getDeadline());
                existing.setCategory(task.getCategory());
                TaskService.updateTask(existing);
                new Alert(Alert.AlertType.INFORMATION, "Synced task updated in personal dashboard!").show();
            } else {
                // Create new synced task
                Task syncedTask = new Task();
                syncedTask.setUserId(UserManager.currentUser);
                syncedTask.setTitle(syncedTitle);
                syncedTask.setDescription(task.getDescription());
                syncedTask.setPriority(task.getPriority());
                syncedTask.setDeadline(task.getDeadline());
                syncedTask.setCategory(task.getCategory());
                syncedTask.setCompleted(false);
                TaskService.createTask(syncedTask, UserManager.currentUser);
                new Alert(Alert.AlertType.INFORMATION, "Task synced to personal dashboard!").show();
            }
            updateStatistics();
        });
        buttonBox.getChildren().add(syncBtn);

        // Edit/Delete Buttons (Visible to ADMIN ONLY)
        if (space.isAdmin(UserManager.currentUser)) {
            Button editBtn = new Button("✏ Edit");
            editBtn.getStyleClass().add("add-space-btn");
            editBtn.setOnAction(e -> handleEditSpaceTask(task, space));

            Button deleteBtn = new Button("🗑 Delete");
            deleteBtn.getStyleClass().add("delete-button");
            deleteBtn.setOnAction(e -> {
                space.getSpaceTasks().remove(task);
                // Save to MongoDB (cloud-first)
                if (DatabaseManager.isConnected()) {
                    new Thread(() -> DatabaseManager.saveSpace(space)).start();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Not connected to cloud. Changes may not be saved.").show();
                }
                showSpaceDetails(space); // Refresh UI
            });

            buttonBox.getChildren().addAll(editBtn, deleteBtn);
        }

        taskCard.getChildren().addAll(contentBox, buttonBox);
        return taskCard;
    }

    private void showSpaceDetails(Space space) {
        this.currentActiveSpace = space;
        
        // Load the new FXML with space content + chat
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/space-with-chat.fxml"));
            javafx.scene.Parent spaceWithChatRoot = loader.load();
            spaceWithChatRoot.getStylesheets().add(getClass().getResource("/com/example/javafx_project/space-chat.css").toExternalForm());

            ChatController chatController = loader.getController();
            chatController.setSpace(space);

            // Get the space content container from FXML
            VBox spaceContentContainer = (VBox) spaceWithChatRoot.lookup("#spaceContentContainer");

            // Build space UI and add to container
            Label title = new Label(space.getSpaceName());
            title.getStyleClass().add("greeting");

            Button backBtn = new Button("← Back");
            backBtn.getStyleClass().add("delete-button");
            backBtn.setOnAction(e -> showSpaceList());

            Button addTaskBtn = new Button("+ Task");
            addTaskBtn.getStyleClass().add("add-space-btn");
            addTaskBtn.setOnAction(e -> handleAddTask());

            Button inviteBtn = new Button("👤 Invite");
            inviteBtn.getStyleClass().add("invite-button");
            inviteBtn.setOnAction(e -> handleInvite(space));

            // ADMIN CHECK
            boolean isAdmin = space.isAdmin(UserManager.currentUser);
            addTaskBtn.setVisible(isAdmin);
            inviteBtn.setVisible(isAdmin);

            HBox actionHeader = new HBox(15, backBtn, title, addTaskBtn, inviteBtn);
            
            // Add delete button for admin only
            if (isAdmin) {
                Button deleteSpaceBtn = new Button("🗑 Delete Space");
                deleteSpaceBtn.setStyle(
                    "-fx-background-color: rgba(244, 67, 54, 0.8);" +
                    "-fx-text-fill: white;" +
                    "-fx-padding: 8 15;" +
                    "-fx-font-size: 12;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 15;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(244, 67, 54, 0.4), 8, 0, 0, 1);"
                );
                deleteSpaceBtn.setOnAction(e -> handleDeleteSpace(space));
                actionHeader.getChildren().add(deleteSpaceBtn);
            }
            
            actionHeader.setAlignment(Pos.CENTER_LEFT);

            VBox taskArea = new VBox(15);
            if (space.getSpaceTasks() != null) {
                for(Task t : space.getSpaceTasks()) {
                    taskArea.getChildren().add(createSpaceSpecificCard(t, space));
                }
            }

            spaceContentContainer.getChildren().addAll(actionHeader, taskArea);
            
            // Replace main content with space+chat view
            mainContent.getChildren().clear();
            mainContent.getChildren().add(spaceWithChatRoot);
            VBox.setVgrow(spaceWithChatRoot, Priority.ALWAYS);
            
        } catch (IOException e) {
            System.err.println("Error loading space with chat: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleInvite(Space space) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Invite to " + space.getSpaceName());
        dialog.setContentText("Username:");
        dialog.showAndWait().ifPresent(username -> {
            if (username.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Username cannot be empty!").show();
                return;
            }

            // Create invitation in MongoDB
            InvitationManager.createInvitation(space.getSpaceName(), username.trim(), UserManager.currentUser);
            new Alert(Alert.AlertType.INFORMATION, "Invitation sent to " + username + "!").show();
        });
    }

    private void handleDeleteSpace(Space space) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Space");
        confirmAlert.setHeaderText("Delete \"" + space.getSpaceName() + "\"?");
        confirmAlert.setContentText("This action cannot be undone. All space members will lose access.");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            SpaceManager.deleteSpace(space.getSpaceName());
            // Also delete all messages for this space
            DatabaseManager.deleteMessagesBySpaceId(space.getSpaceName());
            new Alert(Alert.AlertType.INFORMATION, "Space deleted successfully!").show();
            showSpaceList();
        }
    }

    private void showInvitations() {
        mainContent.getChildren().clear();
        
        Label title = new Label("Your Invitations");
        title.getStyleClass().add("greeting");

        Button backBtn = new Button("← Back to Spaces");
        backBtn.getStyleClass().add("add-space-btn");
        backBtn.setOnAction(e -> showSpaceList());

        HBox topBar = new HBox(20, title, backBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Fetch pending invitations for current user
        List<Invitation> invitations = InvitationManager.getPendingInvitations(UserManager.currentUser);

        VBox invitationsList = new VBox(15);
        invitationsList.setPadding(new Insets(20));

        if (invitations.isEmpty()) {
            Label noInvitationsLabel = new Label("No pending invitations");
            noInvitationsLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 14; -fx-padding: 40;");
            invitationsList.getChildren().add(noInvitationsLabel);
        } else {
            for (Invitation invitation : invitations) {
                VBox invCard = createInvitationCard(invitation);
                invitationsList.getChildren().add(invCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(invitationsList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-control-inner-background: transparent; -fx-padding: 0;");

        mainContent.getChildren().addAll(topBar, scrollPane);
    }

    private VBox createInvitationCard(Invitation invitation) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: rgba(123, 115, 255, 0.15);" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 20;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(255, 111, 181, 0.3), 15, 0, 0, 0);"
        );
        card.setPrefWidth(550);

        Label spaceName = new Label("🚀 " + invitation.spaceName);
        spaceName.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #FFF; -fx-padding: 0 0 5 0;");

        Label senderLabel = new Label("📨 Invited by: " + invitation.senderUsername);
        senderLabel.setStyle("-fx-font-size: 13; -fx-text-fill: rgba(255, 255, 255, 0.8); -fx-padding: 0 0 3 0;");

        Label dateLabel = new Label("📅 " + invitation.createdAt);
        dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: rgba(255, 255, 255, 0.6);");

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button acceptBtn = new Button("✓ Accept");
        acceptBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #7B73FF, #FF6FB5);" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10 25;" +
            "-fx-font-size: 13;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(123, 115, 255, 0.5), 10, 0, 0, 2);"
        );
        acceptBtn.setOnAction(e -> {
            InvitationManager.acceptInvitation(invitation.spaceName, UserManager.currentUser);
            SpaceManager.loadSpacesFromMongoDB();
            new Alert(Alert.AlertType.INFORMATION, "🎉 You joined " + invitation.spaceName + "!").show();
            showInvitations();
        });

        Button declineBtn = new Button("✗ Decline");
        declineBtn.setStyle(
            "-fx-background-color: rgba(244, 67, 54, 0.8);" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10 25;" +
            "-fx-font-size: 13;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(244, 67, 54, 0.4), 8, 0, 0, 1);"
        );
        declineBtn.setOnAction(e -> {
            InvitationManager.declineInvitation(invitation.spaceName, UserManager.currentUser);
            new Alert(Alert.AlertType.INFORMATION, "Invitation declined.").show();
            showInvitations();
        });

        buttonBox.getChildren().addAll(acceptBtn, declineBtn);

        card.getChildren().addAll(spaceName, senderLabel, dateLabel, buttonBox);
        return card;
    }

    private void handleEditSpaceTask(Task task, Space space) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_project/addTask-view.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/com/example/javafx_project/addTask.css").toExternalForm());

            AddTaskController controller = loader.getController();
            controller.setCurrentSpace(space);
            controller.setEditMode(true);
            controller.setTaskToEdit(task);
            controller.setupForEdit();

            Stage modalStage = new Stage();
            modalStage.setTitle("Edit Task in " + space.getSpaceName());
            modalStage.setScene(scene);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            showSpaceDetails(space); // Refresh space view
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setActiveButton(Button activeButton) {
        // List of all your sidebar buttons
        Button[] allButtons = {dashboardBtn, todayBtn, upcomingBtn, completedBtn, focusBtn,calendarBtn, analyticsBtn,spaceBtn, settingsBtn};

        // Remove the active class from all buttons
        for (Button btn : allButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-button-active");
            }
        }

        // Add the active class to the clicked button
        if (activeButton != null) {
            if (!activeButton.getStyleClass().contains("sidebar-button-active")) {
                activeButton.getStyleClass().add("sidebar-button-active");
            }
        }
    }
}
