package com.example.ballersnet;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

// PlayerProfile מציג את פרטי השחקן ויורש מ-MainActivity
public class PlayerProfile extends MainActivity {

    // הגדרת משתנים לתצוגת פרטי השחקן
    private TextView usernameTextView, emailTextView, ageTextView, cityTextView;
    private TextView preferredPositionTextView, averagePointsTextView;
    // משתנים לאימות והתחברות למסד הנתונים
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);
        Toast.makeText(this, "פרופיל שחקן", Toast.LENGTH_SHORT).show();
        // הגדרת ה-Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Player Profile");
        setSupportActionBar(toolbar);

        // אתחול הרכיבים הגרפיים
        initializeViews();

        // אתחול Firebase Auth ו-Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // טעינת נתוני השחקן
        loadPlayerData();
    }

    // אתחול הרכיבים הגרפיים
    private void initializeViews() {
        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        ageTextView = findViewById(R.id.ageTextView);
        cityTextView = findViewById(R.id.cityTextView);
        preferredPositionTextView = findViewById(R.id.preferredPositionTextView);
        averagePointsTextView = findViewById(R.id.averagePointsTextView);
    }

    // טעינת נתוני השחקן מ-Firebase
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
                        // הצגת נתוני המשתמש בתצוגה
                        usernameTextView.setText("Username: " + user.userId);
                        emailTextView.setText("Email: " + user.email);
                        ageTextView.setText("Age: " + user.age);
                        cityTextView.setText("City: " + user.city);
                        preferredPositionTextView.setText("Preferred Position: " + user.spot);
                        averagePointsTextView.setText("Average Points: " + user.avg);
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
}
