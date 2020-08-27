package com.teamvoid.djevents.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.teamvoid.djevents.Models.Post;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        holder.tvTime.setText(getTimeStatus(post.getTimestamp().toDate()));
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

            Picasso.get().load(post.getImageUrl()).into(holder.sivPostImage, new Callback() {
                @Override
                public void onSuccess() {
                    Picasso.get().load(post.getImageUrl()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();
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
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private String getTimeStatus(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN);
        return getTimeDiff(sfd.format(date), sfd.format(new Date()));

    }

    private String getTimeDiff(String sDate, String eDate) {
        SimpleDateFormat sfd = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN);

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sfd.parse(sDate);
            endDate = sfd.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert endDate != null;
        assert startDate != null;
        long duration = endDate.getTime() - startDate.getTime();

        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

        if (diffInMinutes < 1 && diffInDays == 0) return "Just now";

        if (diffInDays == 0 && diffInHours == 0) {
            if (diffInMinutes <= 1) return "Just now";
            else return diffInMinutes + " m";
        } else if (diffInDays == 0 && diffInHours >= 1 && diffInHours < 24) {
            return diffInHours + " h";
        } else if (diffInDays < 7) return diffInDays + " d";
        else if ((int) (diffInDays / 7) <= 24) return ((int) (diffInDays / 7)) + " w";
        else return sDate.substring(0, 10);
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
