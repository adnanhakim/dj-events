package com.teamvoid.djevents.Models;

import com.google.firebase.Timestamp;

public class Event {
    private String id;
    private String dpUrl;
    private String committeeName;
    private Timestamp timestamp;
    private String name;
    private String description;
    private Timestamp eventDate;
    private String eligibility;
    private String price;
    private String registrationLink;
    private Timestamp registrationDate;
    private String status;
    private String imageUrl;

    public Event() {
        // For Firebase
    }

    public Event(String id, String dpUrl, String committeeName, Timestamp timestamp, String name, String description, Timestamp eventDate, String eligibility, String price, String registrationLink, Timestamp registrationDate, String status, String imageUrl) {
        this.id = id;
        this.dpUrl = dpUrl;
        this.committeeName = committeeName;
        this.timestamp = timestamp;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.eligibility = eligibility;
        this.price = price;
        this.registrationLink = registrationLink;
        this.registrationDate = registrationDate;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getEventDate() {
        return eventDate;
    }

    public String getEligibility() {
        return eligibility;
    }

    public String getPrice() {
        return price;
    }

    public String getRegistrationLink() {
        return registrationLink;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
