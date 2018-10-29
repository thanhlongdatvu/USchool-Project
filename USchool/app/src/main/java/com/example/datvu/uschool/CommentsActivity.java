package com.example.datvu.uschool;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.datvu.adapter.CommentAdapter;
import com.example.datvu.model.Comment;
import com.example.datvu.model.User;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    Toolbar commentToolbar;
    EditText txtComment;
    ImageView imgSend;
    RecyclerView rycComments;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    String postID;
    String currentUserID;
    List<Comment> lsComment;
    CommentAdapter adapterComment;
    Map<String, Object> commentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        addControls();
        addEvents();
    }

    private void addEvents() {

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String commetMessage = txtComment.getText().toString();

                db.collection("User")
                        .document(currentUserID).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    User user = task.getResult().toObject(User.class);
                                    commentMap.put("username",user.getLastName() + " " + user.getFirstName());
                                    commentMap.put("imgUser",user.getImage());

                                    if (!commetMessage.isEmpty()) {
                                        commentMap.put("message",commetMessage);
                                        commentMap.put("timestamp", FieldValue.serverTimestamp());
                                        db.collection("Post/"+postID+"/Comment").add(commentMap)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if(!task.isSuccessful()){
                                                            Toast.makeText(CommentsActivity.this,
                                                                    "Lỗi bình luận: "+task.getException().toString(),
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });
        db.collection("Post/"+postID+"/Comment")
                .addSnapshotListener(CommentsActivity.this,new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if(!documentSnapshots.isEmpty()){
                            for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
                                if(doc.getType() == DocumentChange.Type.ADDED){
                                    String commentId = doc.getDocument().getId();
                                    Comment comment = doc.getDocument().toObject(Comment.class);
                                    lsComment.add(comment);
                                    adapterComment.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });

    }

    private void addControls() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        postID = getIntent().getStringExtra("postID");
        commentToolbar = findViewById(R.id.commentToolbar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Bình luận");
        txtComment = findViewById(R.id.txtComment);
        imgSend = findViewById(R.id.imgSend);

        lsComment = new ArrayList<>();
        rycComments = findViewById(R.id.rycComments);
        rycComments.setLayoutManager(new LinearLayoutManager(this));
        rycComments.setHasFixedSize(true);
        adapterComment = new CommentAdapter(lsComment);
        rycComments.setAdapter(adapterComment);



    }
}
