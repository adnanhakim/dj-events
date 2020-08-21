package com.teamvoid.djevents.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.teamvoid.djevents.R;

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
