package com.example.ballersnet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerSearch extends MainActivity {

    private RecyclerView recyclerView;
    private PlayerAdapter adapter;
    private List<User> playerList;
    private DatabaseReference usersRef;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_search);

        // הגדרת ה-Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Player Search");
        setSupportActionBar(toolbar);

        // אתחול רכיבי ה-UI
        recyclerView = findViewById(R.id.recyclerViewPlayers);
        searchView = findViewById(R.id.searchViewPlayers);

        // הגדרת ה-RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerList = new ArrayList<>();
        adapter = new PlayerAdapter(playerList, this::onMessageButtonClick,this);
        recyclerView.setAdapter(adapter);

        // אתחול הפניה למסד הנתונים
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // טעינת השחקנים
        loadPlayers();

        // הגדרת פונקציונליות החיפוש
        setupSearch();
    }

    // פונקציה לטעינת השחקנים מ-Firebase
    private void loadPlayers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> newPlayerList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User player = snapshot.getValue(User.class);
                    if (player != null && !player.isAdmin) {
                        newPlayerList.add(player);
                    }
                }
                adapter.updateList(newPlayerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PlayerSearch.this, "Error loading players", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // פונקציה להגדרת החיפוש
    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    // פונקציה לטיפול בלחיצה על כפתור ההודעה
    private void onMessageButtonClick(User player) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey " + player.name + ", I found you on BallersNet!");
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    // בדיקה אם אפליקציה מותקנת
    private boolean isAppInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void updatePlayerTeamStatus(String playerId, boolean isInTeam) {
        DatabaseReference playerRef = FirebaseDatabase.getInstance().getReference("Users").child(playerId);
        playerRef.child("isInMyTeam").setValue(isInTeam)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Player team status updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update player team status", Toast.LENGTH_SHORT).show());
    }
}
