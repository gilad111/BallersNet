package com.example.ballersnet;

public class Team {
    public String name, location, description;

    // Default constructor required for Firebase
    public Team() {}

    public Team(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }
}
