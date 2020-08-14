package com.teamvoid.djevents.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamvoid.djevents.Adapters.SpinnerAdapter;
import com.teamvoid.djevents.Models.User;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import fr.ganfra.materialspinner.MaterialSpinner;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    // Elements
    private TextInputLayout tilName, tilEmail, tilPassword;
    private MaterialSpinner msYear, msDepartment;
    private FloatingActionButton fabSignUp;

    // Firebase
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Data Binding
        init();

        SpinnerAdapter yearAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, Constants.YEARS);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msYear.setAdapter(yearAdapter);

        SpinnerAdapter departmentAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, Constants.DEPARTMENTS);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msDepartment.setAdapter(departmentAdapter);

        fabSignUp.setOnClickListener(view -> {
            if (!validateName() | !validateEmail() | !validatePassword() | !validateYear() | !validateDepartment()) {
                Log.d(TAG, "onCreate: Validation Failed");
                return;
            }


            Log.d(TAG, "onCreate: Validation Completed");
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
                    firebaseUser.sendEmailVerification().addOnCompleteListener(emailTask -> {
                        if (emailTask.isSuccessful()) {
                            Log.d(TAG, "onCreate: Email verification sent successfully");
                            Toast.makeText(this, "Email verification sent successfully", Toast.LENGTH_SHORT).show();
                            saveUser(firebaseUser.getUid(), new User(email, name, year, department));
                        } else {
                            Log.d(TAG, "onCreate: Email verification not sent");
                            Toast.makeText(this, "Email verification not sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d(TAG, "onCreate: User not created");
                    if (createTask.getException() != null && createTask.getException().getMessage() != null) {
                        Log.d(TAG, "onCreate: Failed: " + createTask.getException().getMessage());
                        Toast.makeText(this, createTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "onCreate: Failed");
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void init() {
        tilName = findViewById(R.id.tilRegisterName);
        tilEmail = findViewById(R.id.tilRegisterEmail);
        tilPassword = findViewById(R.id.tilRegisterPassword);
        msYear = findViewById(R.id.msRegisterYear);
        msDepartment = findViewById(R.id.msRegisterDepartment);
        fabSignUp = findViewById(R.id.fabRegister);

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
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot added with ID: " + uid))
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error adding document");
                    Toast.makeText(this, "User details not saved", Toast.LENGTH_SHORT).show();
                });
    }
}