package com.teamvoid.djevents.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.teamvoid.djevents.Models.Committee;
import com.teamvoid.djevents.R;

import java.util.List;

public class CommitteesAdapter extends RecyclerView.Adapter<CommitteesAdapter.ViewHolder> {

    private Context context;
    private List<Committee> committees;

    public CommitteesAdapter(Context context, List<Committee> committees) {
        this.context = context;
        this.committees = committees;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_committee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Committee committee = committees.get(position);

        if (committee.getImageUrl() != null && !committee.getImageUrl().equals("")) {
            Picasso.get()
                    .load(committee.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.sivImage);
        }

        holder.tvName.setText(committee.getName());
    }

    @Override
    public int getItemCount() {
        return committees.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ShapeableImageView sivImage;
        private TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sivImage = itemView.findViewById(R.id.sivCommitteeImage);
            tvName = itemView.findViewById(R.id.tvCommitteeName);
        }
    }
}
