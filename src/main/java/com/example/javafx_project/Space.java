package com.example.javafx_project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Space implements Serializable {
    private String spaceName;
    private String adminUsername;
    private List<String> members;
    private List<Task> spaceTasks;

    public Space(String name, String creator) {
        this.spaceName = name;
        this.adminUsername = creator;
        this.members = new ArrayList<>();
        this.members.add(creator); // Creator is the first member
        this.spaceTasks = new ArrayList<>();
    }

    public String getSpaceName() { return spaceName; }
    public String getAdminUsername() { return adminUsername; }
    public List<String> getMembers() { return members; }
    public List<Task> getSpaceTasks() {
        if (this.spaceTasks == null) {
            this.spaceTasks = new ArrayList<>();
        }
        return this.spaceTasks;
    }

    public boolean isAdmin(String username) {
        return adminUsername.equals(username);
    }
}