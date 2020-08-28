package com.teamvoid.djevents.Models;

import com.google.firebase.Timestamp;

public class Comment {
    private String name;
    private String comment;
    private Timestamp timestamp;

    public Comment() {
        // For Firebase
    }

    public Comment(String name, String comment, Timestamp timestamp) {
        this.name = name;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
