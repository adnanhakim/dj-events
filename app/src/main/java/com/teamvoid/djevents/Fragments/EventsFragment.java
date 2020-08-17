package com.teamvoid.djevents.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamvoid.djevents.Adapters.CommitteesAdapter;
import com.teamvoid.djevents.Models.Committee;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.MarginItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    // Elements
    private View view;
    private RecyclerView recyclerCommittees;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_events, container, false);

        // Data Binding
        init();

        List<Committee> committees = new ArrayList<>();
        committees.add(new Committee("1", "DJ ACM", ""));
        committees.add(new Committee("2", "DJ CSI", ""));
        committees.add(new Committee("3", "DJ Callback", ""));
        committees.add(new Committee("4", "DJ Aura", ""));
        committees.add(new Committee("5", "DJ Trinity", ""));

        CommitteesAdapter committeesAdapter = new CommitteesAdapter(getContext(), committees);
        recyclerCommittees.setAdapter(committeesAdapter);
        recyclerCommittees.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerCommittees.addItemDecoration(new MarginItemDecorator(getContext(), 8, 8, 16, 16, true));

        return view;
    }

    private void init() {
        recyclerCommittees = view.findViewById(R.id.recyclerEventsCommittees);
    }
}
