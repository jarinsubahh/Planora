package com.example.javafx_project;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void createTask(Task task, String currentUser) {
        String sql = """
            INSERT INTO tasks (user_id, title, description, deadline, category, priority, completed)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, task.getUserId());
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, task.getDeadline() != null ? task.getDeadline().format(DATE_FORMAT) : null);
            pstmt.setString(5, task.getCategory());
            pstmt.setString(6, task.getPriority());
            pstmt.setInt(7, task.isCompleted() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Task> getTasksByUser(String userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY task_id DESC";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("task_id"));
                task.setUserId(rs.getString("user_id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                String deadlineStr = rs.getString("deadline");
                if (deadlineStr != null) {
                    task.setDeadline(LocalDate.parse(deadlineStr, DATE_FORMAT));
                }
                task.setCategory(rs.getString("category"));
                task.setPriority(rs.getString("priority"));
                task.setCompleted(rs.getInt("completed") == 1);
                String createdAtStr = rs.getString("created_at");
                if (createdAtStr != null) {
                    task.setCreatedAt(LocalDateTime.parse(createdAtStr, DATETIME_FORMAT));
                }
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static List<Task> getTodayTasks(String userId) {
        String today = LocalDate.now().format(DATE_FORMAT);
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND deadline = ? AND completed = 0 ORDER BY task_id DESC";
        return getTasksFromQuery(sql, userId, today);
    }

    public static List<Task> getUpcomingTasks(String userId) {
        String today = LocalDate.now().format(DATE_FORMAT);
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND deadline > ? AND completed = 0 ORDER BY deadline ASC";
        return getTasksFromQuery(sql, userId, today);
    }

    public static List<Task> getCompletedTasks(String userId) {
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND completed = 1 ORDER BY task_id DESC";
        return getTasksFromQuery(sql, userId, null);
    }

    public static void markCompleted(int taskId) {
        String sql = "UPDATE tasks SET completed = 1 WHERE task_id = ?";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE task_id = ?";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, deadline = ?, category = ?, priority = ? WHERE task_id = ?";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getDeadline() != null ? task.getDeadline().format(DATE_FORMAT) : null);
            pstmt.setString(4, task.getCategory());
            pstmt.setString(5, task.getPriority());
            pstmt.setInt(6, task.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("task_id"));
        task.setUserId(rs.getString("user_id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        String deadlineStr = rs.getString("deadline");
        if (deadlineStr != null) {
            task.setDeadline(LocalDate.parse(deadlineStr, DATE_FORMAT));
        }
        task.setCategory(rs.getString("category"));
        task.setPriority(rs.getString("priority"));
        task.setCompleted(rs.getInt("completed") == 1);
        String createdAtStr = rs.getString("created_at");
        if (createdAtStr != null) {
            task.setCreatedAt(LocalDateTime.parse(createdAtStr, DATETIME_FORMAT));
        }
        return task;
    }

    private static List<Task> getTasksFromQuery(String sql, String userId, String param) {
        List<Task> tasks = new ArrayList<>();
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, userId);
            if (param != null) {
                pstmt.setString(2, param);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Task task = mapResultSetToTask(rs);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static Task getTaskByTitle(String userId, String title) {
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND title = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, title);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTask(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}