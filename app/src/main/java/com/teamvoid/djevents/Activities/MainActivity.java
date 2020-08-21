package com.teamvoid.djevents.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.teamvoid.djevents.Fragments.EventsFragment;
import com.teamvoid.djevents.Fragments.HomeFragment;
import com.teamvoid.djevents.Fragments.ProfileFragment;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.SharedPref;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Elements
    private ChipNavigationBar chipNavigationBar;
    private ImageButton ibLogout;

    // Fragments
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment eventsFragment = new EventsFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active = homeFragment;

    // Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Data Binding
        init();

        chipNavigationBar.setItemSelected(R.id.bottomNavHome, true);
        fragmentManager.beginTransaction().add(R.id.frameMain, profileFragment, "3").hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frameMain, eventsFragment, "2").hide(eventsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frameMain, homeFragment, "1").commit();

        chipNavigationBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case R.id.bottomNavHome:
                    fragmentManager.beginTransaction().hide(active).show(homeFragment).commit();
                    active = homeFragment;
                    break;
                case R.id.bottomNavEvents:
                    fragmentManager.beginTransaction().hide(active).show(eventsFragment).commit();
                    active = eventsFragment;
                    break;
                case R.id.bottomNavProfile:
                    fragmentManager.beginTransaction().hide(active).show(profileFragment).commit();
                    active = profileFragment;
                    break;
            }
        });

        // Clicks
        ibLogout.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        firebaseAuth.signOut();
                        new SharedPref(MainActivity.this).removeLoginData();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        MainActivity.this.finish();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        ibLogout = findViewById(R.id.ibMainLogout);
        chipNavigationBar = findViewById(R.id.cnbMain);

        firebaseAuth = FirebaseAuth.getInstance();
    }
}