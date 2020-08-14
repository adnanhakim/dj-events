package com.teamvoid.djevents.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.teamvoid.djevents.Adapters.SpinnerAdapter;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

import java.util.Arrays;

import fr.ganfra.materialspinner.MaterialSpinner;

public class RegisterActivity extends AppCompatActivity {

    // Elements
    private MaterialSpinner msYear, msDepartment;

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
    }

    private void init() {
        msYear = findViewById(R.id.msRegisterYear);
        msDepartment = findViewById(R.id.msRegisterDepartment);
    }
}