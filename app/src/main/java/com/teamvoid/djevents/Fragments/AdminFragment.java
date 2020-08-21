package com.teamvoid.djevents.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.teamvoid.djevents.Activities.AddEventActivity;
import com.teamvoid.djevents.Activities.AddMemberActivity;
import com.teamvoid.djevents.Activities.AddPostActivity;
import com.teamvoid.djevents.Activities.EditProfileActivity;
import com.teamvoid.djevents.R;

import java.util.Objects;

public class AdminFragment extends Fragment {

    // Elements
    private View view;
    private CardView cvEditProfile, cvAddMember, cvAddPost, cvAddEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin, container, false);

        // Data Binding
        init();

        // Clicks
        cvEditProfile.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });

        cvAddMember.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddMemberActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });

        cvAddPost.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddPostActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });

        cvAddEvent.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });

        return view;
    }

    private void init() {
        cvEditProfile = view.findViewById(R.id.cardAdminEditProfile);
        cvAddMember = view.findViewById(R.id.cardAdminAddMember);
        cvAddPost = view.findViewById(R.id.cardAdminAddPost);
        cvAddEvent = view.findViewById(R.id.cardAdminAddEvent);
    }
}
