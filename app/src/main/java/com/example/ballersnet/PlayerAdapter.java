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

// PlayerAdapter הוא אדפטר שמשמש לתצוגת רשימת שחקנים באמצעות RecyclerView.
public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> implements Filterable {
    // רשימת השחקנים המוצגת ב-RecyclerView.
    private List<User> playerList;
    // רשימה מלאה של כל השחקנים, משמשת לחיפוש.
    private List<User> playerListFull;
    // הפעילות שמשתמשת באדפטר זה.
    private PlayerSearch playerSearch;
    // מאזין לאירועים של לחיצה על כפתור ההודעה.
    private OnMessageClickListener messageClickListener;

    // קונסטרקטור מעודכן שמקבל את רשימת השחקנים ומאזין לאירועים.
    public PlayerAdapter(List<User> playerList, OnMessageClickListener listener, PlayerSearch playerSearch) {
        // יצירת עותק חדש של הרשימה המועברת.
        this.playerList = new ArrayList<>(playerList);
        // יצירת עותק נוסף לרשימה המלאה.
        this.playerListFull = new ArrayList<>(playerList);
        this.playerSearch = playerSearch;
        this.messageClickListener = listener;
    }

    // פונקציה לעדכון הרשימות עם רשימה חדשה.
    public void updateList(List<User> newList) {
        // עדכון הרשימה המוצגת והרשימה המלאה.
        this.playerList = new ArrayList<>(newList);
        this.playerListFull = new ArrayList<>(newList);
        // הודעה ל-RecyclerView לעדכן את התצוגה.
        notifyDataSetChanged();
    }

    // יצירת תצוגה חדשה לכל פריט ברשימה.
    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // יצירת תצוגה חדשה מהלייאאוט המוגדר ב-item_player.xml.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    // מספר הפריטים ברשימה.
    @Override
    public int getItemCount() {
        return playerList.size();
    }

    // החזרת הפילטר שמשמש לחיפוש ברשימה.
    @Override
    public Filter getFilter() {
        return playerFilter;
    }

    // פילטר לחיפוש ברשימה המלאה של השחקנים.
    private Filter playerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // רשימה שתכיל את השחקנים שעונים לחיפוש.
            List<User> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                // אם אין מחרוזת חיפוש, החזרת הרשימה המלאה.
                filteredList.addAll(playerListFull);
            } else {
                // המרת מחרוזת החיפוש לקטנות והסרת רווחים.
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User player : playerListFull) {
                    // בדיקה אם השם או העמדה מכילים את מחרוזת החיפוש.
                    if ((player.name != null && player.name.toLowerCase().contains(filterPattern)) ||
                            (player.spot != null && player.spot.toLowerCase().contains(filterPattern))) {
                        filteredList.add(player);
                    }
                }
            }
            // הכנת תוצאות החיפוש.
            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // ניקוי הרשימה המוצגת והוספת תוצאות החיפוש.
            playerList.clear();
            playerList.addAll((List) results.values);
            // הודעה ל-RecyclerView לעדכן את התצוגה.
            notifyDataSetChanged();
        }
    };

    // מחלקה פנימית שמייצגת את תצוגת כל שחקן ברשימה.
    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        // תצוגות לפרטי השחקן.
        TextView nameTextView, ageTextView, positionTextView, playerAveragePointsTextView, playerCityTextView;
        // כפתור לשליחת הודעה.
        Button messageButton;
        // תצוגה למצב השתייכות לקבוצה (לא בשימוש).
        CheckedTextView isInTeamCheckTextView;

        // קונסטרקטור שמאתחל את תצוגות השחקן.
        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.playerNameTextView);
            ageTextView = itemView.findViewById(R.id.playerAgeTextView);
            positionTextView = itemView.findViewById(R.id.playerPositionTextView);
            playerAveragePointsTextView = itemView.findViewById(R.id.playerAveragePointsTextView);
            playerCityTextView = itemView.findViewById(R.id.playerCityTextView);
            messageButton = itemView.findViewById(R.id.messageButton);
            // isInTeamCheckTextView = itemView.findViewById(R.id.isInTeamCheckTextView);

            // הגדרת מאזין לאירוע לחיצה על ה-CheckedTextView (לא בשימוש).
            // isInTeamCheckTextView.setOnClickListener(v -> {
            //     isInTeamCheckTextView.toggle();
            //     int position = getAdapterPosition();
            //     if (position != RecyclerView.NO_POSITION) {
            //         User player = playerList.get(position);
            //         player.setInMyTeam(isInTeamCheckTextView.isChecked());
            //         playerSearch.updatePlayerTeamStatus(player.userId, player.isInMyTeam());
            //     }
            // });
        }
    }

    // קישור נתוני השחקן לתצוגה המתאימה ב-RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        // קבלת השחקן הנוכחי מהרשימה.
        User player = playerList.get(position);
        // הצגת פרטי השחקן בתצוגה.
        holder.nameTextView.setText(player.name);
        holder.positionTextView.setText("Position: " + player.spot);
        holder.ageTextView.setText("Age: " + player.age);
        holder.playerAveragePointsTextView.setText("Average Points: " + player.avg);
        holder.playerCityTextView.setText("City: " + player.city);

        // הגדרת מאזין לאירוע לחיצה על כפתור ההודעה.
        holder.messageButton.setOnClickListener(v -> messageClickListener.onMessageClick(player));

        // הגדרת מצב ה-CheckedTextView (לא בשימוש).
        // holder.isInTeamCheckTextView.setChecked(player.isInMyTeam());
    }

    // ממשק למאזינים של לחיצה על כפתור ההודעה.
    public interface OnMessageClickListener {
        void onMessageClick(User player);
    }
}
