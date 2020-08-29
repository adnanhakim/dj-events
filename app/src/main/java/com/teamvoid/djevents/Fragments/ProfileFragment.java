package com.teamvoid.djevents.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.teamvoid.djevents.Adapters.ViewPagerAdapter;
import com.teamvoid.djevents.Models.Committee;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // Elements
    private View view;
    private ShapeableImageView sivDp;
    private TextView tvName, tvDepartment, tvBio, tvFollowers, tvPosts, tvEvents;
    private Button btnPosts, btnEvents, btnMembers;
    private ViewPager2 viewPager;

    // Firebase
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Data Binding
        init();

        setUpFragments();

        fetchCommitteeDetails();

        return view;
    }

    private void init() {
        sivDp = view.findViewById(R.id.sivProfileDp);
        tvName = view.findViewById(R.id.tvProfileName);
        tvDepartment = view.findViewById(R.id.tvProfileDepartment);
        tvBio = view.findViewById(R.id.tvProfileBio);
        tvFollowers = view.findViewById(R.id.tvProfileFollowers);
        tvPosts = view.findViewById(R.id.tvProfilePosts);
        tvEvents = view.findViewById(R.id.tvProfileEvents);

        btnPosts = view.findViewById(R.id.btnProfilePostsTab);
        btnEvents = view.findViewById(R.id.btnProfileEventsTab);
        btnMembers = view.findViewById(R.id.btnProfileMembersTab);
        viewPager = view.findViewById(R.id.viewpagerProfile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    private void setUpFragments() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), getLifecycle());

        adapter.addFragment(new ProfilePostsFragment(firebaseUser.getUid()));
        adapter.addFragment(new ProfileEventsFragment(firebaseUser.getUid()));
        adapter.addFragment(new ProfileMembersFragment(firebaseUser.getUid()));

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
        btnPosts.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.textColor));
        btnEvents.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.lightTextColor));
        btnMembers.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.lightTextColor));
    }

    private void selectEvents() {
        btnPosts.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.lightTextColor));
        btnEvents.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.textColor));
        btnMembers.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.lightTextColor));
    }

    private void selectMembers() {
        btnPosts.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.lightTextColor));
        btnEvents.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.lightTextColor));
        btnMembers.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.textColor));
    }

    private void fetchCommitteeDetails() {
        db.collection(Constants.COMMITTEES)
                .document(firebaseUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        Committee committee = document.toObject(Committee.class);
                        if (committee != null) {
                            committee.setId(document.getId());
                            setData(committee);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete: Failed: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private void setData(Committee committee) {
        Log.d(TAG, "displayData: Image Url: " + committee.getImageUrl());
        if (committee.getImageUrl() != null) {
            Picasso.get()
                    .load(committee.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(sivDp);
        }

        tvName.setText(committee.getName());
        tvDepartment.setText(committee.getDepartment());
        tvBio.setText(committee.getBio());
        tvFollowers.setText(String.valueOf(committee.getFollowers()));
        tvPosts.setText(String.valueOf(committee.getPosts()));
        tvEvents.setText(String.valueOf(committee.getEvents()));
    }
}
