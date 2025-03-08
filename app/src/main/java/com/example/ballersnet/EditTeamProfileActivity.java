package com.example.ballersnet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Arrays;

public class EditTeamProfileActivity extends AppCompatActivity {

    // UI elements for team profile editing
    private EditText teamNameEditText, homeCourEditText, winsEditText, lossesEditText, neededPositionsEditText;
    private Button saveButton;

    // Firebase references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Team ID to edit
    private String teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team_profile);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Team Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get team ID from intent
        teamId = getIntent().getStringExtra("teamId");

        // Initialize UI elements
        initializeViews();

        // Load team data
        loadTeamData();

        // Set click listener for save button
        saveButton.setOnClickListener(v -> updateTeamProfile());
    }

    // Method to initialize UI elements
    private void initializeViews() {
        teamNameEditText = findViewById(R.id.teamNameEditText);
        homeCourEditText = findViewById(R.id.homeCourtEditText);
        winsEditText = findViewById(R.id.winsEditText);
        lossesEditText = findViewById(R.id.lossesEditText);
        neededPositionsEditText = findViewById(R.id.neededPositionsEditText);
        saveButton = findViewById(R.id.saveButton);
    }

    // Method to load team data from Firebase
    private void loadTeamData() {
        mDatabase.child("Teams").child(teamId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Team team = dataSnapshot.getValue(Team.class);
                if (team != null) {
                    // Populate UI elements with team data
                    teamNameEditText.setText(team.name);
                    homeCourEditText.setText(team.homeCourtLocation);
                    winsEditText.setText(String.valueOf(team.wins));
                    lossesEditText.setText(String.valueOf(team.losses));
                    neededPositionsEditText.setText(String.join(", ", team.neededPositions));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditTeamProfileActivity.this, "Failed to load team data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to update team profile in Firebase
    private void updateTeamProfile() {
        // Get values from UI elements
        String teamName = teamNameEditText.getText().toString().trim();
        String homeCourt = homeCourEditText.getText().toString().trim();
        int wins = Integer.parseInt(winsEditText.getText().toString().trim());
        int losses = Integer.parseInt(lossesEditText.getText().toString().trim());
        String[] neededPositions = neededPositionsEditText.getText().toString().split(",");

        // Create updated Team object
        // Team updatedTeam = new Team(teamName, homeCourt, wins, losses, "", mAuth.getCurrentUser().getDisplayName());
        Team updatedTeam = new Team();

        // Update team data in Firebase
        mDatabase.child("Teams").child(teamId).setValue(updatedTeam)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditTeamProfileActivity.this, "Team profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity and return to TeamProfile
                })
                .addOnFailureListener(e -> Toast.makeText(EditTeamProfileActivity.this, "Failed to update team profile", Toast.LENGTH_SHORT).show());
    }
}
