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
// TeamAdapter הוא אדפטר שמשמש לתצוגת רשימת קבוצות באמצעות RecyclerView.
public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> implements Filterable {
    // רשימת הקבוצות הנוכחית
    private List<Team> teamList;
    // רשימת הקבוצות המלאה לצורך חיפוש
    private List<Team> teamListFull;
    // מאזין לאירוע לחיצה על כפתור הצטרפות לקבוצה
    private OnJoinTeamClickListener joinTeamClickListener;

    // קונסטרקטור לאדפטר שמקבל רשימת קבוצות ומאזין לאירוע לחיצה על כפתור הצטרפות
    public TeamAdapter(List<Team> teamList, OnJoinTeamClickListener listener) {
        // העתקת הרשימה המקורית לרשימה נוכחית ולרשימה מלאה
        this.teamList = new ArrayList<>(teamList);
        this.teamListFull = new ArrayList<>(teamList);
// הגדרת המאזין לאירוע לחיצה על כפתור הצטרפות
        this.joinTeamClickListener = listener;
    }
    // פונקציה לעדכון הרשימה הנוכחית עם רשימה חדשה של קבוצות
    public void updateList(List<Team> newList) {
        // עדכון הרשימה הנוכחית והמלאה עם הרשימה החדשה
        this.teamList = new ArrayList<>(newList);
        this.teamListFull = new ArrayList<>(newList);
        // הודעה לאדפטר על השינויים כדי שיעדכן את ה-RecyclerView
        notifyDataSetChanged();
    }


    // פונקציה ליצירת תצוגה לכל פריט ברשימה
    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // יצירת תצוגה לפריט ברשימה מתוך הלייאאוט המוגדר
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    // פונקיה של עדכון התצוגה של כל פריט ברשימה
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        // קבלת הקבוצה הנוכחית מהרשימה
        Team team = teamList.get(position);

        // עדכון התצוגה של הקבוצה
        holder.nameTextView.setText(team.name);  // הצגת שם הקבוצה
        holder.homeCourtLocationTextView.setText("Home Court: " + team.homeCourtLocation);   // הצגת מיקום המגרש הביתי
        holder.teamRecordTextView.setText("Record: " + team.wins + "-" + team.losses);   // הצגת המאזן של הקבוצה
        holder.neededPositionsTextView.setText("Needed Positions: " + String.join(", ", team.neededPositions));   // הצגת עמדות הנדרשות

        // הגדרת מאזין לאירוע לחיצה על כפתור הצטרפות לקבוצה
        holder.joinButton.setOnClickListener(v -> joinTeamClickListener.onJoinTeamClick(team));  // קריאה למאזין כאשר לוחצים על כפתור ההצטרפות
    }

    @Override
    // מספר הפריטים ברשימה
    public int getItemCount() {
        return teamList.size();
    }

    @Override
    // פונקציה לקבלת הפילטר של הרשימה
    public Filter getFilter() {
        return teamFilter;   // החזרת הפילטר לחיפוש ברשימה
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
            results.values = filteredList;  // הגדרת התוצאות
            results.count = filteredList.size();   // הגדרת מספר התוצאות
            return results;   // החזרת התוצאות
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
            nameTextView = itemView.findViewById(R.id.teamNameTextView);  // תצוגת שם הקבוצה
            homeCourtLocationTextView = itemView.findViewById(R.id.homeCourtLocationTextView);  // תצוגת מיקום המגרש הביתי
            teamRecordTextView = itemView.findViewById(R.id.teamRecordTextView);  // תצוגת המאזן של הקבוצה
            neededPositionsTextView = itemView.findViewById(R.id.neededPositionsTextView);  // תצוגת עמדות הנדרשות
            // הגדרת הכפתור להצטרפות לקבוצה
            joinButton = itemView.findViewById(R.id.joinTeamButton);
        }
    }

    // ממשק למאזין לאירוע לחיצה על כפתור הצטרפות לקבוצה
    public interface OnJoinTeamClickListener {
        void onJoinTeamClick(Team team);
    }
}
