package com.example.ballersnet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle menu item clicks
        if (item.getItemId() == R.id.action_player_search) {
            intent = new Intent(this, PlayerSearch.class);
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

