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
import com.teamvoid.djevents.Adapters.SpinnerAdapter;
import com.teamvoid.djevents.Models.User;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.util.HashMap;
import java.util.Objects;

import fr.ganfra.materialspinner.MaterialSpinner;

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

    // Firebase
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Data Binding
        init();

        SpinnerAdapter yearAdapter = new SpinnerAdapter(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, Constants.YEARS);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msYear.setAdapter(yearAdapter);

        SpinnerAdapter departmentAdapter = new SpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, Constants.DEPARTMENTS);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msDepartment.setAdapter(departmentAdapter);

        fabSignUp.setOnClickListener(view1 -> {
            if (!validateName() | !validateEmail() | !validatePassword() | !validateYear() | !validateDepartment()) {
                Log.d(TAG, "onCreate: Validation Failed");
                return;
            }

            // Start the progress bar
            progressBar.setVisibility(View.VISIBLE);
            Objects.requireNonNull(getActivity()).getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            String name = Objects.requireNonNull(tilName.getEditText()).getText().toString().trim();
            String email = Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(tilPassword.getEditText()).getText().toString().trim();
            String year = (String) msYear.getSelectedItem();
            String department = (String) msDepartment.getSelectedItem();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(createTask -> {
                if (createTask.isSuccessful()) {
                    Log.d(TAG, "onCreate: User created successfully");
                    firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    firebaseAuth.signOut();
                    firebaseUser.sendEmailVerification().addOnCompleteListener(emailTask -> {
                        if (emailTask.isSuccessful()) {
                            Log.d(TAG, "onCreate: Email verification sent successfully");
                            saveUser(firebaseUser.getUid(), new User(email, name, year, department));
                        } else {
                            // Stop the progress bar
                            progressBar.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Log.d(TAG, "onCreate: Email verification not sent");
                            Toast.makeText(getContext(), "Email verification not sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Stop the progress bar
                    progressBar.setVisibility(View.GONE);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Log.d(TAG, "onCreate: User not created");
                    if (createTask.getException() != null && createTask.getException().getMessage() != null) {
                        Log.d(TAG, "onCreate: Failed: " + createTask.getException().getMessage());
                        Toast.makeText(getContext(), createTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "onCreate: Failed");
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
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
        firebaseFirestore = FirebaseFirestore.getInstance();
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

    private boolean validateYear() {
        int year = msYear.getSelectedItemPosition();
        if (year == 0) {
            msYear.setError("Required");
            return false;
        } else {
            msYear.setError(null);
            return true;
        }
    }

    private boolean validateDepartment() {
        int department = msDepartment.getSelectedItemPosition();
        if (department == 0) {
            msDepartment.setError("Required");
            return false;
        } else {
            msDepartment.setError(null);
            return true;
        }
    }

    private void saveUser(String uid, User user) {
        firebaseAuth.signOut();

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(Constants.EMAIL, user.getEmail());
        userMap.put(Constants.NAME, user.getName());
        userMap.put(Constants.YEAR, user.getYear());
        userMap.put(Constants.DEPARTMENT, user.getDepartment());

        firebaseFirestore.collection(Constants.USERS)
                .document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    // Stop the progress bar
                    progressBar.setVisibility(View.GONE);
                    Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Toast.makeText(getContext(), "Please verify your email and login", Toast.LENGTH_SHORT).show();
                    String name = Objects.requireNonNull(tilName.getEditText()).getText().toString().trim();
                    String email = Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim();
                    sendUser.sendUser(name, email);

                    Log.d(TAG, "DocumentSnapshot added with ID: " + uid);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error adding document");
                    Toast.makeText(getContext(), "User details not saved", Toast.LENGTH_SHORT).show();
                });
    }
}
