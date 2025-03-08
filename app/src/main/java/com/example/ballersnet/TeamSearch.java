package com.example.ballersnet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class TeamSearch extends MainActivity {

    private RecyclerView recyclerView;
    private TeamAdapter adapter;
    private List<Team> teamList;
    private DatabaseReference teamsRef;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_search);

        // הגדרת ה-Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Team Search");
        setSupportActionBar(toolbar);

        // אתחול רכיבי ה-UI
        recyclerView = findViewById(R.id.recyclerViewTeams);
        searchView = findViewById(R.id.searchViewTeams);

        // הגדרת ה-RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamList = new ArrayList<>();
        adapter = new TeamAdapter(teamList, this::onJoinTeamButtonClick);
        recyclerView.setAdapter(adapter);

        // אתחול הפניה למסד הנתונים
        teamsRef = FirebaseDatabase.getInstance().getReference("Teams");

        // טעינת הקבוצות
        loadTeams();

        // הגדרת פונקציונליות החיפוש
        setupSearch();
    }

    // פונקציה לטעינת הקבוצות מ-Firebase
    private void loadTeams() {
        teamsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Team> newTeamList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Team team = snapshot.getValue(Team.class);
                    if (team != null) {
                        newTeamList.add(team);
                    }
                }
                adapter.updateList(newTeamList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeamSearch.this, "Error loading teams", Toast.LENGTH_SHORT).show();
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

    // פונקציה לטיפול בלחיצה על כפתור ההצטרפות לקבוצה
    private void onJoinTeamButtonClick(Team team) {
        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Update user's team ID in Firebase
        FirebaseDatabase.getInstance().getReference("Users").child(userId).child("teamName").setValue(team.name)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Joined team successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to join team", Toast.LENGTH_SHORT).show());
    }
}

