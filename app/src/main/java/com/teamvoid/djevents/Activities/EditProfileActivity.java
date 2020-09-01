package com.teamvoid.djevents.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.teamvoid.djevents.Models.Committee;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    // Elements
    private ImageButton ibBack;
    private TextInputLayout tilBio, tilLink;
    private Button btnChangeLogo, btnEditProfile;
    private ShapeableImageView sivLogo;
    private ProgressBar progressBar;

    // Variables
    private String imagePath;
    private Uri imageUri;
    private final int IMAGE_REQUEST_CODE = 123;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Data Binding
        init();

        fetchDetails();

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnChangeLogo.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, ImageActivity.class);
            intent.putExtra(Constants.EDIT_PROFILE, true);
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        });

        btnEditProfile.setOnClickListener(view -> editProfile());
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarEditProfile);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibEditProfileBack);
        tilBio = findViewById(R.id.tilEditProfileBio);
        tilLink = findViewById(R.id.tilEditProfileLink);
        btnChangeLogo = findViewById(R.id.btnEditProfileChangeLogo);
        sivLogo = findViewById(R.id.sivEditProfileLogo);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        progressBar = findViewById(R.id.progressEditProfile);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(Constants.DP);
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            new SharedPref(this).removeData();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void fetchDetails() {
        startProgressBar();
        db.collection(Constants.COMMITTEES)
                .document(Objects.requireNonNull(firebaseAuth.getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    stopProgressBar();
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "onComplete: Fetched details");
                        DocumentSnapshot document = task.getResult();
                        Committee committee = document.toObject(Committee.class);
                        if (committee != null) {
                            committee.setId(document.getId());
                            setDetails(committee);
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete: Failed: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private void setDetails(Committee committee) {
        Objects.requireNonNull(tilBio.getEditText()).setText(committee.getBio());
        Objects.requireNonNull(tilLink.getEditText()).setText(committee.getLink());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                imagePath = data.getStringExtra(Constants.PHOTO_PATH);
                if (imagePath != null) {
                    imageUri = Uri.fromFile(new File(imagePath));
                    sivLogo.setImageURI(imageUri);
                }
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void editProfile() {
        if (imagePath != null) {
            uploadImage();
        } else saveDetails();
    }

    private void uploadImage() {
        Uri photoUri = Uri.fromFile(new File(imagePath));
        if (photoUri != null) {
            startProgressBar();

            String extension = imagePath.substring(imagePath.lastIndexOf("."));
            String fileName = System.currentTimeMillis() + extension;
            Log.d(TAG, "uploadImage: File Name: " + fileName);
            StorageReference fileReference = storageReference.child(fileName);
            fileReference.putFile(photoUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Photo upload successful");
                            fileReference.getDownloadUrl()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null) {
                                            String downloadUrl = task1.getResult().toString();
                                            updateImageUrl(downloadUrl);
                                        } else {
                                            stopProgressBar();
                                            Log.d(TAG, "onComplete: Photo upload failed: " + Objects.requireNonNull(task1.getException()).getStackTrace().toString());
                                            Toast.makeText(EditProfileActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            stopProgressBar();
                            Log.d(TAG, "onComplete: Photo upload failed: " + Objects.requireNonNull(task.getException()).getStackTrace().toString());
                            Toast.makeText(EditProfileActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                            task.getException().printStackTrace();
                        }
                    });
        }
    }

    private void updateImageUrl(String imageUrl) {
        db.collection(Constants.COMMITTEES)
                .document(Objects.requireNonNull(firebaseAuth.getUid()))
                .update(Constants.IMAGE_URL, imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "updateImageUrl: Updated image url");
                        updatePosts(imageUrl);
                    } else {
                        Log.d(TAG, "updateImageUrl: Failed to update image url: " + Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(this, "Failed to update image url", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePosts(String imageUrl) {
        Log.d(TAG, "updatePosts: Fetching posts...");
        String userId = firebaseAuth.getUid();

        WriteBatch batch = db.batch();
        db.collection(Constants.POSTS)
                .whereEqualTo(Constants.COMMITTEE_ID, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "updatePosts: Fetched posts");
                        // Added all documents that need to be updated
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            DocumentReference reference = db.collection(Constants.POSTS).document(documentSnapshot.getId());
                            batch.update(reference, Constants.DP_URL, imageUrl);
                        }

                        // Update those documents
                        batch.commit()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "onComplete: Updated all posts");
                                        saveDetails();
                                    } else {
                                        stopProgressBar();
                                        Log.d(TAG, "onComplete: Failed: " + Objects.requireNonNull(task1.getException()).getMessage());
                                        Toast.makeText(EditProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        stopProgressBar();
                        Log.d(TAG, "updatePosts: Failed: " + Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveDetails() {
        startProgressBar();

        String bio = Objects.requireNonNull(tilBio.getEditText()).getText().toString().trim();
        String link = Objects.requireNonNull(tilLink.getEditText()).getText().toString().trim();

        Map<String, Object> map = new HashMap<>();
        map.put(Constants.BIO, bio);
        map.put(Constants.LINK, link);

        db.collection(Constants.COMMITTEES)
                .document(Objects.requireNonNull(firebaseAuth.getUid()))
                .update(map)
                .addOnCompleteListener(task -> {
                    stopProgressBar();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Profile Updated");
                        Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        EditProfileActivity.this.finish();
                    } else {
                        Log.d(TAG, "onComplete: Update failed");
                        Toast.makeText(EditProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        EditProfileActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
        EditProfileActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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