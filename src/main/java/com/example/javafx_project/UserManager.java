package com.example.javafx_project;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    public static String currentUser;

    // We no longer need the local Map or the .bin file
    public static boolean register(String username, String password) {
        try {
            // Check Cloud to see if username is taken
            Document existing = DatabaseManager.getUsersCollection()
                    .find(Filters.eq("username", username)).first();

            if (existing != null) {
                return false; // User already exists
            }

            // Insert new user into Cloud
            Document newUser = new Document("username", username)
                    .append("password", password);
            DatabaseManager.getUsersCollection().insertOne(newUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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