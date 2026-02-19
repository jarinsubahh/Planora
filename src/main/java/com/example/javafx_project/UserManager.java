package com.example.javafx_project;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String DATA_FILE = "user_data.bin";
    private static Map<String, String> users = new HashMap<>();

    static { loadData(); }

    public static boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, password);
        saveData();
        return true;
    }

    public static boolean validate(String username, String password) {
        return password.equals(users.get(username));
    }

    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (Map<String, String>) ois.readObject();
            } catch (Exception e) { users = new HashMap<>(); }
        }
    }
}