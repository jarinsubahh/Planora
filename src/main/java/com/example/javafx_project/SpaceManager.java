package com.example.javafx_project;

import java.util.ArrayList;
import java.util.List;

/**
 * Cloud-first Space Manager
 * All operations are performed directly on MongoDB.
 * No local file caching - always fetch fresh data from cloud.
 */
public class SpaceManager {

    /**
     * Create a new space in MongoDB
     */
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

    /**
     * Get all spaces from MongoDB (cloud-first, always fresh)
     */
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

    /**
     * Get a specific space by name from MongoDB
     */
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

    /**
     * Remove user from all spaces in MongoDB
     */
    public static void removeUserFromAllSpaces(String username) {
        if (username == null || !DatabaseManager.isConnected()) {
            return;
        }
        
        try {
            List<Space> spaces = getAllSpaces();
            List<String> spacesToDelete = new ArrayList<>();
            
            for (Space space : spaces) {
                if (username.equals(space.getAdminUsername())) {
                    // Delete space if user is admin
                    spacesToDelete.add(space.getSpaceName());
                } else {
                    // Remove user from members
                    if (space.getMembers().contains(username)) {
                        space.getMembers().remove(username);
                        DatabaseManager.saveSpace(space);
                    }
                }
            }
            
            // Delete spaces where user is admin
            for (String spaceName : spacesToDelete) {
                DatabaseManager.deleteSpace(spaceName);
            }
            
            System.out.println("User removed from all spaces: " + username);
        } catch (Exception e) {
            System.err.println("Error removing user from spaces: " + e.getMessage());
        }
    }

    /**
     * Delete a space from MongoDB
     */
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

    /**
     * Reload all spaces from MongoDB
     * (useful when spaces are updated by other users/operations)
     */
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
    
    /**
     * @deprecated Use addSpace() instead - everything is now cloud-based
     */
    @Deprecated
    public static void saveToFile() {
        System.out.println("Note: saveToFile() is deprecated. All data is now stored in MongoDB.");
    }

    /**
     * @deprecated Local file storage is no longer used - everything is cloud-based
     */
    @Deprecated
    public static void loadFromFile() {
        System.out.println("Note: loadFromFile() is deprecated. All data is now stored in MongoDB.");
    }
}
