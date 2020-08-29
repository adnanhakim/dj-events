package com.teamvoid.djevents.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.teamvoid.djevents.Fragments.AdminFragment;
import com.teamvoid.djevents.Fragments.EventsFragment;
import com.teamvoid.djevents.Fragments.HomeFragment;
import com.teamvoid.djevents.Fragments.ProfileFragment;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Elements
    private ChipNavigationBar chipNavigationBar;
    private ImageButton ibLogout;

    // Fragments
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment eventsFragment = new EventsFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment adminFragment = new AdminFragment();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active = homeFragment;

    // Variables
    private SharedPref sharedPref;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseInstanceId firebaseInstanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Data Binding
        init();
        getFCMToken();

        chipNavigationBar.setItemSelected(R.id.bottomNavHome, true);
        fragmentManager.beginTransaction().add(R.id.frameMain, adminFragment, "4").hide(adminFragment).commit();
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
                case R.id.bottomNavAdmin:
                    fragmentManager.beginTransaction().hide(active).show(adminFragment).commit();
                    active = adminFragment;
                    break;
            }
        });

        // Clicks
        ibLogout.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyle);
            builder.setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        firebaseAuth.signOut();
                        new SharedPref(MainActivity.this).removeData();
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
        db = FirebaseFirestore.getInstance();
        firebaseInstanceId = FirebaseInstanceId.getInstance();
        sharedPref = new SharedPref(this);
    }

    private void getFCMToken() {
        firebaseInstanceId.getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult().getToken();
                        Log.d(TAG, "onComplete: FCM Token: " + token);
                        saveFCMToken(token);
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to get instance", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete: Failed to get instance: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private void saveFCMToken(String token) {
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        if (sharedPref.isCommittee()) {
            // Committee
            db.collection(Constants.COMMITTEES)
                    .document(userId)
                    .update(Constants.TOKEN, token)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Saved token");
                            subscribeToTopics();
                        } else {
                            Log.d(TAG, "onComplete: Failed to save token: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        } else {
            // User
            db.collection(Constants.USERS)
                    .document(userId)
                    .update(Constants.TOKEN, token)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Saved token");
                            subscribeToTopics();
                        } else {
                            Log.d(TAG, "onComplete: Failed to save token: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        }
    }

    private void subscribeToTopics() {
        List<String> topics = sharedPref.getTopics();

        for (String topic : topics) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
        }
    }
}