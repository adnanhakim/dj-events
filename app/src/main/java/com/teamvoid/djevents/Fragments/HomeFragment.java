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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamvoid.djevents.Adapters.PostAdapter;
import com.teamvoid.djevents.Models.Post;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.MarginItemDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    
    // Elements
    private View view;
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

        return view;
    }

    private void init() {
        recyclerPosts = view.findViewById(R.id.recyclerHomePosts);

        posts = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    private void fetchPosts() {
        Log.d(TAG, "fetchPosts: Fetching posts...");
        posts.clear();

        db.collection(Constants.POSTS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "onComplete: Posts fetched successfully");
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            String id = document.getId();
                            Post post = document.toObject(Post.class);
                            post.setId(id);
                            posts.add(post);
                        }
                        setUpRecyclerView();
                    } else {
                        Log.d(TAG, "onComplete: Posts could not be retrieved: " + Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(getContext(), "Posts could not be retrieved", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUpRecyclerView() {
        PostAdapter postAdapter = new PostAdapter(getContext(), posts);
        recyclerPosts.setAdapter(postAdapter);
        recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerPosts.addItemDecoration(new MarginItemDecorator(getContext(), 16, 16, 0, 0));
    }
}
