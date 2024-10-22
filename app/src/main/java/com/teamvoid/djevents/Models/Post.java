package com.teamvoid.djevents.Models;

import com.google.firebase.Timestamp;

import java.util.List;

public class Post {
    private String id;
    private String dpUrl;
    private String committeeName;
    private Timestamp timestamp;
    private String imageUrl;
    private String caption;
    private List<String> likes;

    // For likes
    private Boolean isLiked;

    public Post() {
        // For Firebase
    }

    public Post(String id, String dpUrl, String committeeName, Timestamp timestamp, String imageUrl, String caption, List<String> likes) {
        this.id = id;
        this.dpUrl = dpUrl;
        this.committeeName = committeeName;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.likes = likes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDpUrl() {
        return dpUrl;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public List<String> getLikes() {
        return likes;
    }

    // For likes
    public Boolean isLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }
}