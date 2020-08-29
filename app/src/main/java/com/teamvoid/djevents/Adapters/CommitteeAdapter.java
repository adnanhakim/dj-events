package com.teamvoid.djevents.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.teamvoid.djevents.Activities.CommentActivity;
import com.teamvoid.djevents.Activities.CommitteeActivity;
import com.teamvoid.djevents.Models.Committee;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.util.List;

public class CommitteeAdapter extends RecyclerView.Adapter<CommitteeAdapter.ViewHolder> {

    private Context context;
    private List<Committee> committees;

    public CommitteeAdapter(Context context, List<Committee> committees) {
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

        holder.constraintLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, CommitteeActivity.class);
            intent.putExtra(Constants.COMMITTEE_ID, committee.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return committees.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout constraintLayout;
        private ShapeableImageView sivImage;
        private TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintItemCommittee);
            sivImage = itemView.findViewById(R.id.sivItemCommitteeImage);
            tvName = itemView.findViewById(R.id.tvItemCommitteeName);
        }
    }
}
