package com.example.myto_dolist;

import java.io.Serializable;

public class Task implements Serializable {
    private int id;
    private String name;
    private String description;
    private String date;
    private String priority;
    private boolean isCompleted;

    public Task(int id, String name, String description, String date, String priority, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.isCompleted = isCompleted;
    }

    public Task(String name, String description, String date, String priority, boolean isCompleted) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.isCompleted = isCompleted;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getPriority() { return priority; }
    public boolean isCompleted() { return isCompleted; }
    
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
