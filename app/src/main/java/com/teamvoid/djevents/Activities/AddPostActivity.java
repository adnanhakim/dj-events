package com.teamvoid.djevents.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageButton;

import com.teamvoid.djevents.R;

public class AddPostActivity extends AppCompatActivity {

    // Elements
    private ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // Data Binding
        init();

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarAddPost);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibAddPostBack);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}