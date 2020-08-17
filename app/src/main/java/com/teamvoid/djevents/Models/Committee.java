package com.teamvoid.djevents.Models;

public class Committee {
    String id;
    String name;
    String imageURL;

    public Committee(String id, String name, String imageURL) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }
}
