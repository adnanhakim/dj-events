package com.teamvoid.djevents.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.teamvoid.djevents.R;

public class ProfileEventsFragment extends Fragment {

    // Elements
    private View view;

    // Variables
    private String committeeId;

    public ProfileEventsFragment(String committeeId) {
        this.committeeId = committeeId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_events, container, false);

        // Data Binding
        init();

        return view;
    }

    private void init() {

    }
}
