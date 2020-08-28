package com.teamvoid.djevents.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamvoid.djevents.Adapters.CommentAdapter;
import com.teamvoid.djevents.Models.Comment;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
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
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    // Variables
    private String postId, postName, postCaption;
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
        postName = callingIntent.getStringExtra(Constants.NAME);
        postCaption = callingIntent.getStringExtra(Constants.CAPTION);

        if (postId == null || postName == null || postCaption == null) {
            Toast.makeText(this, "Intents not provided", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        tvHeader.setText(postName);
        tvCaption.setText(postCaption);

        fetchComments();

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnPost.setOnClickListener(view -> {
            String commentString = etComment.getText().toString().trim();
            if (commentString.equals("")) return;

            Log.d(TAG, "onCreate: Posting comment...");
            Comment comment = new Comment(new SharedPref(this).getUserName(), commentString, new Timestamp(new Date()));
            db.collection(Constants.POSTS)
                    .document(postId)
                    .collection(Constants.COMMENTS)
                    .add(comment)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
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
        tvHeader = findViewById(R.id.tvCommentHeader);
        tvCaption = findViewById(R.id.tvCommentCaption);
        recyclerComments = findViewById(R.id.recyclerComments);
        etComment = findViewById(R.id.etComment);
        btnPost = findViewById(R.id.btnAddComment);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        comments = new ArrayList<>();
    }

    private void fetchComments() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}