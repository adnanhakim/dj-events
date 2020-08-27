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
import com.squareup.picasso.Picasso;
import com.teamvoid.djevents.Models.Post;
import com.teamvoid.djevents.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.tvUsername.setText(post.getName());
//        holder.tvTime.setText(post.getTimestamp().toString());
        holder.tvCaption.setText(post.getCaption());
        holder.tvLikes.setText(post.getLikes().size() + " likes");
        holder.tvComments.setText("0 comments");

        if (post.getDpUrl() != null && !post.getDpUrl().equals("")) {
            Picasso.get()
                    .load(post.getDpUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.sivDp);
        }

        if (post.getImageUrl() != null && !post.getImageUrl().equals("")) {
            Picasso.get()
                    .load(post.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.sivPostImage);
        } else holder.sivPostImage.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView sivDp, sivPostImage;
        private TextView tvUsername, tvTime, tvCaption, tvLikes, tvComments;
        private ImageView ivLike, ivComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sivDp = itemView.findViewById(R.id.sivPostDp);
            tvUsername = itemView.findViewById(R.id.tvPostUsername);
            tvTime = itemView.findViewById(R.id.tvPostTime);
            sivPostImage = itemView.findViewById(R.id.sivPostImage);
            tvCaption = itemView.findViewById(R.id.tvPostCaption);
            ivLike = itemView.findViewById(R.id.ivPostLike);
            tvLikes = itemView.findViewById(R.id.tvPostLikes);
            ivComment = itemView.findViewById(R.id.ivPostComment);
            tvComments = itemView.findViewById(R.id.tvPostComments);
        }
    }
}
