package com.teamvoid.djevents.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import java.util.HashMap;
import java.util.Objects;

public class AddMemberActivity extends AppCompatActivity {

    private static final String TAG = "AddMemberActivity";
    
    // Elements
    private ImageButton ibBack;
    private TextInputLayout tilName, tilPosition;
    private Button btnAddMember;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        // Data Binding
        init();

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnAddMember.setOnClickListener(view -> {
            if (!validateName() | !validatePosition())
                return;

            FirebaseUser user = firebaseAuth.getCurrentUser();
            checkUserStatus();

            startProgressBar();
            String name = Objects.requireNonNull(tilName.getEditText()).getText().toString().trim();
            String position = Objects.requireNonNull(tilPosition.getEditText()).getText().toString().trim();

            assert user != null;
            addMember(user, name, position);
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarAddMember);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibAddMemberBack);
        tilName = findViewById(R.id.tilAddMemberName);
        tilPosition = findViewById(R.id.tilAddMemberPosition);
        btnAddMember = findViewById(R.id.btnAddMember);
        progressBar = findViewById(R.id.progressAddMember);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            new SharedPref(this).removeData();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
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

    private boolean validatePosition() {
        String position = Objects.requireNonNull(tilPosition.getEditText()).getText().toString().trim();
        if (position.isEmpty()) {
            tilPosition.setError("Required");
            return false;
        } else {
            tilPosition.setError(null);
            return true;
        }
    }
    
    private void addMember(FirebaseUser user, String name, String position) {
        HashMap<String, Object> member = new HashMap<>();
        member.put(Constants.NAME, name);
        member.put(Constants.POSITION, position);

        db.collection(Constants.COMMITTEES)
                .document(user.getUid())
                .collection(Constants.MEMBERS)
                .add(member)
                .addOnSuccessListener(documentReference -> {
                    stopProgressBar();
                    Toast.makeText(this, "Member Added", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onSuccess: Member Added");
                    Objects.requireNonNull(tilName.getEditText()).setText("");
                    Objects.requireNonNull(tilPosition.getEditText()).setText("");
                })
                .addOnFailureListener(e -> {
                    stopProgressBar();
                    Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure: Failed to add: " + e.getMessage());
                });
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        AddMemberActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
        AddMemberActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}