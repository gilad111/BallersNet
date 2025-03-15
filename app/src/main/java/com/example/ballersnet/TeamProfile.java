package com.example.ballersnet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

// TeamProfile displays team details and inherits from AppCompatActivity
public class TeamProfile extends MainActivity {
    // UI components
    private TextView teamNameTextView, homeCourtTextView, recordTextView, neededPositionsTextView, managerNameTextView;
    private RecyclerView playersRecyclerView;
    private Button editButton;

    // Firebase authentication and database references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Team name and whether the user is a manager
    private String teamName;
    private boolean isManager = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_profile);

        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Team Profile");
        setSupportActionBar(toolbar);


        // Initialize UI components
        initializeViews();

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Load team data
        loadTeamData();

        // Enable edge-to-edge display
        //EdgeToEdge.enable(this);
        // Show a toast message
        Toast.makeText(this, "Team Profile", Toast.LENGTH_SHORT).show();


    }

    // Initialize UI components
    private void initializeViews() {
        teamNameTextView = findViewById(R.id.teamNameTextView);
        homeCourtTextView = findViewById(R.id.homeCourtLocationTextView);
        recordTextView = findViewById(R.id.recordTextView);
        neededPositionsTextView = findViewById(R.id.neededPositionsTextView);
        managerNameTextView = findViewById(R.id.managerNameTextView);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        editButton = findViewById(R.id.editButton);

        // Set click listener for the edit button
        editButton.setOnClickListener(v -> {
            if (isManager) {
                // Open edit team profile activity if user is a manager
                Intent intent = new Intent(TeamProfile.this, EditTeamProfileActivity.class);
                intent.putExtra("teamName", teamName);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Only team managers can edit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load team data by first fetching the user's team name
    private void loadTeamData() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    teamName = user.teamName; // Assuming teamName is stored in User class
                    isManager = user.isAdmin;
                    loadTeamDetails(teamName); // Load team details using the team name
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeamProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load team details from the database
    private void loadTeamDetails(String teamName) {
        Toast.makeText(TeamProfile.this, "Team name is " + teamName, Toast.LENGTH_SHORT).show();
        mDatabase.child("Teams").orderByChild("name").equalTo(teamName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Team team = child.getValue(Team.class);
                    if (team != null) {
                        updateUI(team); // Update the UI with team details
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeamProfile.this, "Failed to load team data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update the UI with team details
    private void updateUI(Team team) {
        teamNameTextView.setText("Team Name: " + team.name);
//        homeCourtTextView.setText("b");
        recordTextView.setText("Record: " + team.wins + "-" + team.losses);
        neededPositionsTextView.setText("Needed Positions: " + String.join(", ", team.neededPositions));
        managerNameTextView.setText("Manager: " + team.managerName);

        // Set up the player list adapter
//        PlayerListAdapter adapter = new PlayerListAdapter(team.playerIds);
//        playersRecyclerView.setAdapter(adapter);
//        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Adapter for the player list
    private class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder> {
        private List<String> playerIds;

        PlayerListAdapter(List<String> playerIds) {
            this.playerIds = playerIds;
        }

        @NonNull
        @Override
        public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new PlayerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
            String playerId = playerIds.get(position);
            mDatabase.child("Users").child(playerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User player = dataSnapshot.getValue(User.class);
                    if (player != null) {
                        holder.playerNameTextView.setText(player.name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }

        @Override
        public int getItemCount() {
            return playerIds.size();
        }

        class PlayerViewHolder extends RecyclerView.ViewHolder {
            TextView playerNameTextView;

            PlayerViewHolder(View itemView) {
                super(itemView);
                playerNameTextView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
