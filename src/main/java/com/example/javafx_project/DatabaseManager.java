package com.example.javafx_project;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.List;
import java.util.ArrayList;

public class DatabaseManager {
    // MongoDB Atlas Connection String
    private static final String URI = "mongodb+srv://tasnia:%40mjplanora2026@planora.oimezwt.mongodb.net/?retryWrites=true&w=majority&appName=Planora&serverSelectionTimeoutMS=10000&connectTimeoutMS=10000";

    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;
    private static boolean connectionSuccessful = false;

    static {
        try {
            System.out.println("\n═══════════════════════════════════════════════════════════════");
            System.out.println("MongoDB Atlas Connection Initialization");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("Connection URI: " + maskPassword(URI));
            System.out.println("[1/3] Creating MongoClient...");
            
            mongoClient = MongoClients.create(URI);
            System.out.println("      ✓ MongoClient created successfully");
            
            System.out.println("[2/3] Connecting to database 'Planora'...");
            database = mongoClient.getDatabase("Planora");
            System.out.println("      ✓ Database reference obtained");
            
            System.out.println("[3/3] Testing connection with ping command...");
            database.runCommand(new Document("ping", 1));
            System.out.println("      ✓ Ping successful!");
            
            connectionSuccessful = true;
            System.out.println("\n✓✓✓ MongoDB Atlas Connection Successful! ✓✓✓");
            System.out.println("═══════════════════════════════════════════════════════════════\n");
        } catch (Exception e) {
            System.err.println("\n✗ MongoDB Connection Failed ✗");
            System.err.println("═══════════════════════════════════════════════════════════════");
            System.err.println("Error: " + e.getClass().getSimpleName());
            System.err.println("Message: " + e.getMessage());
            System.err.println("\nChecklist:");
            System.err.println("  • IP Whitelist: 0.0.0.0/0 (Confirmed)");
            System.err.println("  • Username: tasnia");
            System.err.println("  • Password: @mjplanora2026");
            System.err.println("  • Cluster: planora.oimezwt.mongodb.net");
            System.err.println("  • Database: Planora");
            System.err.println("\nFull Stack Trace:");
            e.printStackTrace();
            System.err.println("═══════════════════════════════════════════════════════════════\n");
        }
    }

    /**
     * Check if connection was successful
     */
    public static boolean isConnected() {
        return connectionSuccessful && mongoClient != null && database != null;
    }

    /**
     * Masks the password in the URI for safe logging
     */
    private static String maskPassword(String uri) {
        return uri.replaceAll("(:[^@]*@)", ":****@");
    }

    /**
     * Returns the active MongoDB database instance.
     */
    public static MongoDatabase getDatabase() {
        if (database == null) {
            System.err.println("WARNING: MongoDB Database instance is null!");
        }
        return database;
    }

    /**
     * Helper method to get the Users collection.
     */
    public static MongoCollection<Document> getUsersCollection() {
        return getDatabase().getCollection("users");
    }

    /**
     * Helper method to get the Tasks collection.
     */
    public static MongoCollection<Document> getTasksCollection() {
        return getDatabase().getCollection("tasks");
    }

    /**
     * Helper method to get the Spaces collection.
     */
    public static MongoCollection<Document> getSpacesCollection() {
        return getDatabase().getCollection("spaces");
    }

    /**
     * Helper method to get the Invitations collection.
     */
    public static MongoCollection<Document> getInvitationsCollection() {
        return getDatabase().getCollection("invitations");
    }

    /**
     * Save a space to MongoDB
     */
    public static void saveSpace(Space space) {
        if (!isConnected() || space == null) return;
        
        try {
            MongoCollection<Document> spacesCollection = getSpacesCollection();
            Document spaceDoc = new Document("spaceName", space.getSpaceName())
                    .append("adminUsername", space.getAdminUsername())
                    .append("members", space.getMembers());
            
            Document existing = spacesCollection.find(new Document("spaceName", space.getSpaceName())).first();
            if (existing == null) {
                spacesCollection.insertOne(spaceDoc);
            } else {
                spacesCollection.replaceOne(new Document("spaceName", space.getSpaceName()), spaceDoc);
            }
        } catch (Exception e) {
            System.err.println("Error saving space to MongoDB: " + e.getMessage());
        }
    }

    /**
     * Get all spaces from MongoDB
     */
    public static List<Space> getAllSpaces() {
        List<Space> spaces = new ArrayList<>();
        if (!isConnected()) return spaces;
        
        try {
            MongoCollection<Document> spacesCollection = getSpacesCollection();
            for (Document doc : spacesCollection.find()) {
                String spaceName = doc.getString("spaceName");
                String adminUsername = doc.getString("adminUsername");
                Space space = new Space(spaceName, adminUsername);
                
                List<?> members = doc.getList("members", Object.class);
                if (members != null) {
                    for (Object member : members) {
                        if (!space.getMembers().contains(member.toString())) {
                            space.getMembers().add(member.toString());
                        }
                    }
                }
                spaces.add(space);
            }
        } catch (Exception e) {
            System.err.println("Error retrieving spaces from MongoDB: " + e.getMessage());
        }
        return spaces;
    }

    /**
     * Delete a space from MongoDB
     */
    public static void deleteSpace(String spaceName) {
        if (!isConnected()) {
            System.err.println("MongoDB not connected. Cannot delete space.");
            return;
        }

        try {
            MongoCollection<Document> spacesCollection = getSpacesCollection();
            spacesCollection.deleteOne(new Document("spaceName", spaceName));
            System.out.println("Space deleted from MongoDB: " + spaceName);
        } catch (Exception e) {
            System.err.println("Error deleting space from MongoDB: " + e.getMessage());
        }
    }

    /**
     * Migration method to move data from your local file to MongoDB.
     */
    public static void migrateUsers() {
        if (!isConnected()) {
            System.err.println("Cannot migrate: MongoDB not connected");
            return;
        }
        
        try {
            MongoCollection<Document> usersCollection = getUsersCollection();
            for (var entry : UserManager.getUsers().entrySet()) {
                String username = entry.getKey();
                String password = entry.getValue();
                Document existing = usersCollection.find(new Document("username", username)).first();
                if (existing == null) {
                    Document newUser = new Document("username", username).append("password", password);
                    usersCollection.insertOne(newUser);
                }
            }
            System.out.println("User migration completed successfully!");
        } catch (Exception e) {
            System.err.println("Migration error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Closes the connection when the application shuts down.
     */
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }
}
