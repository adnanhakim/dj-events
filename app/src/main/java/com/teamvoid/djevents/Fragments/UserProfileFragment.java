package com.teamvoid.djevents.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.teamvoid.djevents.Models.User;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";

    // Elements
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvName, tvEmail;
    private TextInputLayout tilName;
    private MaterialSpinner msYear, msDepartment;
    private Button btnSave;

    // Variables
    private SharedPref sharedPref;
    private String year, department;

    // Firebase
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Data Binding
        init();

        tvName.setText(sharedPref.getUserName());
        tvEmail.setText(sharedPref.getUserEmail());

        msYear.setItems(getResources().getStringArray(R.array.years));
        msYear.setOnItemSelectedListener((view, position, id, item) -> year = (String) item);

        msDepartment.setItems(getResources().getStringArray(R.array.departments));
        msDepartment.setOnItemSelectedListener((view, position, id, item) -> department = (String) item);

        fetchUserDetails();

        btnSave.setOnClickListener(view -> {
            if (!validateName())
                return;

            swipeRefreshLayout.setRefreshing(true);
            String name = Objects.requireNonNull(tilName.getEditText()).getText().toString().trim();
            year = (String) msYear.getItems().get(msYear.getSelectedIndex());
            department = (String) msDepartment.getItems().get(msDepartment.getSelectedIndex());
            Map<String, Object> userMap = new HashMap<>();
            userMap.put(Constants.NAME, name);
            userMap.put(Constants.DEPARTMENT, department);
            userMap.put(Constants.YEAR, year);

            db.collection(Constants.USERS)
                    .document(firebaseUser.getUid())
                    .update(userMap)
                    .addOnSuccessListener(aVoid -> {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Updated!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "onCreateView: Update Failed: " + e.getMessage());
                        swipeRefreshLayout.setRefreshing(false);
                    });
        });

        return view;
    }

    private void init() {
        swipeRefreshLayout = view.findViewById(R.id.srlUserProfile);
        tvName = view.findViewById(R.id.tvUserProfileName);
        tvEmail = view.findViewById(R.id.tvUserProfileEmail);
        tilName = view.findViewById(R.id.tilUserProfileName);
        msYear = view.findViewById(R.id.msUserProfileYear);
        msDepartment = view.findViewById(R.id.msUserProfileDepartment);
        btnSave = view.findViewById(R.id.btnUserProfileSave);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        sharedPref = new SharedPref(Objects.requireNonNull(getContext()));
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

    private void fetchUserDetails() {
        swipeRefreshLayout.setRefreshing(true);
        db.collection(Constants.USERS)
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        user.setId(documentSnapshot.getId());
                        setData(user);
                    }
                })
                .addOnFailureListener(e -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure: Failed: " + e.getMessage());
                });
    }

    private void setData(User user) {
        Objects.requireNonNull(tilName.getEditText()).setText(user.getName());

        tilName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                tvName.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        int yearIndex = 0, departmentIndex = 0;
        List<String> years = msYear.getItems();
        for (int i = 0; i < years.size(); i++) {
            if (user.getYear().equals(years.get(i))) {
                yearIndex = i;
                break;
            }
        }
        msYear.setSelectedIndex(yearIndex);
        Log.d(TAG, "setData: " + msYear.getSelectedIndex());

        List<String> departments = msDepartment.getItems();
        for (int i = 0; i < departments.size(); i++) {
            if (user.getDepartment().equals(departments.get(i))) {
                departmentIndex = i;
                break;
            }
        }
        msDepartment.setSelectedIndex(departmentIndex);

        swipeRefreshLayout.setRefreshing(false);
    }
}
