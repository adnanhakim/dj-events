package com.teamvoid.djevents.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.teamvoid.djevents.Models.Event;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context;
    private List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        if (event.getImageUrl() != null && !event.getImageUrl().equals("")) {
            Picasso.get()
                    .load(event.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.sivImage);
        }

        holder.tvName.setText(event.getName());
        holder.tvCommitteeName.setText(event.getCommitteeName());
        holder.tvDescription.setText(event.getDescription());
        holder.tvStatus.setText(event.getStatus());

        if (event.getStatus() != null) {
            if (event.getStatus().equals(Constants.REGISTRATIONS_OPEN)) {
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
            } else if (event.getStatus().equals(Constants.REGISTRATIONS_CLOSED)) {
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.errorRed));
            } else holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.textColor));
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView sivImage;
        private TextView tvName, tvCommitteeName, tvDescription, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sivImage = itemView.findViewById(R.id.sivItemEventImage);
            tvName = itemView.findViewById(R.id.tvItemEventName);
            tvCommitteeName = itemView.findViewById(R.id.tvItemEventCommitteeName);
            tvDescription = itemView.findViewById(R.id.tvItemEventDescription);
            tvStatus = itemView.findViewById(R.id.tvItemEventStatus);
        }
    }
}
