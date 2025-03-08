package com.example.ballersnet;

public class User {
    public String userId, name, email, profileImage, teamName, spot, city ;
    public int age ;
    public double avg ;
    public boolean isAdmin ;
    private boolean isInMyTeam;


    // Default constructor required for Firebase
    public User() {}

    public User(String userId, String name, String email, String profileImage, String teamName, int age, String spot, double avg, boolean isAdmin, String city) {
        this.userId = userId;
        this.name = name;
        this.teamName = teamName;
        this.age = age;
        this.spot = spot ;
        this.avg = avg ;
        this.isAdmin = isAdmin ;
        this.email = email;
        this.profileImage = profileImage;
        this.city = city ;
    }

    public User(String userId, String name, String email, String profileImage) {
        this(userId, name, email, profileImage, "", -1, "", -1, false, "");
    }
    public boolean isInMyTeam() {
        return isInMyTeam;
    }
    public void setInMyTeam(boolean inMyTeam) {
        isInMyTeam = inMyTeam;
    }
}