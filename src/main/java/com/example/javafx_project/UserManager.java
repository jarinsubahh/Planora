package com.example.javafx_project;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManager {
    public static String currentUser;

    public enum RegistrationResult {
        SUCCESS,
        USERNAME_TAKEN,
        DATABASE_ERROR
    }

    public static RegistrationResult register(String username, String password, String email) {
        try {
            Document existing = DatabaseManager.getUsersCollection()
                    .find(Filters.eq("username", username)).first();

            if (existing != null) {
                return RegistrationResult.USERNAME_TAKEN;
            }

            Document newUser = new Document("username", username)
                    .append("password", password)
                    .append("email", email)
                    .append("created_at", java.time.Instant.now().toString());
            DatabaseManager.getUsersCollection().insertOne(newUser);
            return RegistrationResult.SUCCESS;
        } catch (Exception e) {
            System.err.println("User registration failed: " + e.getMessage());
            e.printStackTrace();
            return RegistrationResult.DATABASE_ERROR;
        }
    }

    public static boolean validate(String username, String password) {
        if (username == null || password == null) return false;

        try {
            // Find user in Cloud
            Document user = DatabaseManager.getUsersCollection()
                    .find(Filters.eq("username", username)).first();

            if (user != null && user.getString("password").equals(password)) {
                currentUser = username;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void logout() {
        currentUser = null;
    }

    /** Profile fields stored in MongoDB users collection. */
    public static Document getUserDocument(String username) {
        if (username == null || !DatabaseManager.isConnected()) {
            return null;
        }
        try {
            return DatabaseManager.getUsersCollection()
                    .find(Filters.eq("username", username))
                    .first();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updatePassword(String username, String currentPassword, String newPassword) {
        if (username == null || currentPassword == null || newPassword == null) {
            return false;
        }
        if (!DatabaseManager.isConnected()) {
            return false;
        }
        try {
            Document user = DatabaseManager.getUsersCollection()
                    .find(Filters.eq("username", username))
                    .first();
            if (user == null || !currentPassword.equals(user.getString("password"))) {
                return false;
            }
            DatabaseManager.getUsersCollection().updateOne(
                    Filters.eq("username", username),
                    Updates.set("password", newPassword));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes the user and related cloud data (tasks, invitations, space membership / owned spaces),
     * then clears the session.
     */
    public static boolean deleteAccount(String username) {
        if (username == null || !DatabaseManager.isConnected()) {
            return false;
        }
        try {
            TaskService.deleteAllTasksForUser(username);

            DatabaseManager.getInvitationsCollection().deleteMany(
                    Filters.or(
                            Filters.eq("recipientUsername", username),
                            Filters.eq("senderUsername", username)));

            MongoCollection<Document> spaces = DatabaseManager.getSpacesCollection();
            List<Document> spaceDocs = new ArrayList<>();
            spaces.find().into(spaceDocs);
            for (Document doc : spaceDocs) {
                String spaceName = doc.getString("spaceName");
                String admin = doc.getString("adminUsername");
                @SuppressWarnings("unchecked")
                List<String> members = (List<String>) doc.get("members");
                if (members == null) {
                    members = new ArrayList<>();
                }
                if (username.equals(admin)) {
                    DatabaseManager.deleteSpace(spaceName);
                } else if (members.contains(username)) {
                    spaces.updateOne(
                            Filters.eq("spaceName", spaceName),
                            Updates.pull("members", username));
                }
            }

            DatabaseManager.getUsersCollection().deleteOne(Filters.eq("username", username));
            SpaceManager.removeUserFromAllSpacesLocal(username);
            logout();
            return true;
        } catch (Exception e) {
            System.err.println("Account deletion failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Keep this empty or remove calls to it to stop using local files
    public static Map<String, String> getUsers() {
        return new HashMap<>();
    }
}