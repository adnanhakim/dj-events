package com.teamvoid.djevents.Activities;

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

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "AddEventActivity";

    // Elements
    private ImageButton ibBack, ibEventDate, ibRegDate;
    private TextInputLayout tilName, tilDescription, tilEventDate, tilEligibility, tilPrice, tilRegLink, tilRegDate;
    private MaterialSpinner msStatus;
    private ShapeableImageView sivImage;
    private Button btnAddEvent;
    private ProgressBar progressBar;

    // Variables
    private String committeeId;
    private Uri photoUri;
    private String photoPath;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Data Binding
        init();

        Intent callingIntent = getIntent();
        photoPath = callingIntent.getStringExtra(Constants.PHOTO_PATH);
        if (photoPath != null) {
            photoUri = Uri.fromFile(new File(photoPath));
            Log.d(TAG, "onCreate: Uri: " + photoUri);

            sivImage.setImageURI(photoUri);
        } else {
            Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnAddEvent.setOnClickListener(view -> {
            if (!validateName() | !validateDescription() | !validateEligibility() | !validatePrice())
                return;

            uploadImage();
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarAddEvent);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibAddEventBack);
        tilName = findViewById(R.id.tilAddEventName);
        tilDescription = findViewById(R.id.tilAddEventDescription);
        tilEventDate = findViewById(R.id.tilAddEventDate);
        ibEventDate = findViewById(R.id.ibAddEventDate);
        tilEligibility = findViewById(R.id.tilAddEventEligibility);
        tilPrice = findViewById(R.id.tilAddEventPrice);
        tilRegLink = findViewById(R.id.tilAddEventLink);
        tilRegDate = findViewById(R.id.tilAddEventRegistrationDate);
        ibRegDate = findViewById(R.id.ibAddEventRegistrationDate);
        msStatus = findViewById(R.id.msAddEventStatus);
        sivImage = findViewById(R.id.sivAddEventImage);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        progressBar = findViewById(R.id.progressAddPost);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.EVENTS);
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

    private boolean validateName() {
        String name = Objects.requireNonNull(tilName.getEditText()).getText().toString().trim();
        if (name.isEmpty()) {
            tilName.setError("Required");
            return false;
        } else {
            tilName.setError(null);
            return true;
        }
    }

    private boolean validateDescription() {
        String description = Objects.requireNonNull(tilDescription.getEditText()).getText().toString().trim();
        if (description.isEmpty()) {
            tilDescription.setError("Required");
            return false;
        } else {
            tilDescription.setError(null);
            return true;
        }
    }

    private boolean validateEligibility() {
        String eligibility = Objects.requireNonNull(tilEligibility.getEditText()).getText().toString().trim();
        if (eligibility.isEmpty()) {
            tilEligibility.setError("Required");
            return false;
        } else {
            tilEligibility.setError(null);
            return true;
        }
    }

    private boolean validatePrice() {
        String price = Objects.requireNonNull(tilPrice.getEditText()).getText().toString().trim();
        if (price.isEmpty()) {
            tilPrice.setError("Required");
            return false;
        } else {
            tilPrice.setError(null);
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
                                            saveEvent(downloadUrl);
                                        } else {
                                            stopProgressBar();
                                            Log.d(TAG, "onComplete: Photo upload failed: " + Objects.requireNonNull(task1.getException()).getStackTrace().toString());
                                            Toast.makeText(AddEventActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            stopProgressBar();
                            Log.d(TAG, "onComplete: Photo upload failed: " + Objects.requireNonNull(task.getException()).getStackTrace().toString());
                            Toast.makeText(AddEventActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                            task.getException().printStackTrace();
                        }
                    });
        }
    }

    private void saveEvent(String imageUrl) {
        startProgressBar();

//        String caption = Objects.requireNonNull(tilCaption.getEditText()).getText().toString().trim();
//        SharedPref sharedPref = new SharedPref(this);
//
//        Map<String, Object> post = new HashMap<>();
//        post.put(Constants.COMMITTEE_ID, committeeId);
//        post.put(Constants.NAME, sharedPref.getCommitteeName());
//        post.put(Constants.DP_URL, sharedPref.getCommitteeDpUrl());
//        post.put(Constants.TIMESTAMP, new Timestamp(new Date()));
//        post.put(Constants.CAPTION, caption);
//        post.put(Constants.IMAGE_URL, imageUrl);
//        post.put(Constants.LIKES, new ArrayList<>());
//
//        db.collection(Constants.EVENTS)
//                .add(post)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        stopProgressBar();
//                        Log.d(TAG, "onComplete: Post successful");
//                        Toast.makeText(AddPostActivity.this, "Post successful", Toast.LENGTH_SHORT).show();
//                        AddPostActivity.this.finish();
//                    } else {
//                        stopProgressBar();
//                        Log.d(TAG, "onComplete: Post Failed: " + Objects.requireNonNull(task.getException()).getMessage());
//                        Toast.makeText(AddPostActivity.this, "Post Failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        AddEventActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
        AddEventActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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