package com.teamvoid.djevents.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamvoid.djevents.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Elements
    private TextInputLayout tilEmail, tilPassword;
    private FloatingActionButton fabLogin;
    private TextView tvSignUp;

    // Firebase
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Data Binding
        init();

        fabLogin.setOnClickListener(view -> {
            if (!validateEmail() | !validatePassword())
                return;

            if (firebaseUser != null && !firebaseUser.isEmailVerified()) {
                Log.d(TAG, "onCreate: Email is not verified");
                Toast.makeText(this, "Email is not verified", Toast.LENGTH_SHORT).show();
            }

            String email = Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(tilPassword.getEditText()).getText().toString().trim();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(loginTask -> {
                if (loginTask.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    if (!firebaseUser.isEmailVerified()) {
                        Log.d(TAG, "onCreate: Email is not verified");
                        Toast.makeText(this, "Email is not verified", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                    } else {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }
                } else {
                    Log.d(TAG, "onCreate: Login Failed");
                    if (loginTask.getException() != null && loginTask.getException().getMessage() != null) {
                        Log.d(TAG, "onCreate: Failed: " + loginTask.getException().getMessage());
                        Toast.makeText(this, loginTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "onCreate: Failed");
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseUser = firebaseAuth.getCurrentUser();
//        if (firebaseUser != null) {
//            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//        }
    }

    private void init() {
        tilEmail = findViewById(R.id.tilLoginEmail);
        tilPassword = findViewById(R.id.tilLoginPassword);
        fabLogin = findViewById(R.id.fabLogin);
        tvSignUp = findViewById(R.id.tvLoginSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
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
}