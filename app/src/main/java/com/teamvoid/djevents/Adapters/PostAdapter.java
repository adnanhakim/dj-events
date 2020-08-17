package com.teamvoid.djevents.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.teamvoid.djevents.Models.Post;
import com.teamvoid.djevents.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> postList;
    private Context context;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ShapeableImageView ivDisplayPicture;
        public TextView tvUsername, tvAgoTime;
        public ImageView ivPostImage;
        public RelativeLayout rlLikeBtn, rlCommentBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivDisplayPicture = itemView.findViewById(R.id.ivDisplayPicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvAgoTime = itemView.findViewById(R.id.tvAgoTime);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            rlLikeBtn = itemView.findViewById(R.id.rlLikeBtn);
            rlCommentBtn = itemView.findViewById(R.id.rlCommentBtn);
        }
    }
}
