package com.teamvoid.djevents.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamvoid.djevents.Adapters.MemberAdapter;
import com.teamvoid.djevents.Models.Member;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.MarginItemDecorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileMembersFragment extends Fragment {

    private static final String TAG = "ProfileMembersFragment";

    // Elements
    private View view;
    private RecyclerView recyclerMembers;
    private TextView tvNoMembers;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_members, container, false);

        // Data Binding
        init();

        fetchMembers();

        return view;
    }

    private void init() {
        recyclerMembers = view.findViewById(R.id.recyclerProfileMembers);
        tvNoMembers = view.findViewById(R.id.tvProfileMembersNoMembers);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void fetchMembers() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            db.collection(Constants.COMMITTEES)
                    .document(user.getUid())
                    .collection(Constants.MEMBERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "fetchMembers: Successfully fetched members");
                            List<Member> members = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                members.add(new Member(document.getString(Constants.NAME), document.getString(Constants.POSITION)));
                                setData(members);
                            }
                        } else {
                            Log.d(TAG, "fetchMembers: Failed to fetched members: " + task.getException());
                            Toast.makeText(getContext(), "Failed to get members", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setData(List<Member> members) {
        if (members.size() > 0) {
            tvNoMembers.setVisibility(View.GONE);
            recyclerMembers.setVisibility(View.VISIBLE);

            Collections.sort(members, (a, b) -> a.getName().compareTo(b.getName()));
            MemberAdapter adapter = new MemberAdapter(getContext(), members);
            recyclerMembers.setAdapter(adapter);
            recyclerMembers.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            recyclerMembers.addItemDecoration(new MarginItemDecorator(getContext(), 8, 8, 8, 8));
        } else  {
            tvNoMembers.setVisibility(View.VISIBLE);
            recyclerMembers.setVisibility(View.GONE);
        }
    }
}
