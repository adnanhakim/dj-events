package com.teamvoid.djevents.Models;

public class Post {
    private String id;
    private String dpURL;
    private String username;
    private String time;
    private String imageURL;
    private String caption;
    private String likes;
    private String comments;

    public Post(String id, String dpURL, String username, String time, String imageURL, String caption, String likes, String comments) {
        this.id = id;
        this.dpURL = dpURL;
        this.username = username;
        this.time = time;
        this.imageURL = imageURL;
        this.caption = caption;
        this.likes = likes;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public String getDpURL() {
        return dpURL;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public String getLikes() {
        return likes;
    }

    public String getComments() {
        return comments;
    }
}

