package com.teamvoid.djevents.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.teamvoid.djevents.Activities.LoginActivity;
import com.teamvoid.djevents.Activities.MainActivity;
import com.teamvoid.djevents.R;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    // Elements
    private View view;

    // Firebase

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Data Binding


        return view;
    }
}
