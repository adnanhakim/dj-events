package com.teamvoid.djevents.Models;

public class Post {
    private User user;
    private String userId;
    private String timestamp;
    private String imageURL;
    private String caption;
    private String likes;
    private String comments;

    public Post(String userId, String timestamp, String imageURL, String caption, String likes, String comments) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.imageURL = imageURL;
        this.caption = caption;
        this.likes = likes;
        this.comments = comments;
    }

    public Post(User user, String timestamp, String imageURL, String caption, String likes, String comments) {
        this.user = user;
        this.timestamp = timestamp;
        this.imageURL = imageURL;
        this.caption = caption;
        this.likes = likes;
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public String getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
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

