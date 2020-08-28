package com.teamvoid.djevents.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.teamvoid.djevents.Activities.CommentActivity;
import com.teamvoid.djevents.Models.Post;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.Methods;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private static final String TAG = "PostAdapter";

    private Context context;
    private List<Post> posts;

    // Firebase
    private FirebaseUser user;
    private FirebaseFirestore db;

    public PostAdapter(Context context, List<Post> posts) {
        this.posts = posts;
        this.context = context;
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
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
        holder.tvTime.setText(Methods.formatTimestamp(post.getTimestamp().toDate()));
        holder.tvCaption.setText(post.getCaption());
        holder.tvLikes.setText(post.getLikes().size() + " likes");

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
                    .into(holder.sivPostImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Picasso.get().load(post.getImageUrl()).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        } else holder.sivPostImage.setVisibility(View.GONE);

        if (post.getLikes().contains(user.getUid())) {
            // Already liked post
            holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like));
            post.setLiked(true);
        } else {
            // Post not liked yet
            holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unlike));
            post.setLiked(false);
        }

        holder.ivLike.setOnClickListener(view -> {
            if (post.isLiked()) {
                // Already liked post
                DocumentReference documentReference = db.collection(Constants.POSTS).document(post.getId());
                documentReference.update(Constants.LIKES, FieldValue.arrayRemove(user.getUid()))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unlike));
                                post.getLikes().remove(user.getUid());
                                holder.tvLikes.setText(post.getLikes().size() + " likes");
                                post.setLiked(false);
                            } else {
                                Log.d(TAG, "onComplete: Failed to unlike: " + Objects.requireNonNull(task.getException()).getMessage());
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Post not liked yet
                DocumentReference documentReference = db.collection(Constants.POSTS).document(post.getId());
                documentReference.update(Constants.LIKES, FieldValue.arrayUnion(user.getUid()))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like));
                                post.getLikes().add(user.getUid());
                                holder.tvLikes.setText(post.getLikes().size() + " likes");
                                post.setLiked(true);
                            } else {
                                Log.d(TAG, "onComplete: Failed to like: " + Objects.requireNonNull(task.getException()).getMessage());
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Opens comments in new activity
        holder.ivComment.setOnClickListener(view -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra(Constants.ID, post.getId());
            intent.putExtra(Constants.NAME, post.getName());
            intent.putExtra(Constants.CAPTION, post.getCaption());
            context.startActivity(intent);
        });

        // Opens comments in new activity
        holder.tvComments.setOnClickListener(view -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra(Constants.ID, post.getId());
            intent.putExtra(Constants.NAME, post.getName());
            intent.putExtra(Constants.CAPTION, post.getCaption());
            context.startActivity(intent);
        });
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

            sivDp = itemView.findViewById(R.id.sivItemPostDp);
            tvUsername = itemView.findViewById(R.id.tvItemPostUsername);
            tvTime = itemView.findViewById(R.id.tvItemPostTime);
            sivPostImage = itemView.findViewById(R.id.sivItemPostImage);
            tvCaption = itemView.findViewById(R.id.tvItemPostCaption);
            ivLike = itemView.findViewById(R.id.ivItemPostLike);
            tvLikes = itemView.findViewById(R.id.tvItemPostLikes);
            ivComment = itemView.findViewById(R.id.ivItemPostComment);
            tvComments = itemView.findViewById(R.id.tvItemPostComments);
        }
    }
}
