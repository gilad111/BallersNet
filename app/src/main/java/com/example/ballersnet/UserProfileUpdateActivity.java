package com.example.ballersnet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// UserProfileUpdateActivity extends MainActivity and handles user profile updates
public class UserProfileUpdateActivity extends MainActivity {
    // UI elements for user input
    private EditText nameEditText, emailEditText, profilePictureUrlEditText;
    private EditText preferredPositionEditText, ageEditText, averagePointsEditText, cityEditText;
    private CheckBox isTeamManagerCheckBox;
    private Button saveButton;
    // Firebase authentication and database references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private User globalUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_user_profile_update);
        // Display a toast message
        Toast.makeText(this, "עדכון פרטי שחקן", Toast.LENGTH_SHORT).show();
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Update Profile");
        setSupportActionBar(toolbar);
        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        // Load user data from Firebase
        loadUserData();

        // Set click listener for save button
        saveButton.setOnClickListener(v -> updateUserProfile());
    }
    // Method to initialize UI elements
    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        profilePictureUrlEditText = findViewById(R.id.profilePictureUrlEditText);
        preferredPositionEditText = findViewById(R.id.preferredPositionEditText);
        ageEditText = findViewById(R.id.ageEditText);
        averagePointsEditText = findViewById(R.id.averagePointsEditText);
        cityEditText = findViewById(R.id.cityEditText);
        isTeamManagerCheckBox = findViewById(R.id.isTeamManagerCheckBox);
        saveButton = findViewById(R.id.saveButton);
    }
    // Method to load user data from Firebase
    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        Log.e("error", userId);
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    globalUser = user;
                    // Populate UI elements with user data
                    nameEditText.setText(mAuth.getCurrentUser().getDisplayName());
                    emailEditText.setText(user.email);
                    profilePictureUrlEditText.setText(user.profileImage);
                    preferredPositionEditText.setText(user.spot);
                    ageEditText.setText(String.valueOf(user.age));
                    averagePointsEditText.setText(String.valueOf(user.avg));
                    Log.e("error", String.valueOf(user.city));
                    cityEditText.setText(user.city);
                    isTeamManagerCheckBox.setChecked(user.isAdmin);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfileUpdateActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Method to update user profile in Firebase
    private void updateUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        // Get values from UI elements
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String profilePictureUrl = profilePictureUrlEditText.getText().toString().trim();
        String preferredPosition = preferredPositionEditText.getText().toString().trim();
        int age = Integer.parseInt(ageEditText.getText().toString().trim());
        double averagePoints = Double.parseDouble(averagePointsEditText.getText().toString().trim());
        String city = cityEditText.getText().toString().trim();
        boolean isTeamManager = isTeamManagerCheckBox.isChecked();

        // Create updated User object
        User updatedUser = new User(userId, name, globalUser.teamName, email, profilePictureUrl,age, preferredPosition, averagePoints, isTeamManager, city);

        // Update user data in Firebase
        mDatabase.child("Users").child(userId).setValue(updatedUser)
                .addOnSuccessListener(aVoid -> Toast.makeText(UserProfileUpdateActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(UserProfileUpdateActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }
}

