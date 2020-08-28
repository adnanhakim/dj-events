package com.teamvoid.djevents.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";

    // Elements
    private ImageButton ibBack;
    private TextInputLayout tilCaption;
    private ShapeableImageView sivImage;
    private Button btnAddPost;
    private ProgressBar progressBar;

    // Variables
    private String committeeId;
    private boolean image = false;
    private Uri photoUri;
    private String photoPath;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // Data Binding
        init();

        Intent callingIntent = getIntent();
        photoPath = callingIntent.getStringExtra(Constants.PHOTO_PATH);
        if (photoPath != null) {
            photoUri = Uri.fromFile(new File(photoPath));
            Log.d(TAG, "onCreate: Uri: " + photoUri);

            sivImage.setImageURI(photoUri);
            image = true;
        } else {
            sivImage.setVisibility(View.GONE);
            image = false;
        }

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnAddPost.setOnClickListener(view -> {
            if (!validateCaption())
                return;

            if (image)
                uploadImage();
            else savePost("");
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarAddPost);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibAddPostBack);
        tilCaption = findViewById(R.id.tilAddPostCaption);
        sivImage = findViewById(R.id.sivAddPostImage);
        btnAddPost = findViewById(R.id.btnAddPost);
        progressBar = findViewById(R.id.progressAddPost);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.POSTS);
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            new SharedPref(this).removeData();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else committeeId = user.getUid();
    }

    private boolean validateCaption() {
        String caption = Objects.requireNonNull(tilCaption.getEditText()).getText().toString().trim();
        if (caption.isEmpty()) {
            tilCaption.setError("Required");
            return false;
        } else {
            tilCaption.setError(null);
            return true;
        }
    }

    private void uploadImage() {
        if (photoUri != null) {
            startProgressBar();

            String extension = photoPath.substring(photoPath.lastIndexOf("."));
            String fileName = System.currentTimeMillis() + extension;
            Log.d(TAG, "uploadFile: File Name: " + fileName);
            StorageReference fileReference = storageReference.child(fileName);
            fileReference.putFile(photoUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Photo upload successful");
                            fileReference.getDownloadUrl()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null) {
                                            String downloadUrl = task1.getResult().toString();
                                            savePost(downloadUrl);
                                        } else {
                                            stopProgressBar();
                                            Log.d(TAG, "onComplete: Photo upload failed: " + Objects.requireNonNull(task1.getException()).getStackTrace().toString());
                                            Toast.makeText(AddPostActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            stopProgressBar();
                            Log.d(TAG, "onComplete: Photo upload failed: " + Objects.requireNonNull(task.getException()).getStackTrace().toString());
                            Toast.makeText(AddPostActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                            task.getException().printStackTrace();
                        }
                    });
        }
    }

    private void savePost(String imageUrl) {
        startProgressBar();

        String caption = Objects.requireNonNull(tilCaption.getEditText()).getText().toString().trim();
        SharedPref sharedPref = new SharedPref(this);

        Map<String, Object> post = new HashMap<>();
        post.put(Constants.COMMITTEE_ID, committeeId);
        post.put(Constants.COMMITTEE_NAME, sharedPref.getCommitteeName());
        post.put(Constants.DP_URL, sharedPref.getCommitteeDpUrl());
        post.put(Constants.TIMESTAMP, new Timestamp(new Date()));
        post.put(Constants.CAPTION, caption);
        post.put(Constants.IMAGE_URL, imageUrl);
        post.put(Constants.LIKES, new ArrayList<>());

        db.collection(Constants.POSTS)
                .add(post)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updatePostCount();
                    } else {
                        stopProgressBar();
                        Log.d(TAG, "onComplete: Post Failed: " + Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(AddPostActivity.this, "Post Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePostCount() {
        db.collection(Constants.COMMITTEES)
                .document(Objects.requireNonNull(firebaseAuth.getUid()))
                .update(Constants.POSTS, FieldValue.increment(1))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        stopProgressBar();
                        Log.d(TAG, "onComplete: Post successful");
                        Toast.makeText(AddPostActivity.this, "Post successful", Toast.LENGTH_SHORT).show();
                        AddPostActivity.this.finish();
                    } else {
                        stopProgressBar();
                        Log.d(TAG, "onComplete: Post Failed: " + Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(AddPostActivity.this, "Post Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        AddPostActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
        AddPostActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}