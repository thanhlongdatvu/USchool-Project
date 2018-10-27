package com.example.datvu.uschool;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.datvu.adapter.PostAdapter;
import com.example.datvu.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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

    Boolean isFirstPageFirstLoad = true;

    DocumentSnapshot lastVisible;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
            addControls(view, container);
            addEvents(container);
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

    private void addEvents(final ViewGroup container) {

        rycListPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!recyclerView.canScrollVertically(1)) {
                    loadMorePost();
                }
            }
        });

        Query firstQuery = db.collection("Post").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

        firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    if (isFirstPageFirstLoad) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        lsPost.clear();

                    }
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            final String postId = doc.getDocument().getId();
                            final Post post = doc.getDocument().toObject(Post.class).setId(postId);

                            db.collection("Post/"+postId+"/like")
                                    .document(mAuth.getCurrentUser().getUid())
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                            if(documentSnapshot.exists()){
                                                post.setLike(true);
                                            }
                                            else {
                                                post.setLike(false);
                                            }
                                        }
                                    });

                            if (isFirstPageFirstLoad) {
                                lsPost.add(post);
                            } else {
                                lsPost.add(0, post);
                            }
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                }
                isFirstPageFirstLoad = false;
            }
        });
    }

    public  void loadMorePost(){
        Query nextQuery = db.collection("Post")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);
        nextQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            final String postId = doc.getDocument().getId();
                            final Post post = doc.getDocument().toObject(Post.class).setId(postId);

                            db.collection("Post/"+postId+"/like")
                                    .document(mAuth.getCurrentUser().getUid())
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                            if(documentSnapshot.exists()){
                                                post.setLike(true);
                                            }
                                            else {
                                                post.setLike(false);
                                            }
                                        }
                                    });

                            lsPost.add(post);
                            postAdapter.notifyDataSetChanged();
                        }


                    }
                }
            }
        });
    }
}
