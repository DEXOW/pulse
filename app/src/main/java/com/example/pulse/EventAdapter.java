package com.example.pulse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<DocumentSnapshot> events;

    public EventAdapter(List<DocumentSnapshot> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        DocumentSnapshot eventSnapshot = events.get(position);
        Glide.with(holder.itemView.getContext())
                .load(eventSnapshot.getString("imageUrl") == null ? "https://via.placeholder.com/150" : eventSnapshot.getString("imageUrl"))
                .centerCrop()
                .into(holder.eventBg);
        holder.eventTitle.setText(eventSnapshot.getString("name"));
        holder.capacity.setText(eventSnapshot.getString("capacity") == null ? "N/A" : eventSnapshot.getString("capacity"));
        holder.sold.setText(eventSnapshot.getString("occupied") == null ? "N/A" : eventSnapshot.getString("occupied"));
        holder.location.setText(eventSnapshot.getString("location") == null ? "Location not available" : eventSnapshot.getString("location"));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, capacity, sold, location;
        ImageView eventBg;
        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventBg = itemView.findViewById(R.id.eventBg);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            capacity = itemView.findViewById(R.id.eventCapacity);
            sold = itemView.findViewById(R.id.eventSold);
            location = itemView.findViewById(R.id.eventLocationText);
        }
    }
}
