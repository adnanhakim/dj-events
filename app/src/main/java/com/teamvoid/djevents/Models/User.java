package com.teamvoid.djevents.Models;

public class User {
    private String email;
    private String name;
    private String year;
    private String department;

    public User(String email, String name, String year, String department) {
        this.email = email;
        this.name = name;
        this.year = year;
        this.department = department;
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
}
