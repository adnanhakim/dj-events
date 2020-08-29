package com.teamvoid.djevents.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.teamvoid.djevents.Adapters.ViewPagerAdapter;
import com.teamvoid.djevents.Fragments.ProfileEventsFragment;
import com.teamvoid.djevents.Fragments.ProfileMembersFragment;
import com.teamvoid.djevents.Fragments.ProfilePostsFragment;
import com.teamvoid.djevents.Models.Committee;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import java.util.Map;
import java.util.Objects;

public class CommitteeActivity extends AppCompatActivity {

    private static final String TAG = "CommitteeActivity";

    // Elements
    private ImageButton ibBack, ibNotification;
    private TextView tvHeader, tvName, tvDepartment, tvBio, tvFollowers, tvPosts, tvEvents;
    private ShapeableImageView sivImage;
    private Button btnPosts, btnEvents, btnMembers;
    private ViewPager2 viewPager;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_committee);

        // Data Binding
        init();

        setUpFragments();

        Intent callingIntent = getIntent();
        String committeeId = callingIntent.getStringExtra(Constants.COMMITTEE_ID);
        if (committeeId == null) {
            Toast.makeText(this, "No committee id", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        fetchCommittee(committeeId);

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarCommittee);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibCommitteeBack);
        tvHeader = findViewById(R.id.tvCommitteeHeader);
        ibNotification = findViewById(R.id.ibCommitteeNotification);
        sivImage = findViewById(R.id.sivCommitteeDp);
        tvName = findViewById(R.id.tvCommitteeName);
        tvDepartment = findViewById(R.id.tvCommitteeDepartment);
        tvBio = findViewById(R.id.tvCommitteeBio);
        tvFollowers = findViewById(R.id.tvCommitteeFollowers);
        tvPosts = findViewById(R.id.tvCommitteePosts);
        tvEvents = findViewById(R.id.tvCommitteeEvents);

        btnPosts = findViewById(R.id.btnCommitteePostsTab);
        btnEvents = findViewById(R.id.btnCommitteeEventsTab);
        btnMembers = findViewById(R.id.btnCommitteeMembersTab);
        viewPager = findViewById(R.id.viewpagerCommittee);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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

    private void setUpFragments() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());

        adapter.addFragment(new ProfilePostsFragment());
        adapter.addFragment(new ProfileEventsFragment());
        adapter.addFragment(new ProfileMembersFragment());

        viewPager.setAdapter(adapter);
        selectPosts();
        viewPager.setCurrentItem(0);
        setUpTabs();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    selectPosts();
                else if (position == 1)
                    selectEvents();
                else selectMembers();
            }
        });
    }

    private void setUpTabs() {
        btnPosts.setOnClickListener(v -> {
            selectPosts();
            viewPager.setCurrentItem(0);
        });

        btnEvents.setOnClickListener(v -> {
            selectEvents();
            viewPager.setCurrentItem(1);
        });

        btnMembers.setOnClickListener(v -> {
            selectMembers();
            viewPager.setCurrentItem(2);
        });
    }

    private void selectPosts() {
        btnPosts.setTextColor(ContextCompat.getColor(this, R.color.textColor));
        btnEvents.setTextColor(ContextCompat.getColor(this, R.color.lightTextColor));
        btnMembers.setTextColor(ContextCompat.getColor(this, R.color.lightTextColor));
    }

    private void selectEvents() {
        btnPosts.setTextColor(ContextCompat.getColor(this, R.color.lightTextColor));
        btnEvents.setTextColor(ContextCompat.getColor(this, R.color.textColor));
        btnMembers.setTextColor(ContextCompat.getColor(this, R.color.lightTextColor));
    }

    private void selectMembers() {
        btnPosts.setTextColor(ContextCompat.getColor(this, R.color.lightTextColor));
        btnEvents.setTextColor(ContextCompat.getColor(this, R.color.lightTextColor));
        btnMembers.setTextColor(ContextCompat.getColor(this, R.color.textColor));
    }

    private void fetchCommittee(String committeeId) {
        Log.d(TAG, "fetchCommittee: Fetching committee...");

        db.collection(Constants.COMMITTEES)
                .document(committeeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            Committee committee = document.toObject(Committee.class);
                            if (committee != null) {
                                committee.setId(document.getId());
                                setData(committee);
                            }
                        } else {
                            Log.d(TAG, "onComplete: Committee failed: " + Objects.requireNonNull(task.getException()).getMessage());
                            Toast.makeText(CommitteeActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setData(Committee committee) {
        Log.d(TAG, "setData: Image Url: " + committee.getImageUrl());
        if (committee.getImageUrl() != null) {
            Picasso.get()
                    .load(committee.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(sivImage);
        }

        tvHeader.setText(committee.getName());
        tvName.setText(committee.getName());
        tvDepartment.setText(committee.getDepartment());
        tvBio.setText(committee.getBio());
        tvFollowers.setText(String.valueOf(committee.getFollowers()));
        tvPosts.setText(String.valueOf(committee.getPosts()));
        tvEvents.setText(String.valueOf(committee.getEvents()));
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