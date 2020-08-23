package com.teamvoid.djevents.Models;

public class Member {
    String name;
    String position;

    public Member(String name, String position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }
}
