package com.example.datvu.model;

import java.sql.Timestamp;
import java.util.Date;

public class Post extends PostId{
    private String image;
    private String imageCompress;
    private String description;
    private Date timestamp;
    private String userID;
    private String username;
    private String imgUser;

    public Post() {
    }

    public Post(String image, String imageCompress, String description, Date timestamp, String userID, String username, String imgUser) {
        this.image = image;
        this.imageCompress = imageCompress;
        this.description = description;
        this.timestamp = timestamp;
        this.userID = userID;
        this.username = username;
        this.imgUser = imgUser;
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
