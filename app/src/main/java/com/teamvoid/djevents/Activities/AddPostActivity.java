package com.teamvoid.djevents.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.io.File;
import java.util.Objects;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";

    // Elements
    private ImageButton ibBack;
    private ImageView ivImage;
    private Button btnPost;
    private ProgressBar progressBar;

    // Variables
    private Uri photoUri;
    private String photoPath;

    // Firebase
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // Data Binding
        init();

        Intent callingIntent = getIntent();
        if (callingIntent.hasExtra(Constants.PHOTO_PATH)) {
            photoPath = callingIntent.getStringExtra(Constants.PHOTO_PATH);
            photoUri = Uri.fromFile(new File(photoPath));
            Log.d(TAG, "onCreate: Uri: " + photoUri);

            ivImage.setImageURI(photoUri);
        }

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnPost.setOnClickListener(view -> {
            uploadImage();
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarAddPost);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibAddPostBack);
        ivImage = findViewById(R.id.ivAddPostImage);
        btnPost = findViewById(R.id.btnAddPost);
        progressBar = findViewById(R.id.progressAddPost);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://dj-events-9d44b.appspot.com/posts");
    }

    private void uploadImage() {
        if (photoUri != null) {
            String extension = photoPath.substring(photoPath.lastIndexOf("."));
            String fileName = System.currentTimeMillis() + extension;
            Log.d(TAG, "uploadFile: File Name: " + fileName);
            StorageReference fileReference = storageReference.child(fileName);
            fileReference.putFile(photoUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Photo upload successful");
                            Toast.makeText(AddPostActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            fileReference.getDownloadUrl()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null) {
                                            String downloadUrl = task1.getResult().toString();
                                            savePost(downloadUrl);
                                        } else {
                                            Log.d(TAG, "onComplete: Photo upload failed: " + Objects.requireNonNull(task1.getException()).getStackTrace().toString());
                                            Toast.makeText(AddPostActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.d(TAG, "onComplete: Photo upload failed: " + Objects.requireNonNull(task.getException()).getStackTrace().toString());
                            Toast.makeText(AddPostActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                            Exception e = task.getException();
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void savePost(String imageUrl) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}