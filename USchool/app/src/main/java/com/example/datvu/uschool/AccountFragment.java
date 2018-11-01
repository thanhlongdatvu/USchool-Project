package com.example.datvu.uschool;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datvu.adapter.PostAdapter;
import com.example.datvu.adapter.UserAdapter;
import com.example.datvu.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    RecyclerView rycListUser;
    List<User> lsUsers;
    UserAdapter adapterUser;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userIDCurrent;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userIDCurrent = mAuth.getUid();
        addControls(view, container);
        addEvents();
        // Inflate the layout for this fragment
        return view;

    }

    private void addEvents() {
       db.collection("User").addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
               if (!documentSnapshots.isEmpty()) {
                   for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                       String userID = doc.getDocument().getId();
                        User user = doc.getDocument().toObject(User.class).setId(userID);
                       if(!userID.equals(userIDCurrent)) {
                           lsUsers.add(user);
                           adapterUser.notifyDataSetChanged();
                       }
                   }
               }
           }
       });
    }

    private void addControls(View view, ViewGroup container) {
        rycListUser = view.findViewById(R.id.rycUser);
        lsUsers = new ArrayList<>();
        adapterUser = new UserAdapter(lsUsers);
        rycListUser.setLayoutManager(new LinearLayoutManager(container.getContext()));
        rycListUser.setAdapter(adapterUser);
    }

}
