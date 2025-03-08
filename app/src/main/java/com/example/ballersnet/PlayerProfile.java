package com.example.ballersnet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// PlayerProfile displays player details and inherits from MainActivity
public class PlayerProfile extends MainActivity {

    // Define variables for displaying player details
    private TextView nameTextView, emailTextView, ageTextView, cityTextView;
    private TextView preferredPositionTextView, averagePointsTextView;
    private Button leaveTeamButton;
    private TextView teamStatusTextView;

    // Firebase authentication and database references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);
        Toast.makeText(this, "Player Profile", Toast.LENGTH_SHORT).show();
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Player Profile");
        setSupportActionBar(toolbar);

        // Initialize UI components
        initializeViews();

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Load player data
        loadPlayerData();
    }

    // Initialize UI components
    private void initializeViews() {
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        ageTextView = findViewById(R.id.ageTextView);
        cityTextView = findViewById(R.id.cityTextView);
        preferredPositionTextView = findViewById(R.id.preferredPositionTextView);
        averagePointsTextView = findViewById(R.id.averagePointsTextView);
        leaveTeamButton = findViewById(R.id.leaveTeamButton);
        teamStatusTextView = findViewById(R.id.teamStatusTextView);

        // Set click listener for the leave team button
        leaveTeamButton.setOnClickListener(v -> leaveTeam());
    }

    // Load player data from Firebase
    private void loadPlayerData() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(PlayerProfile.this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Display user data in the UI
                        nameTextView.setText("name: " + mAuth.getCurrentUser().getDisplayName());
                        emailTextView.setText("Email: " + user.email);
                        ageTextView.setText("Age: " + user.age);
                        cityTextView.setText("City: " + user.city);
                        preferredPositionTextView.setText("Preferred Position: " + user.spot);
                        averagePointsTextView.setText("Average Points: " + user.avg);

                        // Update team status
                        if (!user.teamName.isEmpty()) {
                            teamStatusTextView.setText("Team: " + user.teamName);
                        } else {
                            teamStatusTextView.setText("No team assigned");
                        }
                    }
                } else {
                    Toast.makeText(PlayerProfile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PlayerProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to handle leaving the team
    private void leaveTeam() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(PlayerProfile.this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (!user.teamName.isEmpty()) {
                        // Update user data to remove team name
                        user.teamName = "";
                        mDatabase.child("Users").child(userId).setValue(user).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(PlayerProfile.this, "Left team successfully", Toast.LENGTH_SHORT).show();
                                // Update UI to reflect no team
                                teamStatusTextView.setText("No team assigned");
                            } else {
                                Toast.makeText(PlayerProfile.this, "Failed to leave team", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(PlayerProfile.this, "You are not part of any team", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PlayerProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
