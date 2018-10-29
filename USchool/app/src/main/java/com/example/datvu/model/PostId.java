package com.example.datvu.model;

public class PostId {
    String id;
    boolean like;
    String finish;

    public String getId() {
        return id;
    }

    public <T extends PostId> T setId(String id) {
        this.id = id;
        return (T)this;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }
}
