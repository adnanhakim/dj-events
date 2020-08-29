package com.teamvoid.djevents.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamvoid.djevents.Activities.MainActivity;
import com.teamvoid.djevents.Models.Committee;
import com.teamvoid.djevents.Models.User;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import java.util.Objects;

public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";

    // Elements
    private View view;
    private TextView tvHeader;
    private TextInputLayout tilEmail, tilPassword;
    private FloatingActionButton fabSignIn;
    private ProgressBar progressBar;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private boolean isCommittee = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin, container, false);

        // Data Binding
        init();

        fabSignIn.setOnClickListener(view1 -> {
            if (!validateEmail() | !validatePassword())
                return;

            if (firebaseUser != null && !firebaseUser.isEmailVerified()) {
                Log.d(TAG, "onCreate: Email is not verified");
                Toast.makeText(getContext(), "Email is not verified", Toast.LENGTH_SHORT).show();
            }

            startProgressBar();
            String email = Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(tilPassword.getEditText()).getText().toString().trim();

            // Check if it is a committee login
            isCommittee = email.endsWith("@djevents.com");

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(loginTask -> {
                if (loginTask.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    if (isCommittee) {
                        fetchCommitteeData(firebaseUser.getUid());
                    } else if (!firebaseUser.isEmailVerified()) {
                        stopProgressBar();
                        Log.d(TAG, "onCreate: Email is not verified");
                        Toast.makeText(getContext(), "Email is not verified", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                    } else {
                        fetchUserData(firebaseUser.getUid());
                    }
                } else {
                    stopProgressBar();
                    Log.d(TAG, "onCreate: Login Failed");
                    if (loginTask.getException() != null && loginTask.getException().getMessage() != null) {
                        Log.d(TAG, "onCreate: Failed: " + loginTask.getException().getMessage());
                        Toast.makeText(getContext(), loginTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "onCreate: Failed");
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        return view;
    }

    private void init() {
        tvHeader = view.findViewById(R.id.tvSignInHeader);
        tilEmail = view.findViewById(R.id.tilSignInEmail);
        tilPassword = view.findViewById(R.id.tilSignInPassword);
        fabSignIn = view.findViewById(R.id.fabSignIn);
        progressBar = view.findViewById(R.id.progressSignIn);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void displayReceivedData(String name, String email) {
        if (name != null) {
            String greeting = "Welcome " + name.split(" ")[0];
            tvHeader.setText(greeting);
        }
        Objects.requireNonNull(tilEmail.getEditText()).setText(email);
    }

    private boolean validateEmail() {
        String email = Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("Required");
            return false;
        } else {
            tilEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = Objects.requireNonNull(tilPassword.getEditText()).getText().toString().trim();
        if (password.isEmpty()) {
            tilPassword.setError("Required");
            return false;
        } else if (password.length() < 6) {
            tilPassword.setError("Must be atleast 6 characters");
            return false;
        } else {
            tilPassword.setError(null);
            return true;
        }
    }

    private void fetchCommitteeData(String uid) {
        db.collection(Constants.COMMITTEES)
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        Committee committee = document.toObject(Committee.class);
                        if (committee != null) {
                            saveCommitteeData(committee);
                        } else {
                            stopProgressBar();
                            Log.d(TAG, "fetchData: Committee data does not exist");
                            Toast.makeText(getContext(), "Committee does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        stopProgressBar();
                        Log.d(TAG, "fetchData: Fetch failed");
                        Toast.makeText(getContext(), "Could not retrieve data at the moment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveCommitteeData(Committee committee) {
        SharedPref sharedPref = new SharedPref(Objects.requireNonNull(getContext()));
        sharedPref.saveCommitteeData(committee.getName(), committee.getEmail(), committee.getImageUrl(), committee.getTopics());

        stopProgressBar();
        startActivity(new Intent(getActivity(), MainActivity.class));
        Objects.requireNonNull(getActivity()).finish();
    }

    private void fetchUserData(String uid) {
        db.collection(Constants.USERS)
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        User user = document.toObject(User.class);
                        if (user != null) {
                            saveUserData(user);
                        } else {
                            stopProgressBar();
                            Log.d(TAG, "fetchData: User data does not exist");
                            Toast.makeText(getContext(), "User data does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        stopProgressBar();
                        Log.d(TAG, "fetchData: Fetch failed");
                        Toast.makeText(getContext(), "Could not retrieve data at the moment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(User user) {
        SharedPref sharedPref = new SharedPref(Objects.requireNonNull(getContext()));
        sharedPref.saveUserData(user.getName(), user.getEmail(), user.getYear(), user.getDepartment(), user.getTopics());

        stopProgressBar();
        startActivity(new Intent(getActivity(), MainActivity.class));
        Objects.requireNonNull(getActivity()).finish();
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
        Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
