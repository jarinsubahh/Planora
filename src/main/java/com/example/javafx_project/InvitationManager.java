package com.example.javafx_project;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager class for handling space invitations
 * Handles creation, retrieval, and acceptance of space invitations
 */
public class InvitationManager {

    /**
     * Create an invitation and save to MongoDB
     */
    public static void createInvitation(String spaceName, String recipientUsername, String senderUsername) {
        if (!DatabaseManager.isConnected()) {
            System.err.println("MongoDB not connected. Cannot create invitation.");
            return;
        }

        try {
            Invitation invitation = new Invitation(spaceName, recipientUsername, senderUsername);
            MongoCollection<Document> invitationsCollection = DatabaseManager.getInvitationsCollection();

            Document invDoc = new Document("spaceName", invitation.spaceName)
                    .append("recipientUsername", invitation.recipientUsername)
                    .append("senderUsername", invitation.senderUsername)
                    .append("status", invitation.status)
                    .append("createdAt", invitation.createdAt);

            invitationsCollection.insertOne(invDoc);
            System.out.println("Invitation created: " + recipientUsername + " invited to " + spaceName);
        } catch (Exception e) {
            System.err.println("Error creating invitation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get all pending invitations for a user
     */
    public static List<Invitation> getPendingInvitations(String username) {
        List<Invitation> invitations = new ArrayList<>();
        if (!DatabaseManager.isConnected()) {
            System.err.println("MongoDB not connected. Cannot fetch invitations.");
            return invitations;
        }

        try {
            MongoCollection<Document> invitationsCollection = DatabaseManager.getInvitationsCollection();
            Bson filter = Filters.and(
                    Filters.eq("recipientUsername", username),
                    Filters.eq("status", "pending")
            );

            for (Document doc : invitationsCollection.find(filter)) {
                Invitation inv = documentToInvitation(doc);
                invitations.add(inv);
            }
        } catch (Exception e) {
            System.err.println("Error fetching invitations: " + e.getMessage());
            e.printStackTrace();
        }

        return invitations;
    }

    /**
     * Accept an invitation and add user to space members
     */
    public static void acceptInvitation(String spaceName, String username) {
        if (!DatabaseManager.isConnected()) {
            System.err.println("MongoDB not connected. Cannot accept invitation.");
            return;
        }

        try {
            // Update invitation status to accepted
            MongoCollection<Document> invitationsCollection = DatabaseManager.getInvitationsCollection();
            Bson invFilter = Filters.and(
                    Filters.eq("spaceName", spaceName),
                    Filters.eq("recipientUsername", username),
                    Filters.eq("status", "pending")
            );

            Bson invUpdate = Updates.set("status", "accepted");
            invitationsCollection.updateOne(invFilter, invUpdate);

            // Add user to space members
            MongoCollection<Document> spacesCollection = DatabaseManager.getSpacesCollection();
            Document spaceDoc = spacesCollection.find(Filters.eq("spaceName", spaceName)).first();

            if (spaceDoc != null) {
                @SuppressWarnings("unchecked")
                List<String> members = (List<String>) spaceDoc.get("members");
                if (members == null) {
                    members = new ArrayList<>();
                }

                if (!members.contains(username)) {
                    members.add(username);
                    Bson spaceFilter = Filters.eq("spaceName", spaceName);
                    Bson spaceUpdate = Updates.set("members", members);
                    spacesCollection.updateOne(spaceFilter, spaceUpdate);
                }
            }

            // Reload spaces locally
            Space space = SpaceManager.getSpaceByName(spaceName);

            if (space != null && !space.getMembers().contains(username)) {
                space.getMembers().add(username);
                SpaceManager.saveToFile();
            }

            System.out.println("Invitation accepted: " + username + " joined " + spaceName);
        } catch (Exception e) {
            System.err.println("Error accepting invitation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Decline an invitation
     */
    public static void declineInvitation(String spaceName, String username) {
        if (!DatabaseManager.isConnected()) {
            System.err.println("MongoDB not connected. Cannot decline invitation.");
            return;
        }

        try {
            MongoCollection<Document> invitationsCollection = DatabaseManager.getInvitationsCollection();
            Bson filter = Filters.and(
                    Filters.eq("spaceName", spaceName),
                    Filters.eq("recipientUsername", username),
                    Filters.eq("status", "pending")
            );

            Bson update = Updates.set("status", "declined");
            invitationsCollection.updateOne(filter, update);

            System.out.println("Invitation declined: " + username + " declined invitation to " + spaceName);
        } catch (Exception e) {
            System.err.println("Error declining invitation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to convert MongoDB document to Invitation object
     */
    private static Invitation documentToInvitation(Document doc) {
        return new Invitation(
                doc.getString("spaceName"),
                doc.getString("recipientUsername"),
                doc.getString("senderUsername"),
                doc.getString("status"),
                doc.getString("createdAt")
        );
    }
}
