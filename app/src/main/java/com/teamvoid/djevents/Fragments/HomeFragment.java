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
import com.teamvoid.djevents.Adapters.PostAdapter;
import com.teamvoid.djevents.Models.Post;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.MarginItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // Elements
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerPosts;

    // Variables
    private List<Post> posts;

    // Firebase
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // Data Binding
        init();

        fetchPosts();

        swipeRefreshLayout.setOnRefreshListener(this::fetchPosts);

        return view;
    }

    private void init() {
        swipeRefreshLayout = view.findViewById(R.id.srlHome);
        recyclerPosts = view.findViewById(R.id.recyclerHomePosts);
        recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerPosts.addItemDecoration(new MarginItemDecorator(getContext(), 16, 16, 0, 0));

        posts = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    private void fetchPosts() {
        Log.d(TAG, "fetchPosts: Fetching posts...");
        posts.clear();
        swipeRefreshLayout.setRefreshing(true);

        db.collection(Constants.POSTS)
                .orderBy(Constants.TIMESTAMP, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "onSuccess: Posts fetched successfully");
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        post.setId(document.getId());
                        posts.add(post);
                    }
                    setUpRecyclerView();
                })
                .addOnFailureListener(e -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "onFailure: Posts could not be retrieved: " + e.getMessage());
                    Toast.makeText(getContext(), "Posts could not be retrieved: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setUpRecyclerView() {
        PostAdapter postAdapter = new PostAdapter(getContext(), posts);
        recyclerPosts.setAdapter(postAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
