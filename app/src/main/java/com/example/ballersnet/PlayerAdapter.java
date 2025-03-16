package com.example.ballersnet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> implements Filterable {
    private List<User> playerList; // רשימת השחקנים המוצגת
    private List<User> playerListFull; // רשימה מלאה של כל השחקנים
    private PlayerSearch playerSearch;
    private OnMessageClickListener messageClickListener;

    // קונסטרקטור מעודכן
    public PlayerAdapter(List<User> playerList, OnMessageClickListener listener, PlayerSearch playerSearch) {
        this.playerList = new ArrayList<>(playerList); // יצירת עותק חדש
        this.playerListFull = new ArrayList<>(playerList); // יצירת עותק נוסף לרשימה המלאה
        this.playerSearch = playerSearch;
        this.messageClickListener = listener;
    }

    // פונקציה חדשה לעדכון הרשימות
    public void updateList(List<User> newList) {
        this.playerList = new ArrayList<>(newList);
        this.playerListFull = new ArrayList<>(newList);
        notifyDataSetChanged(); // עדכון ה-RecyclerView
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    @Override
    public Filter getFilter() {
        return playerFilter;
    }

    private Filter playerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(playerListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User player : playerListFull) {
                    // בדיקה אם השם או העמדה מכילים את מחרוזת החיפוש
                    if ((player.name != null && player.name.toLowerCase().contains(filterPattern)) ||
                            (player.spot != null && player.spot.toLowerCase().contains(filterPattern))) {
                        filteredList.add(player);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            playerList.clear();
            playerList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ageTextView, positionTextView, playerAveragePointsTextView, playerCityTextView;
        Button messageButton;
        CheckedTextView isInTeamCheckTextView;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.playerNameTextView);
            ageTextView = itemView.findViewById(R.id.playerAgeTextView);
            positionTextView = itemView.findViewById(R.id.playerPositionTextView);
            playerAveragePointsTextView = itemView.findViewById(R.id.playerAveragePointsTextView);
            playerCityTextView = itemView.findViewById(R.id.playerCityTextView);
            messageButton = itemView.findViewById(R.id.messageButton);
          //  isInTeamCheckTextView = itemView.findViewById(R.id.isInTeamCheckTextView);

            // Set up click listener for the CheckedTextView
            isInTeamCheckTextView.setOnClickListener(v -> {
                isInTeamCheckTextView.toggle();
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    User player = playerList.get(position);
                    // player.setInMyTeam(isInTeamCheckTextView.isChecked());
                    // playerSearch.updatePlayerTeamStatus(player.userId, player.isInMyTeam());
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        User player = playerList.get(position);
        holder.nameTextView.setText(player.name);
        holder.positionTextView.setText("Position: " + player.spot);
        holder.ageTextView.setText("Age: " + player.age);
        holder.playerAveragePointsTextView.setText("Average Points: " + player.avg);
        holder.playerCityTextView.setText("City: " + player.city);

        holder.messageButton.setOnClickListener(v -> messageClickListener.onMessageClick(player));

        // Set the checked state of the CheckedTextView
        // holder.isInTeamCheckTextView.setChecked(player.isInMyTeam());
    }

    public interface OnMessageClickListener {
        void onMessageClick(User player);
    }
}
