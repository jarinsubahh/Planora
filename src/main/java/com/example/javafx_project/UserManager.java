package com.example.javafx_project;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.HashMap;
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
                    .append("email", email);
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

    // Keep this empty or remove calls to it to stop using local files
    public static Map<String, String> getUsers() {
        return new HashMap<>();
    }
}