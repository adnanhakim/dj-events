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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.teamvoid.djevents.Adapters.CommitteeAdapter;
import com.teamvoid.djevents.Adapters.EventAdapter;
import com.teamvoid.djevents.Models.Committee;
import com.teamvoid.djevents.Models.Event;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.MarginItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    private static final String TAG = "EventsFragment";

    // Elements
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
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

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchCommittees();
            fetchEvents();
        });

        return view;
    }

    private void init() {
        swipeRefreshLayout = view.findViewById(R.id.srlEvents);
        recyclerCommittees = view.findViewById(R.id.recyclerEventsCommittees);
        recyclerCommittees.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerCommittees.addItemDecoration(new MarginItemDecorator(getContext(), 8, 8, 16, 16, true));
        recyclerEvents = view.findViewById(R.id.recyclerEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerEvents.addItemDecoration(new MarginItemDecorator(getContext(), 16, 16, 16, 16));

        committees = new ArrayList<>();
        events = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    private void fetchCommittees() {
        Log.d(TAG, "fetchCommittees: Fetching committees...");
        committees.clear();

        swipeRefreshLayout.setRefreshing(true);
        db.collection(Constants.COMMITTEES)
                .orderBy(Constants.NAME, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "onSuccess: Committees fetched");
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Committee committee = document.toObject(Committee.class);
                        committee.setId(document.getId());
                        committees.add(committee);
                    }
                    recyclerCommittees.setAdapter(new CommitteeAdapter(getContext(), committees));
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: Committees failed: " +  e.getMessage());
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchEvents() {
        Log.d(TAG, "fetchEvents: Fetching events...");
        events.clear();

        swipeRefreshLayout.setRefreshing(true);
        db.collection(Constants.EVENTS)
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
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: Events failed: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                });
    }
}
