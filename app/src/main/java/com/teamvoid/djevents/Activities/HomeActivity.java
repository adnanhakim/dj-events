package com.teamvoid.djevents.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.teamvoid.djevents.R;

public class HomeActivity extends AppCompatActivity {
    private Button btnLogout;

    // Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();

        btnLogout.setOnClickListener(view -> {
            firebaseAuth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void init() {
        btnLogout = findViewById(R.id.btnLogout);

        firebaseAuth = FirebaseAuth.getInstance();
    }
}