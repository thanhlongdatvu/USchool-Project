package com.example.datvu.model;

import java.sql.Timestamp;
import java.util.Date;

public class Post {
    private String image;
    private String imageCompress;
    private String description;
    private Date timestamp;
    private String userID;

    public Post() {
    }

    public Post(String image, String imageCompress, String description, Date timestamp, String userID) {
        this.image = image;
        this.imageCompress = imageCompress;
        this.description = description;
        this.timestamp = timestamp;
        this.userID = userID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageCompress() {
        return imageCompress;
    }

    public void setImageCompress(String imageCompress) {
        this.imageCompress = imageCompress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
