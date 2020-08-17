package com.teamvoid.djevents.Models;

public class Post {
    private String displayPicture;
    private String username;
    private String timeAgo;
    private String postImage;
    private String likes;
    private String comments;

    public Post(String displayPicture, String username, String timeAgo, String postImage, String likes, String comments) {
        this.displayPicture = displayPicture;
        this.username = username;
        this.timeAgo = timeAgo;
        this.postImage = postImage;
        this.likes = likes;
        this.comments = comments;
    }

    public String getDisplayPicture() {
        return displayPicture;
    }

    public String getUsername() {
        return username;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public String getPostImage() {
        return postImage;
    }

    public String getLikes() {
        return likes;
    }

    public String getComments() {
        return comments;
    }
}
