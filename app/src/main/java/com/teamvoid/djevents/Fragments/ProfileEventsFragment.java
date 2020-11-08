package com.teamvoid.djevents.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.teamvoid.djevents.Adapters.EventAdapter;
import com.teamvoid.djevents.Models.Event;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.MarginItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class ProfileEventsFragment extends Fragment {

    private static final String TAG = "ProfileEventsFragment";

    // Elements
    private View view;
    private RecyclerView recyclerEvents;

    // Variables
    private String committeeId;
    private List<Event> events;

    // Firebase
    private FirebaseFirestore db;

    public ProfileEventsFragment(String committeeId) {
        this.committeeId = committeeId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_events, container, false);

        // Data Binding
        init();

        fetchEvents();

        return view;
    }

    private void init() {
        recyclerEvents = view.findViewById(R.id.recyclerProfileEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerEvents.addItemDecoration(new MarginItemDecorator(getContext(), 16, 16, 16, 16));

        events = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    private void fetchEvents() {
        Log.d(TAG, "fetchEvents: Fetching events...");
        events.clear();

        Log.d(TAG, "fetchEvents: Committee Id: " + committeeId);
        db.collection(Constants.EVENTS)
                .whereEqualTo(Constants.COMMITTEE_ID, committeeId)
                .orderBy(Constants.EVENT_DATE, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "onSuccess: Events fetched");
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        event.setId(document.getId());
                        events.add(event);
                    }
                    recyclerEvents.setAdapter(new EventAdapter(getContext(), events));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: Events failed: " + e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

}
