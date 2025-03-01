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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

// TeamProfile מציג את פרטי הקבוצה ויורש מ-MainActivity
public class TeamProfile extends MainActivity {
    private TextView teamNameTextView, homeCourtTextView, recordTextView, neededPositionsTextView, managerNameTextView;
    private RecyclerView playersRecyclerView;
    private Button editButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String teamId;
    private boolean isManager = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // אתחול Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // מגדיר את הלייאאוט של המסך
        setContentView(R.layout.activity_team_profile);

        // אתחול הרכיבים הגרפיים
        initializeViews();

        // טעינת נתוני הקבוצה
        loadTeamData();
        // מאפשר תצוגה מקצה לקצה
        EdgeToEdge.enable(this);
        // מציג הודעת טוסט קצרה
        Toast.makeText(this, "פרופיל קבוצות", Toast.LENGTH_SHORT).show();
        // מאתחל את ה-Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Team Profile");
        // מגדיר את ה-Toolbar כ-ActionBar של האפליקציה
        setSupportActionBar(toolbar);
    }
    private void initializeViews() {
        teamNameTextView = findViewById(R.id.teamNameTextView);
        homeCourtTextView = findViewById(R.id.homeCourtLocationTextView);
        recordTextView = findViewById(R.id.recordTextView);
        neededPositionsTextView = findViewById(R.id.neededPositionsTextView);
        managerNameTextView = findViewById(R.id.managerNameTextView);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        editButton = findViewById(R.id.editButton);

        editButton.setOnClickListener(v -> {
            if (isManager) {
                // פתיחת מסך עריכה
                Intent intent = new Intent(TeamProfile.this, EditTeamProfileActivity.class);
                intent.putExtra("teamId", teamId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Only team managers can edit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeamData() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    teamId = user.teamId;
                    isManager = user.isAdmin;
                    loadTeamDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeamProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeamDetails() {
        String teamId = mAuth.getCurrentUser().getUid();
        if (teamId == null) {
            Toast.makeText(this, "No team associated with this user", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabase.child("Teams").child(teamId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Team team = dataSnapshot.getValue(Team.class);
                if (team != null) {
                    updateUI(team);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeamProfile.this, "Failed to load team data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Team team) {
        teamNameTextView.setText(team.name);
        homeCourtTextView.setText("Home Court: " + team.homeCourtLocation);
        recordTextView.setText("Record: " + team.wins + "-" + team.losses);
        neededPositionsTextView.setText("Needed Positions: " + String.join(", ", team.neededPositions));
        managerNameTextView.setText("Manager: " + team.managerName);

        // הגדרת מתאם לרשימת השחקנים
        PlayerListAdapter adapter = new PlayerListAdapter(team.playerIds);
        playersRecyclerView.setAdapter(adapter);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // מתאם לרשימת השחקנים
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
                    // טיפול בשגיאה
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
