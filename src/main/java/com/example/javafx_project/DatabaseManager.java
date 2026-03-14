package com.example.javafx_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:planora.db";
    private static Connection connection;

    static {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            if (connection != null) {
                createTables();
                System.out.println("Database connection established successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error during database initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            System.err.println("WARNING: Database connection is null!");
        }
        return connection;
    }

    private static void createTables() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            );
            """;

        String createTasksTable = """
            CREATE TABLE IF NOT EXISTS tasks (
                task_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                deadline TEXT,
                category TEXT,
                priority TEXT,
                completed INTEGER DEFAULT 0,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(username)
            );
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createTasksTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void migrateUsers() {
        // Migrate existing users from UserManager to database
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("Cannot migrate users: database connection is null");
            return;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO users (username, password) VALUES (?, ?)")) {
            for (var entry : UserManager.getUsers().entrySet()) {
                pstmt.setString(1, entry.getKey());
                pstmt.setString(2, entry.getValue());
                pstmt.executeUpdate();
            }
            System.out.println("User migration completed successfully!");
        } catch (SQLException e) {
            System.err.println("Error during user migration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}