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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "ברוכים הבאים לעמוד הראשי של BallersNet", Toast.LENGTH_SHORT).show();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ballers Net");
       // toolbar.setLogo(R.drawable.app_logo);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
           // Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          //  v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          //  return insets;
       // });
        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("error", "|**************");
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.action_main_menu2) {
            intent = new Intent(this, MainMenu.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_player_search) {
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
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


}

