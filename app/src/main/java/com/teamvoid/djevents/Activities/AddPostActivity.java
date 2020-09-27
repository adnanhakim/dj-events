package com.teamvoid.djevents.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.teamvoid.djevents.Models.NotificationResponse;
import com.teamvoid.djevents.Network.ApiRequest;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";

    // Elements
    private ImageButton ibBack;
    private TextInputLayout tilCaption;
    private ShapeableImageView sivImage;
    private Button btnAddPost;
    private ProgressBar progressBar;

    // Variables
    private String committeeId, photoPath;
    private boolean image = false;
    private Uri photoUri;
    private ApiRequest apiRequest;
    private SharedPref sharedPref;

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
        apiRequest = new ApiRequest();
        sharedPref = new SharedPref(this);
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
            Log.d(TAG, "uploadImage: File Name: " + fileName);
            StorageReference fileReference = storageReference.child(fileName);
            fileReference.putFile(photoUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "onComplete: Photo upload successful");
                        fileReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> savePost(uri.toString()))
                                .addOnFailureListener(e -> {
                                    stopProgressBar();
                                    Log.e(TAG, "onFailure: Photo upload failed: " + e.getMessage());
                                    Toast.makeText(AddPostActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                });
                    })
                    .addOnFailureListener(e -> {
                        stopProgressBar();
                        Log.e(TAG, "onFailure: Photo upload failed: " + e.getMessage());
                        Toast.makeText(AddPostActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
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
                .addOnSuccessListener(documentReference -> updatePostCount(documentReference.getId()))
                .addOnFailureListener(e -> {
                    stopProgressBar();
                    Log.e(TAG, "onFailure: Post Failed: " + e.getMessage());
                    Toast.makeText(AddPostActivity.this, "Post Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePostCount(String postId) {
        db.collection(Constants.COMMITTEES)
                .document(Objects.requireNonNull(firebaseAuth.getUid()))
                .update(Constants.POSTS, FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> sendPostNotification(postId))
                .addOnFailureListener(e -> {
                    stopProgressBar();
                    Log.e(TAG, "onFailure: Post Failed: " + e.getMessage());
                    Toast.makeText(AddPostActivity.this, "Post Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendPostNotification(String postId) {
        String title = sharedPref.getCommitteeName();
        String body = "Just added a new post";
        String topic = sharedPref.getCommitteeTopic();

        Log.d(TAG, "sendPostNotification: Title: " + title);
        Log.d(TAG, "sendPostNotification: Body: " + body);
        Log.d(TAG, "sendPostNotification: PostId: " + postId);
        Log.d(TAG, "sendPostNotification: Topic: " + topic);

        apiRequest.sendPostNotification(title, body, postId, topic, new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NotNull Call<NotificationResponse> call, @NotNull Response<NotificationResponse> response) {
                stopProgressBar();
                if (!response.isSuccessful() || response.body() == null) {
                    Log.d(TAG, "onResponse: Post notification not sent: " + response.code() + ": " + response.message());
                    Toast.makeText(AddPostActivity.this, "Post notification not sent", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "onResponse: Event added successfully");
                Toast.makeText(AddPostActivity.this, "Post successful", Toast.LENGTH_SHORT).show();
                AddPostActivity.this.finish();
            }

            @Override
            public void onFailure(@NotNull Call<NotificationResponse> call, @NotNull Throwable t) {
                stopProgressBar();
                Log.e(TAG, "onFailure: Failed: " + t.getMessage());
                Toast.makeText(AddPostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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