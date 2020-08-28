package com.teamvoid.djevents.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.teamvoid.djevents.Adapters.CommentAdapter;
import com.teamvoid.djevents.Models.Comment;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.MarginItemDecorator;
import com.teamvoid.djevents.Utils.SharedPref;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";

    // Elements
    private ImageButton ibBack;
    private TextView tvHeader, tvCaption;
    private RecyclerView recyclerComments;
    private EditText etComment;
    private Button btnPost;

    // Firebase
    private FirebaseFirestore db;

    // Variables
    private String postId;
    private List<Comment> comments;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Data Binding
        init();

        Intent callingIntent = getIntent();
        postId = callingIntent.getStringExtra(Constants.ID);
        String postName = callingIntent.getStringExtra(Constants.NAME);
        String postCaption = callingIntent.getStringExtra(Constants.CAPTION);

        if (postId == null || postName == null || postCaption == null) {
            Toast.makeText(this, "Intents not provided", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        tvHeader.setText(postName);
        tvCaption.setText(postCaption);
        etComment.setHint("Comment as " + new SharedPref(this).getUserName() + "...");

        fetchComments();

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnPost.setOnClickListener(view -> {
            String commentString = etComment.getText().toString().trim();
            if (commentString.equals("")) return;

            if (adapter == null) return;

            Log.d(TAG, "onCreate: Posting comment...");
            Comment comment = new Comment(new SharedPref(this).getUserName(), commentString, new Timestamp(new Date()));
            db.collection(Constants.POSTS)
                    .document(postId)
                    .collection(Constants.COMMENTS)
                    .add(comment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onCreate: Comment Posted");
                            comments.add(comment);
                            adapter.notifyItemInserted(comments.size() - 1);
                            etComment.setText("");
                        } else {
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onCreate: Comment failed: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarComment);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibCommentBack);
        tvHeader = findViewById(R.id.tvCommentHeader);
        tvCaption = findViewById(R.id.tvCommentCaption);
        recyclerComments = findViewById(R.id.recyclerComments);
        etComment = findViewById(R.id.etComment);
        btnPost = findViewById(R.id.btnAddComment);

        db = FirebaseFirestore.getInstance();
        comments = new ArrayList<>();
    }

    private void fetchComments() {
        Log.d(TAG, "fetchComments: Fetching comments...");
        comments.clear();

        db.collection(Constants.POSTS)
                .document(postId)
                .collection(Constants.COMMENTS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "onComplete: Fetched comments");

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comment comment = document.toObject(Comment.class);
                            comments.add(comment);
                        }
                        setUpRecyclerView();
                    } else {
                        Log.d(TAG, "onComplete: Failed to fetch comments: " + Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(CommentActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUpRecyclerView() {
        adapter = new CommentAdapter(this, comments);
        recyclerComments.setAdapter(adapter);
        recyclerComments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerComments.addItemDecoration(new MarginItemDecorator(this, 16, 16, 16, 16));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}