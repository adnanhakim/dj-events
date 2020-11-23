package com.teamvoid.djevents.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.teamvoid.djevents.Models.User;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.util.ArrayList;
import java.util.Objects;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";

    // Elements
    private View view;
    private TextInputLayout tilName, tilEmail, tilPassword;
    private MaterialSpinner msYear, msDepartment;
    private FloatingActionButton fabSignUp;
    private ProgressBar progressBar;

    // Interface
    private SendUser sendUser;

    // Variables
    private String year, department;

    // Firebase
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Data Binding
        init();

        msYear.setItems(getResources().getStringArray(R.array.years));
        msYear.setOnItemSelectedListener((view, position, id, item) -> year = (String) item);
        year = (String) msYear.getItems().get(msYear.getSelectedIndex());

        msDepartment.setItems(getResources().getStringArray(R.array.departments));
        msDepartment.setOnItemSelectedListener((view, position, id, item) -> department = (String) item);
        department = (String) msDepartment.getItems().get(msDepartment.getSelectedIndex());

        fabSignUp.setOnClickListener(view1 -> {
            if (!validateName() | !validateEmail() | !validatePassword())
                return;

            startProgressBar();
            String name = Objects.requireNonNull(tilName.getEditText()).getText().toString().trim();
            String email = Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(tilPassword.getEditText()).getText().toString().trim();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        Log.d(TAG, "onSuccess: User created successfully");
                        firebaseUser = authResult.getUser();
                        firebaseAuth.signOut();
                        firebaseUser.sendEmailVerification()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "onSuccess: Email verification sent successfully");
                                    saveUser(firebaseUser.getUid(), email, name);
                                })
                                .addOnFailureListener(e -> {
                                    stopProgressBar();
                                    Log.e(TAG, "onFailure: Email verification not sent: " + e.getMessage());
                                    Toast.makeText(getContext(), "Email verification not sent", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        stopProgressBar();
                        Log.e(TAG, "onFailure: User not created: " + e.getMessage());
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        return view;
    }

    public interface SendUser {
        void sendUser(String name, String email);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            sendUser = (SendUser) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    private void init() {
        tilName = view.findViewById(R.id.tilSignUpName);
        tilEmail = view.findViewById(R.id.tilSignUpEmail);
        tilPassword = view.findViewById(R.id.tilSignUpPassword);
        msYear = view.findViewById(R.id.msSignUpYear);
        msDepartment = view.findViewById(R.id.msSignUpDepartment);
        fabSignUp = view.findViewById(R.id.fabSignUp);
        progressBar = view.findViewById(R.id.progressSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private boolean validateName() {
        String name = Objects.requireNonNull(tilName.getEditText()).getText().toString().trim();
        if (name.isEmpty()) {
            tilName.setError("Required");
            return false;
        } else {
            tilName.setError(null);
            return true;
        }
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

    private void saveUser(String uid, String email, String name) {
        firebaseAuth.signOut();

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(instanceIdResult -> {
                    String token = instanceIdResult.getToken();
                    User user = new User(email, name, year, department, token, new ArrayList<>());

                    db.collection(Constants.USERS)
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                stopProgressBar();
                                Toast.makeText(getContext(), "Please verify your email and login", Toast.LENGTH_SHORT).show();
                                sendUser.sendUser(name, email);

                                Log.d(TAG, "saveUser: DocumentSnapshot added with ID: " + uid);
                            })
                            .addOnFailureListener(e -> {
                                stopProgressBar();
                                Log.e(TAG, "saveUser: Error adding document: " + e.getMessage());
                                Toast.makeText(getContext(), "User details not saved", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Could not retrieve token", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: Could not retrieve token: " + e.getMessage());
                });
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
