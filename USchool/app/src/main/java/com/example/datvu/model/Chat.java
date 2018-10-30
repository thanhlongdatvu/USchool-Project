package com.example.datvu.model;

import java.util.Date;

public class Chat {
    String message;
    String userID;
    Date timestamp;

    public Chat() {
    }

    public Chat(String message, String userID, Date timestamp) {
        this.message = message;
        this.userID = userID;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
