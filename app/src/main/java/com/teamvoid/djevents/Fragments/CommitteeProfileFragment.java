package com.teamvoid.djevents.Fragments;

import android.content.Intent;
import android.net.Uri;
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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.teamvoid.djevents.Adapters.ViewPagerAdapter;
import com.teamvoid.djevents.Models.Committee;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.util.Objects;

public class CommitteeProfileFragment extends Fragment {

    private static final String TAG = "CommitteeProfileFragmen";

    // Elements
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView nestedScrollView;
    private ShapeableImageView sivDp;
    private TextView tvName, tvDepartment, tvBio, tvLink, tvFollowers, tvPosts, tvEvents;
    private Button btnPosts, btnEvents, btnMembers;
    private ViewPager2 viewPager;

    // Firebase
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_committee_profile, container, false);

        // Data Binding
        init();

        setUpFragments();

        fetchCommitteeDetails();

        swipeRefreshLayout.setOnRefreshListener(this::fetchCommitteeDetails);

        tvLink.setOnClickListener(view -> {
            String link = tvLink.getText().toString().trim();
            if (link.equals(""))
                return;

            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                Toast.makeText(getActivity(), "Improper link", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        nestedScrollView.fullScroll(View.FOCUS_UP);
        nestedScrollView.scrollTo(0,0);
    }

    private void init() {
        swipeRefreshLayout = view.findViewById(R.id.srlCommitteeProfile);
        nestedScrollView = view.findViewById(R.id.nsvCommitteeProfile);
        sivDp = view.findViewById(R.id.sivCommitteeProfileDp);
        tvName = view.findViewById(R.id.tvCommitteeProfileName);
        tvDepartment = view.findViewById(R.id.tvCommitteeProfileDepartment);
        tvBio = view.findViewById(R.id.tvCommitteeProfileBio);
        tvLink = view.findViewById(R.id.tvCommitteeProfileLink);
        tvFollowers = view.findViewById(R.id.tvCommitteeProfileFollowers);
        tvPosts = view.findViewById(R.id.tvCommitteeProfilePosts);
        tvEvents = view.findViewById(R.id.tvCommitteeProfileEvents);

        btnPosts = view.findViewById(R.id.btnCommitteeProfilePostsTab);
        btnEvents = view.findViewById(R.id.btnCommitteeProfileEventsTab);
        btnMembers = view.findViewById(R.id.btnCommitteeProfileMembersTab);
        viewPager = view.findViewById(R.id.viewpagerCommitteeProfile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
        swipeRefreshLayout.setRefreshing(true);
        db.collection(Constants.COMMITTEES)
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Committee committee = documentSnapshot.toObject(Committee.class);
                    if (committee != null) {
                        committee.setId(documentSnapshot.getId());
                        setData(committee);
                    }
                })
                .addOnFailureListener(e -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure: Failed: " + e.getMessage());
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
        tvLink.setText(committee.getLink());
        tvFollowers.setText(String.valueOf(committee.getFollowers()));
        tvPosts.setText(String.valueOf(committee.getPosts()));
        tvEvents.setText(String.valueOf(committee.getEvents()));

        swipeRefreshLayout.setRefreshing(false);
    }
}
