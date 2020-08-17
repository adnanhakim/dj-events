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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Adapters
    private PostAdapter postAdapter;

    // Elements
    private View view;
    private RecyclerView rvPosts;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // Data Binding
        init();

        List<Post> posts = new ArrayList<>();
        posts.add (new Post("1", "arsh", "5 min", "1", "100", "900"));
        posts.add (new Post("1", "arsh1", "5 min", "1", "100", "900"));
        posts.add (new Post("1", "arsh2", "5 min", "1", "100", "900"));
        posts.add (new Post("1", "arsh3", "5 min", "1", "100", "900"));

        postAdapter = new PostAdapter(posts, getContext());

        rvPosts.setAdapter(postAdapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void init() {
        rvPosts = view.findViewById(R.id.rvPosts);
    }
}
