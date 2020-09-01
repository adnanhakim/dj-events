package com.teamvoid.djevents.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.FileUtils;

public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "ImageActivity";

    // Elements
    private ImageButton ibBack;
    private ImageView ivImage;
    private Button btnSaveImage;

    // Variables
    private boolean image, isPost, isEvent, isEdit;
    private final int REQUEST_CODE = 123, PICK_IMAGE = 124;
    private Uri photoUri;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // Data Binding
        init();
        checkStoragePermission();

        Intent callingIntent = getIntent();
        if (callingIntent.hasExtra(Constants.POSTS))
            isPost = true;
        else if (callingIntent.hasExtra(Constants.EVENTS))
            isEvent = true;
        else if (callingIntent.hasExtra(Constants.EDIT_PROFILE))
            isEdit = true;
        else Toast.makeText(this, "Must define calling intent", Toast.LENGTH_SHORT).show();

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnSaveImage.setOnClickListener(view -> {
            if (image && photoUri != null) {
                if (isEdit) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(Constants.PHOTO_PATH, photoPath);
                    setResult(RESULT_OK, resultIntent);
                    this.finish();
                }

                Intent intent;
                if (isPost)
                    intent = new Intent(ImageActivity.this, AddPostActivity.class);
                else if (isEvent)
                    intent = new Intent(ImageActivity.this, AddEventActivity.class);
                else return;
                intent.putExtra(Constants.PHOTO_PATH, photoPath);
                startActivity(intent);
                this.finish();
            } else Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarImage);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibImageBack);
        ivImage = findViewById(R.id.ivImage);
        btnSaveImage = findViewById(R.id.btnImageSave);
    }

    private void checkStoragePermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else pickImageFromGallery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery();
        } else this.finish();
    }

    private void pickImageFromGallery() {
        Log.d(TAG, "pickImageFromGallery: Getting photo...");
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select image"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d(TAG, "onActivityResult: Got photo...");
            FileUtils fileUtils = new FileUtils(this);
            photoUri = data.getData();
            photoPath = fileUtils.getPath(photoUri);
            Log.d(TAG, "onActivityResult: Uri: " + photoUri);
            Log.d(TAG, "onActivityResult: Path: " + photoPath);

            // Set image
            ivImage.setImageURI(photoUri);
            image = true;
        } else {
            Log.d(TAG, "onActivityResult: Could not get photo");
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isEdit)
            setResult(RESULT_CANCELED, new Intent());
        this.finish();
    }
}