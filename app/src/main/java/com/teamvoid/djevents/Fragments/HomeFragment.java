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

import com.teamvoid.djevents.Adapters.PostAdapter;
import com.teamvoid.djevents.Models.Post;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.MarginItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Elements
    private View view;
    private RecyclerView recyclerPosts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // Data Binding
        init();

        List<Post> posts = new ArrayList<>();
        posts.add(new Post("1", "", "DJ CSI", "5 min", "", "Welcome to Codeshashtra 6.0", "128", "3"));
        posts.add(new Post("2", "", "DJ CSI", "2 days", "", "2 days to Codeshashtra 6.0", "301", "14"));
        posts.add(new Post("3", "", "DJ ACM", "Just Now", "", "Welcome to LOC 2.0", "18", "0"));
        posts.add(new Post("4", "", "DJ Callback", "7 days", "", "whiteboard.io", "428", "21"));

        PostAdapter postAdapter = new PostAdapter(getContext(), posts);
        recyclerPosts.setAdapter(postAdapter);
        recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerPosts.addItemDecoration(new MarginItemDecorator(getContext(), 16, 16, 0, 0));

        return view;
    }

    private void init() {
        recyclerPosts = view.findViewById(R.id.recyclerHomePosts);
    }
}
