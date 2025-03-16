package com.example.ballersnet;

import java.util.ArrayList;
import java.util.List;

public class Team {
    public String name;
    public String homeCourtLocation;
    public int wins = 0;
    public int losses = 0;
    public String neededPositions;
    public String managerName;

    public Team(){}

    public Team(String name, String homeCourtLocation, String neededPositions, String managerName) {
        this.name = name;
        this.homeCourtLocation = homeCourtLocation;
        this.neededPositions = neededPositions;
        this.managerName = managerName;
    }

    public Team(String name, String homeCourtLocation, int wins, int losses, String neededPositions, String managerName) {
        this.name = name;
        this.homeCourtLocation = homeCourtLocation;
        this.wins = wins;
        this.losses = losses;
        this.neededPositions = neededPositions;
        this.managerName = managerName;
    }

}
