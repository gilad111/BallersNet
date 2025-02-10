package com.example.ballersnet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> implements Filterable {
    private List<Team> teamList;
    private List<Team> teamListFull;
    private OnJoinTeamClickListener joinTeamClickListener;

    public TeamAdapter(List<Team> teamList, OnJoinTeamClickListener listener) {
        this.teamList = new ArrayList<>(teamList);
        this.teamListFull = new ArrayList<>(teamList);
        this.joinTeamClickListener = listener;
    }

    public void updateList(List<Team> newList) {
        this.teamList = new ArrayList<>(newList);
        this.teamListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teamList.get(position);
        holder.nameTextView.setText(team.name);
        holder.locationTextView.setText(team.location);
        holder.descriptionTextView.setText(team.description);
        holder.joinButton.setOnClickListener(v -> joinTeamClickListener.onJoinTeamClick(team));
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    @Override
    public Filter getFilter() {
        return teamFilter;
    }

    private Filter teamFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Team> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(teamListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Team team : teamListFull) {
                    if ((team.name != null && team.name.toLowerCase().contains(filterPattern)) ||
                            (team.location != null && team.location.toLowerCase().contains(filterPattern))) {
                        filteredList.add(team);
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
            teamList.clear();
            teamList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, locationTextView, descriptionTextView;
        Button joinButton;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.teamNameTextView);
            locationTextView = itemView.findViewById(R.id.teamLocationTextView);
            descriptionTextView = itemView.findViewById(R.id.teamDescriptionTextView);
            joinButton = itemView.findViewById(R.id.joinTeamButton);
        }
    }

    public interface OnJoinTeamClickListener {
        void onJoinTeamClick(Team team);
    }
}
