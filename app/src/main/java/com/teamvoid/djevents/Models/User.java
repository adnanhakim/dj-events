package com.teamvoid.djevents.Models;

import java.util.List;

public class User {
    private String id;
    private String email;
    private String name;
    private String year;
    private String department;
    private String token;
    private List<String> topics;

    public User() {
        // For Firebase
    }

    public User(String email, String name, String year, String department, String token, List<String> topics) {
        this.email = email;
        this.name = name;
        this.year = year;
        this.department = department;
        this.token = token;
        this.topics = topics;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getDepartment() {
        return department;
    }

    public String getToken() {
        return token;
    }

    public List<String> getTopics() {
        return topics;
    }
}
