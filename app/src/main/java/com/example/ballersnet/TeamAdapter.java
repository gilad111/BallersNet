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
    // עדכון התצוגה של כל פריט ברשימה
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        // קבלת הקבוצה הנוכחית מהרשימה
        Team team = teamList.get(position);

        // עדכון התצוגה של הקבוצה
        holder.nameTextView.setText(team.name);
        holder.homeCourtLocationTextView.setText("Home Court: " + team.homeCourtLocation);
        holder.teamRecordTextView.setText("Record: " + team.wins + "-" + team.losses);
        holder.neededPositionsTextView.setText("Needed Positions: " + String.join(", ", team.neededPositions));

        // הגדרת מאזין לאירוע לחיצה על כפתור הצטרפות לקבוצה
        holder.joinButton.setOnClickListener(v -> joinTeamClickListener.onJoinTeamClick(team));
    }

    @Override
    // מספר הפריטים ברשימה
    public int getItemCount() {
        return teamList.size();
    }

    @Override
    // פונקציה לקבלת הפילטר של הרשימה
    public Filter getFilter() {
        return teamFilter;
    }
    // פילטר לחיפוש ברשימה
    private Filter teamFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // יצירת רשימה חדשה לתוצאות החיפוש
            List<Team> filteredList = new ArrayList<>();

            // אם אין מחרוזת חיפוש, החזרת כל הרשימה המלאה
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(teamListFull);
            } else {
                // המרת המחרוזת לקטנות והסרת רווחים מיותרים
                String filterPattern = constraint.toString().toLowerCase().trim();

                // חיפוש ברשימה המלאה
                for (Team team : teamListFull) {
                    // בדיקה אם השם או המיקום של הקבוצה מכילים את מחרוזת החיפוש
                    if ((team.name != null && team.name.toLowerCase().contains(filterPattern)) ||
                            (team.homeCourtLocation != null && team.homeCourtLocation.toLowerCase().contains(filterPattern))) {
                        filteredList.add(team);
                    }
                }
            }
            // יצירת תוצאות החיפוש
            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            teamList.clear();
            // הוספת תוצאות החיפוש לרשימה הנוכחית
            teamList.addAll((List) results.values);
            // הודעה לאדפטר על השינויים כדי שיעדכן את ה-RecyclerView
            notifyDataSetChanged();
        }
    };

    // תצוגה לכל פריט ברשימה
    public class TeamViewHolder extends RecyclerView.ViewHolder {
        // תצוגות לפרטים של הקבוצה
        TextView nameTextView, homeCourtLocationTextView, teamRecordTextView, neededPositionsTextView;
        // כפתור להצטרפות לקבוצה
        Button joinButton;

        // קונסטרקטור לתצוגה של פריט ברשימה
        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            // הגדרת התצוגות לפרטים של הקבוצה
            nameTextView = itemView.findViewById(R.id.teamNameTextView);
            homeCourtLocationTextView = itemView.findViewById(R.id.homeCourtLocationTextView);
            teamRecordTextView = itemView.findViewById(R.id.teamRecordTextView);
            neededPositionsTextView = itemView.findViewById(R.id.neededPositionsTextView);
            // הגדרת הכפתור להצטרפות לקבוצה
            joinButton = itemView.findViewById(R.id.joinTeamButton);
        }
    }

    // ממשק למאזין לאירוע לחיצה על כפתור הצ�


    public interface OnJoinTeamClickListener {
        void onJoinTeamClick(Team team);
    }
}
