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

public class UserProfileUpdateActivity extends MainActivity {
    private EditText usernameEditText, emailEditText, profilePictureUrlEditText;
    private EditText preferredPositionEditText, ageEditText, averagePointsEditText, cityEditText;
    private CheckBox isTeamManagerCheckBox;
    private Button saveButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_update);
        Toast.makeText(this, "עדכון פרטי שחקן", Toast.LENGTH_SHORT).show();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Update Profile");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        loadUserData();

        saveButton.setOnClickListener(v -> updateUserProfile());
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        profilePictureUrlEditText = findViewById(R.id.profilePictureUrlEditText);
        preferredPositionEditText = findViewById(R.id.preferredPositionEditText);
        ageEditText = findViewById(R.id.ageEditText);
        averagePointsEditText = findViewById(R.id.averagePointsEditText);
        cityEditText = findViewById(R.id.cityEditText);
        isTeamManagerCheckBox = findViewById(R.id.isTeamManagerCheckBox);
        saveButton = findViewById(R.id.saveButton);
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        Log.e("error", userId);
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    usernameEditText.setText(user.userId);
                    emailEditText.setText(user.email);
                    profilePictureUrlEditText.setText(user.profileImage);
                    preferredPositionEditText.setText(user.spot);
                    ageEditText.setText(String.valueOf(user.age));
                    averagePointsEditText.setText(String.valueOf(user.avg));
                    cityEditText.setText(user.city);
                    isTeamManagerCheckBox.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfileUpdateActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String profilePictureUrl = profilePictureUrlEditText.getText().toString().trim();
        String preferredPosition = preferredPositionEditText.getText().toString().trim();
        int age = Integer.parseInt(ageEditText.getText().toString().trim());
        double averagePoints = Double.parseDouble(averagePointsEditText.getText().toString().trim());
        String city = cityEditText.getText().toString().trim();
        boolean isTeamManager = isTeamManagerCheckBox.isChecked();

        User updatedUser = new User(userId, username, email, profilePictureUrl, "Ramat HaSharon", age, preferredPosition, averagePoints, isTeamManager, city);

        mDatabase.child("Users").child(userId).setValue(updatedUser)
                .addOnSuccessListener(aVoid -> Toast.makeText(UserProfileUpdateActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(UserProfileUpdateActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }
}

