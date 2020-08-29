package com.teamvoid.djevents.Models;

import java.util.List;

public class Committee {
    private String id;
    private String name;
    private String department;
    private String bio;
    private String email;
    private Long followers;
    private Long posts;
    private Long events;
    private String imageUrl;
    private String topic;
    private List<String> topics;

    public Committee() {
        // For Firebase
    }

    public Committee(String id, String name, String department, String bio, String email, Long followers, Long posts, Long events, String imageUrl, String topic, List<String> topics) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.bio = bio;
        this.email = email;
        this.followers = followers;
        this.posts = posts;
        this.events = events;
        this.imageUrl = imageUrl;
        this.topic = topic;
        this.topics = topics;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getBio() {
        return bio;
    }

    public String getEmail() {
        return email;
    }

    public Long getFollowers() {
        return followers;
    }

    public Long getPosts() {
        return posts;
    }

    public Long getEvents() {
        return events;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTopic() {
        return topic;
    }

    public List<String> getTopics() {
        return topics;
    }
}
