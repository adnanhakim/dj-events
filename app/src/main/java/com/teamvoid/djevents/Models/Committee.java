package com.teamvoid.djevents.Models;

public class Committee {
    private String id;
    private String name;
    private String department;
    private String email;
    private Integer followers;
    private Integer posts;
    private Integer events;
    private String imageUrl;

    public Committee() {
        // For Firebase
    }

    public Committee(String id, String name, String department, String email, Integer followers, Integer posts, Integer events, String imageUrl) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.email = email;
        this.followers = followers;
        this.posts = posts;
        this.events = events;
        this.imageUrl = imageUrl;
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

    public String getEmail() {
        return email;
    }

    public Integer getFollowers() {
        return followers;
    }

    public Integer getPosts() {
        return posts;
    }

    public Integer getEvents() {
        return events;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
