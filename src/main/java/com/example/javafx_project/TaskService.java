package com.example.javafx_project;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static MongoCollection<Document> getCollection() {
        return DatabaseManager.getTasksCollection();
    }

    public static void createTask(Task task, String currentUser) {
        Document doc = new Document("user_id", task.getUserId())
                .append("title", task.getTitle())
                .append("description", task.getDescription())
                .append("deadline", task.getDeadline() != null ? task.getDeadline().format(DATE_FORMAT) : null)
                .append("category", task.getCategory())
                .append("priority", task.getPriority())
                .append("completed", task.isCompleted())
                .append("created_at", LocalDateTime.now().format(DATETIME_FORMAT));

        getCollection().insertOne(doc);
    }

    public static List<Task> getTasksByUser(String userId) {
        List<Task> tasks = new ArrayList<>();
        Bson filter = Filters.eq("user_id", userId);

        for (Document doc : getCollection().find(filter).sort(Sorts.descending("_id"))) {
            tasks.add(mapDocumentToTask(doc));
        }
        return tasks;
    }

    public static List<Task> getTodayTasks(String userId) {
        String today = LocalDate.now().format(DATE_FORMAT);
        Bson filter = Filters.and(
                Filters.eq("user_id", userId),
                Filters.eq("deadline", today),
                Filters.eq("completed", false)
        );
        return getTasksFromFilter(filter);
    }

    public static List<Task> getUpcomingTasks(String userId) {
        String today = LocalDate.now().format(DATE_FORMAT);
        Bson filter = Filters.and(
                Filters.eq("user_id", userId),
                Filters.gt("deadline", today),
                Filters.eq("completed", false)
        );
        return getTasksFromFilter(filter);
    }

    public static List<Task> getCompletedTasks(String userId) {
        Bson filter = Filters.and(
                Filters.eq("user_id", userId),
                Filters.eq("completed", true)
        );
        return getTasksFromFilter(filter);
    }
    public static Task getTaskByTitle(String userId, String title) {
        Bson filter = Filters.and(Filters.eq("user_id", userId), Filters.eq("title", title));
        Document doc = DatabaseManager.getTasksCollection().find(filter).first();
        return (doc != null) ? mapDocumentToTask(doc) : null;
    }

    public static void markCompleted(String title, String userId) {
        Bson filter = Filters.and(Filters.eq("title", title), Filters.eq("user_id", userId));
        getCollection().updateOne(filter, Updates.set("completed", true));
    }

    public static void deleteTask(String title, String userId) {
        Bson filter = Filters.and(Filters.eq("title", title), Filters.eq("user_id", userId));
        getCollection().deleteOne(filter);
    }

    public static void updateTask(Task task) {
        Bson filter = Filters.and(
                Filters.eq("title", task.getTitle()),
                Filters.eq("user_id", task.getUserId())
        );

        Bson update = Updates.combine(
                Updates.set("description", task.getDescription()),
                Updates.set("deadline", task.getDeadline() != null ? task.getDeadline().format(DATE_FORMAT) : null),
                Updates.set("category", task.getCategory()),
                Updates.set("priority", task.getPriority())
        );

        getCollection().updateOne(filter, update);
    }

    private static Task mapDocumentToTask(Document doc) {
        Task task = new Task();
        task.setUserId(doc.getString("user_id"));
        task.setTitle(doc.getString("title"));
        task.setDescription(doc.getString("description"));
        String deadlineStr = doc.getString("deadline");
        if (deadlineStr != null && !deadlineStr.isEmpty()) {
            task.setDeadline(LocalDate.parse(deadlineStr, DATE_FORMAT));
        }
        task.setCategory(doc.getString("category"));
        task.setPriority(doc.getString("priority"));
        task.setCompleted(doc.getBoolean("completed", false));
        String createdAtStr = doc.getString("created_at");
        if (createdAtStr != null) {
            task.setCreatedAt(LocalDateTime.parse(createdAtStr, DATETIME_FORMAT));
        }
        return task;
    }

    private static List<Task> getTasksFromFilter(Bson filter) {
        List<Task> tasks = new ArrayList<>();
        for (Document doc : getCollection().find(filter).sort(Sorts.descending("created_at"))) {
            tasks.add(mapDocumentToTask(doc));
        }
        return tasks;
    }
}