package com.teamvoid.djevents.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";

    // Elements
    private ImageButton ibBack;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // Data Binding
        init();

        Intent callingIntent = getIntent();
        if (callingIntent.hasExtra(Constants.PHOTO_PATH)) {
            String photoPath = callingIntent.getStringExtra(Constants.PHOTO_PATH);
            Uri photoUri = Uri.parse(photoPath);
            Log.d(TAG, "onCreate: Uri: " + photoUri);

            ivImage.setImageURI(photoUri);
        }

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarAddPost);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibAddPostBack);
        ivImage = findViewById(R.id.ivAddPostImage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}