package com.teamvoid.djevents.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // Elements
    private View view;
    private ShapeableImageView sivDp;
    private TextView tvName, tvDepartment, tvFollowers, tvPosts, tvEvents;

    // Tabs
    private Button btnPosts, btnEvents, btnMembers;
    private ImageView ivPosts, ivEvents, ivMembers;
    private ViewPager viewPager;

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

        fetchCommitteeDetails();

        return view;
    }

    private void init() {
        sivDp = view.findViewById(R.id.sivProfileDp);
        tvName = view.findViewById(R.id.tvProfileName);
        tvDepartment = view.findViewById(R.id.tvProfileDepartment);
        tvFollowers = view.findViewById(R.id.tvProfileFollowers);
        tvPosts = view.findViewById(R.id.tvProfilePosts);
        tvEvents = view.findViewById(R.id.tvProfileEvents);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    private void fetchCommitteeDetails() {
        db.collection(Constants.COMMITTEES)
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document != null && document.exists() && document.getData() != null) {
                        displayData(document.getData());
                    } else {
                        // Stop the progress bar
//                        progressBar.setVisibility(View.GONE);
//                        Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Log.d(TAG, "fetchData: Committee does not exist");
                        Toast.makeText(getContext(), "Committee does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "addMember: Failed to fetch: " + e.getMessage());
                });
    }

    private void displayData(Map<String, Object> data) {
        String name = (String) data.get(Constants.NAME);
        String department = (String) data.get(Constants.DEPARTMENT);
        String imageURL = (String) data.get(Constants.IMAGE_URL);
        Long followers = (Long) data.get(Constants.FOLLOWERS);
        Long posts = (Long) data.get(Constants.POSTS);
        Long events = (Long) data.get(Constants.EVENTS);

        if (imageURL != null) {
            Picasso.get()
                    .load(imageURL)
                    .centerCrop()
                    .into(sivDp);
        }

        tvName.setText(name);
        tvDepartment.setText(department);
        tvFollowers.setText(String.valueOf(followers));
        tvPosts.setText(String.valueOf(posts));
        tvEvents.setText(String.valueOf(events));
    }
}
