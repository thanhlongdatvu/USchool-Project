package com.example.datvu.model;

public class Schedule {
    String time;
    String action;
    String description;

    public Schedule() {
    }

    public Schedule(String time, String action, String description) {
        this.time = time;
        this.action = action;
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
