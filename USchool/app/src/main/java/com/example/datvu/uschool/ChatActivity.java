package com.example.datvu.uschool;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.datvu.adapter.ChatAdapter;
import com.example.datvu.model.Chat;
import com.example.datvu.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    Toolbar chatToolbar;
    ImageView imgSend;
    TextView txtMessage;
    RecyclerView rycChat;
    CircleImageView imgUser;

    String userIDchat;
    String userIDCurrent;
    List<Chat> lsChat;
    ChatAdapter adapterChat;
    Boolean isFirstPageFirstLoad = true;


    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentSnapshot lastVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        addControls();
        addEvents();
    }

    private void addEvents() {
        final String key = setKeyID(userIDchat, userIDCurrent);

        rycChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    loadMoreChat(key);
                }
            }
        });

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> chatMap = new HashMap<>();
                chatMap.put("message", txtMessage.getText().toString());
                chatMap.put("userID", userIDCurrent);
                chatMap.put("timestamp", FieldValue.serverTimestamp());
                db.collection("User")
                        .document("Chat")
                        .collection(key)
                        .add(chatMap)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(ChatActivity.this,
                                            "Lỗi tin nhắn: " + task.getException().toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                                txtMessage.setText("");
                            }
                        });
            }
        });
        Query firstQuery = db.collection("User/Chat/"+key).orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
        firstQuery.addSnapshotListener(ChatActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    if (isFirstPageFirstLoad) {
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        lsChat.clear();
                    }
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Chat chat = doc.getDocument().toObject(Chat.class);
                            if(isFirstPageFirstLoad) {
                                lsChat.add(chat);
                            }
                            else {
                                lsChat.add(0, chat);
                            }
                            adapterChat.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            }
        });
    }

    private void loadMoreChat(String key){
        if(mAuth.getCurrentUser() != null) {
            Query nextQuery = db.collection("User/Chat/"+key).
                    orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(10);
            nextQuery.addSnapshotListener(ChatActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (!documentSnapshots.isEmpty()) {
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                Chat chat = doc.getDocument().toObject(Chat.class);
                                lsChat.add(chat);
                                adapterChat.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }

    private void addControls() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userIDCurrent = mAuth.getUid();

        userIDchat = getIntent().getStringExtra("userID");
        String imageUser = getIntent().getStringExtra("imgUser");
        String username = getIntent().getStringExtra("username");

        chatToolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setTitle(username+" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgSend = findViewById(R.id.btnSend);
        txtMessage = findViewById(R.id.txtMessage);
        imgUser = findViewById(R.id.imgUser);
        if(!TextUtils.isEmpty(imageUser)){
            Glide.with(ChatActivity.this).load(imageUser).into(imgUser);
        }


        lsChat = new ArrayList<>();
        rycChat = findViewById(R.id.rycChat);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLayoutManager.setReverseLayout(true);
        rycChat.setLayoutManager(mLayoutManager);
        adapterChat = new ChatAdapter(lsChat);
        rycChat.setAdapter(adapterChat);
    }

    public String setKeyID(String userIDchar, String userIDcurrent) {
        char[] key = new char[userIDchar.length()];
        char[] key1 = userIDchar.toCharArray();
        char[] key2 = userIDcurrent.toCharArray();
        for (int i = 0; i < key1.length; i++) {
            key[i] = (char) ((key1[i] + key2[i]) / 2);
        }
        return String.valueOf(key);
    }
}
