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
    private Button btnLogout;

    // Firebase
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Data Binding
        btnLogout = view.findViewById(R.id.btnProfileLogout);
        firebaseAuth = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(view -> {
            firebaseAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            Objects.requireNonNull(getActivity()).finish();
        });

        return view;
    }
}
