package com.teamvoid.djevents.Models;

public class User {
    private String name;
    private int year;
    private String department;

    public User(String name, int year, String department) {
        this.name = name;
        this.year = year;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public String getDepartment() {
        return department;
    }
}
