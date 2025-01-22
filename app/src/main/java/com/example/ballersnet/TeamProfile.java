package com.example.ballersnet;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// TeamProfile מציג את פרטי הקבוצה ויורש מ-MainActivity
public class TeamProfile extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מאפשר תצוגה מקצה לקצה
        EdgeToEdge.enable(this);
        // מגדיר את הלייאאוט של המסך
        setContentView(R.layout.activity_team_profile);
        // מציג הודעת טוסט קצרה
        Toast.makeText(this, "פרופיל קבוצות", Toast.LENGTH_SHORT).show();
        // מאתחל את ה-Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Team Profile");
        // מגדיר את ה-Toolbar כ-ActionBar של האפליקציה
        setSupportActionBar(toolbar);
    }
}
