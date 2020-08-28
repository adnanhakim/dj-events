package com.teamvoid.djevents.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamvoid.djevents.Models.Comment;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Methods;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.tvName.setText(comment.getName());
        holder.tvTime.setText(Methods.formatTimestamp(comment.getTimestamp().toDate()));
        holder.tvComment.setText(comment.getComment());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvComment, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvItemCommentName);
            tvTime = itemView.findViewById(R.id.tvItemCommentTime);
            tvComment = itemView.findViewById(R.id.tvItemComment);
        }
    }
}
