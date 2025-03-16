package com.example.ballersnet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.webauthn.Cbor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

// MainActivity serves as the main screen of the application
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ballers Net");
       // toolbar.setLogo(R.drawable.app_logo);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        Log.e("error", "|**************");
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;

    }

    public boolean onPrepareOptionsMenu(Menu menu){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Check if user is admin
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.getReference("Users").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().getValue(User.class);
                if (user != null) {
                    if (user.isAdmin) {
                        // User is admin, show Create Team and player search items
                        MenuItem createTeamItem = menu.findItem(R.id.action_create_team);
                        createTeamItem.setVisible(true);
                        MenuItem editPlayerSearchItem = menu.findItem(R.id.action_player_search);
                        editPlayerSearchItem.setVisible(true);
                    } else {
                        // User is not admin, hide Create Team and player search items
                        MenuItem createTeamItem = menu.findItem(R.id.action_create_team);
                        createTeamItem.setVisible(false);
                        MenuItem editPlayerSearchItem = menu.findItem(R.id.action_player_search);
                        editPlayerSearchItem.setVisible(false);
                    }
                }
            } else {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle menu item clicks
        if (item.getItemId() == R.id.action_player_search) {
            intent = new Intent(this, PlayerSearch.class);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.action_create_team) {
            intent = new Intent(this, CreateTeamActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_team_profile) {
            intent = new Intent(this, TeamProfile.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_team_search) {
            intent = new Intent(this, TeamSearch.class);
            startActivity(intent);
            return true;
        } else if( item.getItemId() == R.id.action_player_profile) {
            intent = new Intent(this, PlayerProfile.class);
            startActivity(intent);
            return true;
        } else if( item.getItemId() == R.id.activity_user_profile_update) {
            intent = new Intent(this, UserProfileUpdateActivity.class);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


}

