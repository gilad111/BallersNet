package com.example.ballersnet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class CreateTeamActivity extends MainActivity {

    // UI elements
    private EditText teamNameEditText, homeCourtEditText, neededPositionsEditText;
    private Button createTeamButton;

    // Firebase references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Create Team");
        setSupportActionBar(toolbar);
        Toast.makeText(this, "Create Team", Toast.LENGTH_SHORT).show();

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        teamNameEditText = findViewById(R.id.teamNameEditText);
        homeCourtEditText = findViewById(R.id.homeCourtEditText);
        createTeamButton = findViewById(R.id.createTeamButton);
        neededPositionsEditText = findViewById(R.id.neededPositionsEditText);

        // Set click listener for create team button
        createTeamButton.setOnClickListener(v -> createTeam());
    }

    private void createTeam() {
        // Get current user
        String userId = mAuth.getCurrentUser().getUid();

        if (teamNameEditText.getText().toString().isEmpty() ||
                homeCourtEditText.getText().toString().isEmpty() ||
                neededPositionsEditText.getText().toString().isEmpty())
        {
            Toast.makeText(CreateTeamActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
        }
        else {
            // Check if user is a team manager
            mDatabase.child("Users").child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        if (user.isAdmin) {
                            // User is a team manager, proceed with team creation
                            if (user.teamName == null || user.teamName.isEmpty()) {
                                // User doesn't have a team, create new team
                                String teamName = teamNameEditText.getText().toString().trim();
                                String homeCourt = homeCourtEditText.getText().toString().trim();
                                String neededPositions = neededPositionsEditText.getText().toString().trim();

                                // Check if team name is already taken
                                mDatabase.child("Teams").orderByChild("name").equalTo(teamName).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Toast.makeText(CreateTeamActivity.this, "Team name is already taken", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Create new Team object
                                            Team newTeam = new Team(teamName, homeCourt, neededPositions, user.name);

                                            // Save the team to Firebase
                                            mDatabase.child("Teams").child(teamName).setValue(newTeam)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Update user's teamName
                                                        mDatabase.child("Users").child(userId).child("teamName").setValue(teamName);
                                                        Toast.makeText(CreateTeamActivity.this, "Team created successfully", Toast.LENGTH_SHORT).show();
                                                        finish(); // Close the activity
                                                    })
                                                    .addOnFailureListener(e -> Toast.makeText(CreateTeamActivity.this, "Failed to create team", Toast.LENGTH_SHORT).show());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(CreateTeamActivity.this, "Failed to check team name", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // User already has a team
                                Toast.makeText(CreateTeamActivity.this, "You already have a team", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // User is not a team manager
                            Toast.makeText(CreateTeamActivity.this, "Only team managers can create teams", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(CreateTeamActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
