package com.example.datvu.model;

import java.util.Date;

public class Comment {
    String message;
    String userId;
    Date timestamp;
    String username;
    String imgUser;

    public Comment() {
    }

    public Comment(String message, String userId, Date timestamp, String username, String imgUser) {
        this.message = message;
        this.userId = userId;
        this.timestamp = timestamp;
        this.username = username;
        this.imgUser = imgUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgUser() {
        return imgUser;
    }

    public void setImgUser(String imgUser) {
        this.imgUser = imgUser;
    }
}
