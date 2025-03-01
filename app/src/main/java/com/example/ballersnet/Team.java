package com.example.ballersnet;

import java.util.ArrayList;
import java.util.List;

public class Team {
    public String name;
    public String homeCourtLocation;
    public int wins;
    public int losses;
    public List<String> playerIds;
    public List<String> neededPositions;
    public String managerName;
    public int description;

    // קונסטרקטור ברירת מחדל עבור Firebase
    public Team() {}

    public Team(String name, String homeCourtLocation, int wins, int losses, List<String> playerIds, List<String> neededPositions, String managerName) {
        this.name = name;
        this.homeCourtLocation = homeCourtLocation;
        this.wins = wins;
        this.losses = losses;
        this.playerIds = playerIds;
        this.neededPositions = neededPositions;
        this.managerName = managerName;
    }

    public <E> Team(String teamName, String homeCourt, int wins, int losses, ArrayList<E> es, String displayName) {
    }
}
