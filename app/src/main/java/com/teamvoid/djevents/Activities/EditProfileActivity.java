package com.teamvoid.djevents.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageButton;

import com.teamvoid.djevents.R;

public class EditProfileActivity extends AppCompatActivity {

    // Elements
    private ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Data Binding
        init();

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarEditProfile);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibEditProfileBack);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}