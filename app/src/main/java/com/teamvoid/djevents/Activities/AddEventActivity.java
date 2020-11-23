package com.teamvoid.djevents.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.teamvoid.djevents.Adapters.SpinnerAdapter;
import com.teamvoid.djevents.Models.Event;
import com.teamvoid.djevents.Models.NotificationResponse;
import com.teamvoid.djevents.Network.ApiRequest;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.SharedPref;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "AddEventActivity";

    // Elements
    private ImageButton ibBack, ibEventDate, ibRegDate;
    private TextInputLayout tilName, tilDescription, tilEventDate, tilEligibility, tilPrice, tilRegLink, tilRegDate;
    private MaterialSpinner msStatus;
    private ShapeableImageView sivImage;
    private Button btnAddEvent;
    private ProgressBar progressBar;

    // Variables
    private String committeeId, photoPath, status;
    private Uri photoUri;
    private Date eventDate, regDate;
    private SharedPref sharedPref;
    private ApiRequest apiRequest;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Data Binding
        init();

        Intent callingIntent = getIntent();
        photoPath = callingIntent.getStringExtra(Constants.PHOTO_PATH);
        if (photoPath != null) {
            photoUri = Uri.fromFile(new File(photoPath));
            Log.d(TAG, "onCreate: Uri: " + photoUri);

            sivImage.setImageURI(photoUri);
        } else {
            Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        // Event Date Picker
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog eventDatePicker = new DatePickerDialog(this, R.style.datePicker, (datePicker, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            Objects.requireNonNull(tilEventDate.getEditText()).setText(date);
            try {
                eventDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date);
            } catch (ParseException e) {
                Log.d(TAG, "onCreate: Parsing error: " + e.getMessage());
                e.printStackTrace();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        ibEventDate.setOnClickListener(view -> eventDatePicker.show());

        // Registration Date Picker
        DatePickerDialog regDatePicker = new DatePickerDialog(this, R.style.datePicker, (datePicker, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            Objects.requireNonNull(tilRegDate.getEditText()).setText(date);
            try {
                regDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date);
            } catch (ParseException e) {
                Log.d(TAG, "onCreate: Parsing error: " + e.getMessage());
                e.printStackTrace();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        ibRegDate.setOnClickListener(view -> regDatePicker.show());

        // Status Spinner
        msStatus.setItems(getResources().getStringArray(R.array.status));
        msStatus.setOnItemSelectedListener((view, position, id, item) -> status = (String) item);
        status = (String) msStatus.getItems().get(msStatus.getSelectedIndex());

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnAddEvent.setOnClickListener(view -> {
            if (!validateName() | !validateDescription() | !validateEventDate() | !validateEligibility() |
                    !validatePrice() | !validateRegDate())
                return;

            uploadImage();
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarAddEvent);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibAddEventBack);
        tilName = findViewById(R.id.tilAddEventName);
        tilDescription = findViewById(R.id.tilAddEventDescription);
        tilEventDate = findViewById(R.id.tilAddEventDate);
        ibEventDate = findViewById(R.id.ibAddEventDate);
        tilEligibility = findViewById(R.id.tilAddEventEligibility);
        tilPrice = findViewById(R.id.tilAddEventPrice);
        tilRegLink = findViewById(R.id.tilAddEventLink);
        tilRegDate = findViewById(R.id.tilAddEventRegistrationDate);
        ibRegDate = findViewById(R.id.ibAddEventRegistrationDate);
        msStatus = findViewById(R.id.msAddEventStatus);
        sivImage = findViewById(R.id.sivAddEventImage);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        progressBar = findViewById(R.id.progressAddEvent);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.EVENTS);
        sharedPref = new SharedPref(this);
        apiRequest = ApiRequest.getInstance();
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            new SharedPref(this).removeData();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else committeeId = user.getUid();
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

    private boolean validateDescription() {
        String description = Objects.requireNonNull(tilDescription.getEditText()).getText().toString().trim();
        if (description.isEmpty()) {
            tilDescription.setError("Required");
            return false;
        } else {
            tilDescription.setError(null);
            return true;
        }
    }

    private boolean validateEventDate() {
        String eventDate = Objects.requireNonNull(tilEventDate.getEditText()).getText().toString().trim();
        if (eventDate.isEmpty()) {
            tilEventDate.setError("Select an event date");
            return false;
        } else {
            tilEventDate.setError(null);
            return true;
        }
    }

    private boolean validateEligibility() {
        String eligibility = Objects.requireNonNull(tilEligibility.getEditText()).getText().toString().trim();
        if (eligibility.isEmpty()) {
            tilEligibility.setError("Required");
            return false;
        } else {
            tilEligibility.setError(null);
            return true;
        }
    }

    private boolean validatePrice() {
        String price = Objects.requireNonNull(tilPrice.getEditText()).getText().toString().trim();
        if (price.isEmpty()) {
            tilPrice.setError("Required");
            return false;
        } else {
            tilPrice.setError(null);
            return true;
        }
    }

    private boolean validateRegDate() {
        String regDate = Objects.requireNonNull(tilRegDate.getEditText()).getText().toString().trim();
        if (regDate.isEmpty()) {
            tilRegDate.setError("Select a registration date");
            return false;
        } else {
            tilRegDate.setError(null);
            return true;
        }
    }

    private void uploadImage() {
        if (photoUri != null) {
            startProgressBar();

            String extension = photoPath.substring(photoPath.lastIndexOf("."));
            String fileName = System.currentTimeMillis() + extension;
            Log.d(TAG, "uploadFile: File Name: " + fileName);
            StorageReference fileReference = storageReference.child(fileName);
            fileReference.putFile(photoUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "onComplete: Photo upload successful");
                        fileReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> saveEvent(uri.toString()))
                                .addOnFailureListener(e -> {
                                    stopProgressBar();
                                    Log.e(TAG, "onComplete: Photo upload failed: " + e.getMessage());
                                    Toast.makeText(AddEventActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                });
                    })
                    .addOnFailureListener(e -> {
                        stopProgressBar();
                        Log.e(TAG, "onFailure: Photo upload failed: " + e.getMessage());
                        Toast.makeText(AddEventActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        }
    }

    private void saveEvent(String imageUrl) {
        startProgressBar();

        String name = Objects.requireNonNull(tilName.getEditText()).getText().toString().trim();
        String description = Objects.requireNonNull(tilDescription.getEditText()).getText().toString().trim();
        String eligibility = Objects.requireNonNull(tilEligibility.getEditText()).getText().toString().trim();
        String price = Objects.requireNonNull(tilPrice.getEditText()).getText().toString().trim();
        String registrationLink = Objects.requireNonNull(tilRegLink.getEditText()).getText().toString().trim();

        Event event = new Event(
                committeeId,
                sharedPref.getCommitteeName(),
                new Timestamp(new Date()),
                name, description,
                new Timestamp(eventDate),
                eligibility,
                price,
                registrationLink,
                new Timestamp(regDate),
                status,
                imageUrl
        );

        db.collection(Constants.EVENTS)
                .add(event)
                .addOnSuccessListener(documentReference -> updateEventCount(documentReference.getId(), name))
                .addOnFailureListener(e -> {
                    stopProgressBar();
                    Log.e(TAG, "onFailure: Event Failed: " + e.getMessage());
                    Toast.makeText(AddEventActivity.this, "Event failed to add", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEventCount(String eventId, String eventName) {
        db.collection(Constants.COMMITTEES)
                .document(Objects.requireNonNull(firebaseAuth.getUid()))
                .update(Constants.EVENTS, FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> sendEventNotification(eventId, eventName))
                .addOnFailureListener(e -> {
                    stopProgressBar();
                    Log.e(TAG, "onFailure: Event Failed: " + e.getMessage());
                    Toast.makeText(AddEventActivity.this, "Event failed to add", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendEventNotification(String eventId, String eventName) {
        String committeeName = sharedPref.getCommitteeName();
        String title = committeeName + " presents " + eventName;
        String topic = sharedPref.getCommitteeTopic();

        Log.d(TAG, "sendEventNotification: Title: " + title);
        Log.d(TAG, "sendEventNotification: Body: " + status);
        Log.d(TAG, "sendEventNotification: EventId: " + eventId);
        Log.d(TAG, "sendEventNotification: Topic: " + topic);

        apiRequest.sendEventNotification(title, status, eventId, topic, new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NotNull Call<NotificationResponse> call, @NotNull Response<NotificationResponse> response) {
                stopProgressBar();
                if (!response.isSuccessful() || response.body() == null) {
                    Log.d(TAG, "onResponse: Event notification not sent: " + response.code() + ": " + response.message());
                    Toast.makeText(AddEventActivity.this, "Event notification not sent: " + response.message(), Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d(TAG, "onResponse: Event added successfully");
                Toast.makeText(AddEventActivity.this, "Event added successful", Toast.LENGTH_SHORT).show();
                AddEventActivity.this.finish();
            }

            @Override
            public void onFailure(@NotNull Call<NotificationResponse> call, @NotNull Throwable t) {
                stopProgressBar();
                Log.e(TAG, "onFailure: Failed: " + t.getMessage());
                Toast.makeText(AddEventActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        AddEventActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
        AddEventActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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