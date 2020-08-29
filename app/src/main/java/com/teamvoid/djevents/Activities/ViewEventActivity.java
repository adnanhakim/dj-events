package com.teamvoid.djevents.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.teamvoid.djevents.Models.Event;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.Methods;
import com.teamvoid.djevents.Utils.SharedPref;

import java.util.Objects;

public class ViewEventActivity extends AppCompatActivity {

    private static final String TAG = "ViewEventActivity";

    // Elements
    private ImageButton ibBack;
    private ShapeableImageView sivImage;
    private TextView tvName, tvCommitteeName, tvStatus, tvDescription, tvEventDate, tvEligibility, tvPrice, tvRegDate;
    private LinearLayout linearRegister;
    private Button btnRegister;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    // Variables
    private String registrationLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        // Date Binding
        init();

        Intent callingIntent = getIntent();
        String eventId = callingIntent.getStringExtra(Constants.ID);
        if (eventId == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        fetchEvent(eventId);

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnRegister.setOnClickListener(view -> {
            if (registrationLink == null || registrationLink.equals(""))
                return;

            if (!registrationLink.startsWith("http://") && !registrationLink.startsWith("https://")) {
                Toast.makeText(this, "Registration Link is not proper", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(registrationLink));
            startActivity(browserIntent);
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarViewEvent);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibViewEventBack);
        sivImage = findViewById(R.id.sivViewEventImage);
        tvName = findViewById(R.id.tvViewEventName);
        tvCommitteeName = findViewById(R.id.tvViewEventCommitteeName);
        tvStatus = findViewById(R.id.tvViewEventStatus);
        tvDescription = findViewById(R.id.tvViewEventDescription);
        tvEventDate = findViewById(R.id.tvViewEventDate);
        tvEligibility = findViewById(R.id.tvViewEventEligibility);
        tvPrice = findViewById(R.id.tvViewEventPrice);
        tvRegDate = findViewById(R.id.tvViewEventRegDate);
        linearRegister = findViewById(R.id.linearViewEventFooter);
        btnRegister = findViewById(R.id.btnViewEventRegister);
        progressBar = findViewById(R.id.progressViewEvent);

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

    private void fetchEvent(String eventId) {
        Log.d(TAG, "fetchEvent: Fetching event...");

        startProgressBar();
        db.collection(Constants.EVENTS)
                .document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            event.setId(document.getId());
                            setData(event);
                        }
                    } else {
                        stopProgressBar();
                        Log.d(TAG, "fetchEvent: Event failed: " + Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                        this.finish();
                    }
                });
    }

    private void setData(Event event) {
        if (event.getImageUrl() != null && !event.getImageUrl().equals("")) {
            Picasso.get()
                    .load(event.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(sivImage);
        }

        tvName.setText(event.getName());
        tvCommitteeName.setText(event.getCommitteeName());

        if (event.getStatus() != null) {
            tvStatus.setText(event.getStatus());
            if (event.getStatus().equals(Constants.REGISTRATIONS_OPEN)) {
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
            } else if (event.getStatus().equals(Constants.REGISTRATIONS_CLOSED)) {
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.errorRed));
            } else tvStatus.setTextColor(ContextCompat.getColor(this, R.color.textColor));
        }

        tvDescription.setText(event.getDescription());
        tvEventDate.setText(Methods.formatDate(event.getEventDate().toDate()));
        tvEligibility.setText(event.getEligibility());
        tvPrice.setText(event.getPrice());
        tvRegDate.setText(Methods.formatDate(event.getRegistrationDate().toDate()));
        registrationLink = event.getRegistrationLink();

        if (event.getRegistrationLink() != null && !event.getRegistrationLink().equals(""))
            linearRegister.setVisibility(View.VISIBLE);
        else linearRegister.setVisibility(View.GONE);

        stopProgressBar();
    }



    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        ViewEventActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
        ViewEventActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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