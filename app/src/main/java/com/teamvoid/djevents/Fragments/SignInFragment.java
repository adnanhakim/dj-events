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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamvoid.djevents.Activities.MainActivity;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import java.util.Map;
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
    private FirebaseFirestore firebaseFirestore;

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

            // Start the progress bar
            progressBar.setVisibility(View.VISIBLE);
            Objects.requireNonNull(getActivity()).getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            String email = Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(tilPassword.getEditText()).getText().toString().trim();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(loginTask -> {
                if (loginTask.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    if (!firebaseUser.isEmailVerified()) {
                        // Stop the progress bar
                        progressBar.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Log.d(TAG, "onCreate: Email is not verified");
                        Toast.makeText(getContext(), "Email is not verified", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                    } else {
                        fetchData(firebaseUser.getUid());
                    }
                } else {
                    // Stop the progress bar
                    progressBar.setVisibility(View.GONE);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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

    public void displayReceivedData(String name, String email) {
        if (name != null) {
            String firstName = name.split(" ")[0];
            tvHeader.setText("Welcome " + firstName);
        }
        Objects.requireNonNull(tilEmail.getEditText()).setText(email);
    }

    private void init() {
        tvHeader = view.findViewById(R.id.tvSignInHeader);
        tilEmail = view.findViewById(R.id.tilSignInEmail);
        tilPassword = view.findViewById(R.id.tilSignInPassword);
        fabSignIn = view.findViewById(R.id.fabSignIn);
        progressBar = view.findViewById(R.id.progressSignIn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
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
        } else {
            tilPassword.setError(null);
            return true;
        }
    }

    private void fetchData(String uid) {
        DocumentReference reference = firebaseFirestore.collection(Constants.USERS).document(uid);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists() && document.getData() != null) {
                    saveData(document.getData());
                } else {
                    // Stop the progress bar
                    progressBar.setVisibility(View.GONE);
                    Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Log.d(TAG, "fetchData: User data does not exist");
                    Toast.makeText(getContext(), "User data does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Stop the progress bar
                progressBar.setVisibility(View.GONE);
                Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Log.d(TAG, "fetchData: Fetch failed");
                Toast.makeText(getContext(), "Could not retrieve data at the moment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveData(Map<String, Object> user) {
        String name = (String) user.get(Constants.NAME);
        String email = (String) user.get(Constants.EMAIL);
        String year = (String) user.get(Constants.YEAR);
        String department = (String) user.get(Constants.DEPARTMENT);

        SharedPref sharedPref = new SharedPref(Objects.requireNonNull(getContext()));
        sharedPref.saveLoginData(name, email, year, department);

        // Stop the progress bar
        progressBar.setVisibility(View.GONE);
        Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        startActivity(new Intent(getActivity(), MainActivity.class));
        Objects.requireNonNull(getActivity()).finish();
    }
}
