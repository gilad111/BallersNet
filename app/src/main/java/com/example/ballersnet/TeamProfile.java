package com.example.ballersnet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeamProfile extends AppCompatActivity {
    // UI components
    private TextView teamNameTextView, CourtTextView, recordTextView, neededPositionsTextView, managerNameTextView, PlayersListTextView;
    private Button editButton;

    // Firebase authentication and database references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Team name and whether the user is a manager
    private String teamName, neededPositions, managerName, homeCourt;
    private boolean isManager = false;
    private int wins, losses;
    private String players = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_profile);

        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Team Profile");
        setSupportActionBar(toolbar);

        // Initialize UI components
        initializeViews();

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Load team data
        loadTeamData();

        // Show a toast message
        Toast.makeText(this, "Team Profile", Toast.LENGTH_SHORT).show();
    }

    // Initialize UI components
    private void initializeViews() {
        teamNameTextView = findViewById(R.id.teamNameTextView);
        CourtTextView = findViewById(R.id.CourtTextView);
        recordTextView = findViewById(R.id.recordTextView);
        neededPositionsTextView = findViewById(R.id.neededPositionsTextView);
        managerNameTextView = findViewById(R.id.managerNameTextView);
        PlayersListTextView = findViewById(R.id.PlayersListTextView);
        editButton = findViewById(R.id.editButton);

        // Set click listener for the edit button
        editButton.setOnClickListener(v -> {
            if (isManager) {
                // Open edit team profile activity if user is a manager
                Intent intent = new Intent(TeamProfile.this, EditTeamProfileActivity.class);
                editButton.setVisibility(View.VISIBLE);
                intent.putExtra("teamName", teamName);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Only team managers can edit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load team data by first fetching the user's team name
    private void loadTeamData() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    teamName = user.teamName; // Assuming teamName is stored in User class
                    isManager = user.isAdmin;
                    loadTeamDetails(teamName); // Load team details using the team name
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeamProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load team details from the database
    private void loadTeamDetails(String teamName) {
        mDatabase.child("Teams").orderByChild("name").equalTo(teamName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Team team = child.getValue(Team.class);
                    if (team != null) {
                        managerName = team.managerName;
                        neededPositions = team.neededPositions;
                        wins = team.wins;
                        losses = team.losses;
                        homeCourt = team.homeCourtLocation;
                        loadTeamPlayers(teamName); // Load team players
                    }
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeamProfile.this, "Failed to load team data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to load team players
    private void loadTeamPlayers(String teamName) {
        players = ""; // Reset the players string
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null && user.teamName != null) {
                                if (user.teamName.equalsIgnoreCase(teamName)) {
                                    players = players + user.name + "\n"; // Add the player's name
                                }
                            }
                        }
                    }
                    // Remove the trailing newline character
                    if (players.length() > 0 && players.charAt(players.length() - 1) == '\n') {
                        players = players.substring(0, players.length() - 1);
                    }
                    updateUI(); // Update the UI here
                } else {
                    players = "No players found";
                    updateUI(); // Update the UI even if no players are found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeamProfile.this, "Failed to load users data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update the UI with team details
    private void updateUI() {
        teamNameTextView.setText("Team Name: " + teamName);
        CourtTextView.setText("Home court: " + homeCourt);
        recordTextView.setText("Record: " + wins + "-" + losses);
        neededPositionsTextView.setText("Needed Positions: " + neededPositions);
        managerNameTextView.setText("Manager: " + managerName);
        PlayersListTextView.setText(players);
    }
}
