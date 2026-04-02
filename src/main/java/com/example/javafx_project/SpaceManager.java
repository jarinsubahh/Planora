package com.example.javafx_project;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SpaceManager {
    private static final String FILE_PATH = "spaces.dat";
    private static List<Space> allSpaces = new ArrayList<>();
    private static boolean initializedFromMongo = false;

    public static void addSpace(Space space) {
        if (allSpaces.isEmpty()) loadFromFile();
        allSpaces.add(space);
        saveToFile();
        
        // Also save to MongoDB if connected (asynchronously to avoid lag)
        if (DatabaseManager.isConnected()) {
            new Thread(() -> DatabaseManager.saveSpace(space)).start();
        }
    }

    public static List<Space> getAllSpaces() {
        // Try to get from MongoDB first if connected and not yet initialized
        if (DatabaseManager.isConnected() && !initializedFromMongo) {
            List<Space> mongoSpaces = DatabaseManager.getAllSpaces();
            if (!mongoSpaces.isEmpty()) {
                allSpaces = mongoSpaces;
                initializedFromMongo = true;
                return mongoSpaces;
            }
        }
        
        // If already initialized from MongoDB, return cached version
        if (initializedFromMongo) {
            return allSpaces;
        }
        
        // Fallback to local file system
        if (allSpaces.isEmpty()) loadFromFile();
        return allSpaces;
    }

    public static void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(allSpaces);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<Space> loaded = new ArrayList<>();
                for (Object item : (List<?>) obj) {
                    if (item instanceof Space) {
                        loaded.add((Space) item);
                    }
                }
                allSpaces = loaded;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Migrate all local spaces to MongoDB (call once)
     */
    public static void migrateSpacesToCloud() {
        if (!DatabaseManager.isConnected()) {
            System.err.println("Cannot migrate: MongoDB not connected");
            return;
        }
        
        new Thread(() -> {
            loadFromFile();
            for (Space space : allSpaces) {
                try {
                    DatabaseManager.saveSpace(space);
                } catch (Exception e) {
                    System.err.println("Error migrating space: " + e.getMessage());
                }
            }
            System.out.println("Spaces migration to MongoDB completed!");
        }).start();
    }
}
