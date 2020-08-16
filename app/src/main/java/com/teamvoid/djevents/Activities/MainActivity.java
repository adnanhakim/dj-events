package com.teamvoid.djevents.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.teamvoid.djevents.Fragments.EventsFragment;
import com.teamvoid.djevents.Fragments.HomeFragment;
import com.teamvoid.djevents.Fragments.ProfileFragment;
import com.teamvoid.djevents.R;

public class MainActivity extends AppCompatActivity {

    // Elements
    private ChipNavigationBar chipNavigationBar;

    // Fragments
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment eventsFragment = new EventsFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active = homeFragment;

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
    }

    private void init() {
        chipNavigationBar = findViewById(R.id.cnbMain);
    }
}