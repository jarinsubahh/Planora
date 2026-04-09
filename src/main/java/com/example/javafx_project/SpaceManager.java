package com.example.javafx_project;

import java.util.ArrayList;
import java.util.List;


public class SpaceManager {

    public static void addSpace(Space space) {
        if (!DatabaseManager.isConnected()) {
            System.err.println("MongoDB not connected. Cannot add space.");
            return;
        }
        
        try {
            DatabaseManager.saveSpace(space);
            System.out.println("Space created: " + space.getSpaceName());
        } catch (Exception e) {
            System.err.println("Error adding space: " + e.getMessage());
        }
    }

    public static List<Space> getAllSpaces() {
        if (!DatabaseManager.isConnected()) {
            System.err.println("MongoDB not connected. Cannot retrieve spaces.");
            return new ArrayList<>();
        }
        
        try {
            return DatabaseManager.getAllSpaces();
        } catch (Exception e) {
            System.err.println("Error retrieving spaces: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static Space getSpaceByName(String spaceName) {
        if (!DatabaseManager.isConnected()) {
            System.err.println("MongoDB not connected. Cannot retrieve space.");
            return null;
        }
        
        try {
            return getAllSpaces().stream()
                    .filter(s -> s.getSpaceName().equals(spaceName))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Error getting space by name: " + e.getMessage());
            return null;
        }
    }

    public static void removeUserFromAllSpaces(String username) {
        if (username == null || !DatabaseManager.isConnected()) {
            return;
        }
        
        try {
            List<Space> spaces = getAllSpaces();
            List<String> spacesToDelete = new ArrayList<>();
            
            for (Space space : spaces) {
                if (username.equals(space.getAdminUsername())) {

                    spacesToDelete.add(space.getSpaceName());
                } else {

                    if (space.getMembers().contains(username)) {
                        space.getMembers().remove(username);
                        DatabaseManager.saveSpace(space);
                    }
                }
            }

            for (String spaceName : spacesToDelete) {
                DatabaseManager.deleteSpace(spaceName);
            }
            
            System.out.println("User removed from all spaces: " + username);
        } catch (Exception e) {
            System.err.println("Error removing user from spaces: " + e.getMessage());
        }
    }


    public static void deleteSpace(String spaceName) {
        if (!DatabaseManager.isConnected()) {
            System.err.println("MongoDB not connected. Cannot delete space.");
            return;
        }
        
        try {
            DatabaseManager.deleteSpace(spaceName);
            System.out.println("Space deleted: " + spaceName);
        } catch (Exception e) {
            System.err.println("Error deleting space: " + e.getMessage());
        }
    }


    public static void loadSpacesFromMongoDB() {
        if (!DatabaseManager.isConnected()) {
            System.err.println("Cannot load from MongoDB: not connected");
            return;
        }

        try {
            List<Space> mongoSpaces = getAllSpaces();
            System.out.println("Spaces reloaded from MongoDB: " + mongoSpaces.size() + " spaces");
        } catch (Exception e) {
            System.err.println("Error reloading spaces from MongoDB: " + e.getMessage());
        }
    }
    

    @Deprecated
    public static void saveToFile() {
        System.out.println("Note: saveToFile() is deprecated. All data is now stored in MongoDB.");
    }


    @Deprecated
    public static void loadFromFile() {
        System.out.println("Note: loadFromFile() is deprecated. All data is now stored in MongoDB.");
    }
}
