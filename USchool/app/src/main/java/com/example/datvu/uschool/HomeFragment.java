package com.example.datvu.uschool;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datvu.adapter.PostAdapter;
import com.example.datvu.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView rycListPost;
    List<Post> lsPost;
    FirebaseFirestore db;
    PostAdapter postAdapter;
    FirebaseAuth mAuth;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        addControls(view, container);
        addEvents();
        // Inflate the layout for this fragment
        return view;
    }

    private void addControls(View view, ViewGroup container) {
        rycListPost = view.findViewById(R.id.rycListPost);
        lsPost = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        postAdapter = new PostAdapter(lsPost);
        rycListPost.setLayoutManager(new LinearLayoutManager(container.getContext()));
        rycListPost.setAdapter(postAdapter);
    }

    private void addEvents() {
        if(mAuth.getCurrentUser() != null) {
            db.collection("Post")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Post post = doc.getDocument().toObject(Post.class);
                                    lsPost.add(post);
                                    postAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }
    }
}
