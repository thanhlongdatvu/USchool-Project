package com.example.datvu.model;

public class PostId {
    String id;
    boolean like;

    public String getId() {
        return id;
    }

    public Post setId(String id) {
        this.id = id;
        return (Post)this;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
