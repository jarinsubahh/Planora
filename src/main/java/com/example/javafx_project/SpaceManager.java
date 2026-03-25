package com.example.javafx_project;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SpaceManager {
    private static final String FILE_PATH = "spaces.dat";
    private static List<Space> allSpaces = new ArrayList<>();

    public static void addSpace(Space space) {
        if (allSpaces.isEmpty()) loadFromFile();
        allSpaces.add(space);
        saveToFile();
    }

    public static List<Space> getAllSpaces() {
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
}
