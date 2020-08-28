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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamvoid.djevents.Adapters.CommitteesAdapter;
import com.teamvoid.djevents.Adapters.EventAdapter;
import com.teamvoid.djevents.Models.Committee;
import com.teamvoid.djevents.Models.Event;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.MarginItemDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventsFragment extends Fragment {

    private static final String TAG = "EventsFragment";

    // Elements
    private View view;
    private RecyclerView recyclerCommittees, recyclerEvents;

    // Variables
    private List<Committee> committees;
    private List<Event> events;

    // Firebase
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_events, container, false);

        // Data Binding
        init();

        fetchCommittees();
        fetchEvents();

        return view;
    }

    private void init() {
        recyclerCommittees = view.findViewById(R.id.recyclerEventsCommittees);
        recyclerEvents = view.findViewById(R.id.recyclerEvents);

        committees = new ArrayList<>();
        events = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    private void fetchCommittees() {
        Log.d(TAG, "fetchCommittees: Fetching committees...");
        committees.clear();

        db.collection(Constants.COMMITTEES)
                .orderBy(Constants.NAME, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Committee committee = document.toObject(Committee.class);
                            committee.setId(document.getId());
                            committees.add(committee);
                        }
                        setUpCommitteeRecycler();
                    } else {
                        Log.d(TAG, "onComplete: Committees failed: " +  Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUpCommitteeRecycler() {
        CommitteesAdapter committeesAdapter = new CommitteesAdapter(getContext(), committees);
        recyclerCommittees.setAdapter(committeesAdapter);
        recyclerCommittees.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerCommittees.addItemDecoration(new MarginItemDecorator(getContext(), 8, 8, 16, 16, true));
    }

    private void fetchEvents() {
        Log.d(TAG, "fetchEvents: Fetching events...");
        events.clear();

        db.collection(Constants.EVENTS)
                .orderBy(Constants.EVENT_DATE, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());
                            events.add(event);
                        }
                        setUpEventRecycler();
                    } else {
                        Log.d(TAG, "onComplete: Events failed: " + Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUpEventRecycler() {
        EventAdapter adapter = new EventAdapter(getContext(), events);
        recyclerEvents.setAdapter(adapter);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerEvents.addItemDecoration(new MarginItemDecorator(getContext(), 16, 16, 16, 16));
    }
}
