package com.example.datvu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datvu.model.Post;
import com.example.datvu.uschool.CommentsActivity;
import com.example.datvu.uschool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> lsPost;
    FirebaseFirestore db;
    Context context;
    FirebaseAuth mAuth;

    public PostAdapter(List<Post> lsPost) {
        this.lsPost = lsPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_post_item, parent, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final String postID = lsPost.get(position).getId();
        String userIdCurrent = mAuth.getUid();

        String desc = lsPost.get(position).getDescription();
        holder.setDescText(desc);
        try {
            long millisecond = lsPost.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setDateText(dateString);
        } catch (Exception ex) {
        }

        String imagePost = lsPost.get(position).getImageCompress();
        holder.setImagePost(imagePost);

        String username = lsPost.get(position).getUsername();
        holder.setUsername(username);

        String imageUser = lsPost.get(position).getImgUser();
        holder.setImageUser(imageUser);

        setLikeorDislike(holder, postID, userIdCurrent);

        setCountLike(holder, postID);

        setCountDocument(holder, postID);

        likeReadTime(holder, postID, userIdCurrent);


        /*
        boolean like = lsPost.get(position).isLike();
        if (like) {
            holder.imgLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like));
        } else {
            holder.imgLike.setImageDrawable(context.getResources().getDrawable(R.drawable.likegray));
        }*/

        holder.imgComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("postID", postID);
                context.startActivity(intent);
            }
        });
    }

    private void setCountDocument(final ViewHolder holder, String postID) {
        db.collection("Post/" + postID + "/Comment").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots.isEmpty()) {
                    holder.updateCommentCount(0);
                } else {
                    int count = documentSnapshots.size();
                    holder.updateCommentCount(count);
                }
            }
        });
    }


    private void setCountLike(final ViewHolder holder, String postID) {
        db.collection("Post/" + postID + "/like").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots.isEmpty()) {
                    holder.updateLikesCount(0);
                } else {
                    int count = documentSnapshots.size();
                    holder.updateLikesCount(count);
                }
            }
        });
    }

    private void likeReadTime(final ViewHolder holder, String postID, String userIdCurrent) {
        db.collection("Post/" + postID + "/like").document(userIdCurrent).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    holder.imgLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like));
                } else {
                    holder.imgLike.setImageDrawable(context.getResources().getDrawable(R.drawable.likegray));
                }
            }
        });
    }

    private void setLikeorDislike(final ViewHolder holder, final String postID, final String userIdCurrent) {
        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Post/" + postID + "/like").document(userIdCurrent).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> likeMap = new HashMap<>();
                            likeMap.put("timestamp", FieldValue.serverTimestamp());
                            db.collection("Post/" + postID + "/like").document(userIdCurrent).set(likeMap);
                            //holder.imgLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like));
                        } else {
                            db.collection("Post/" + postID + "/like").document(userIdCurrent).delete();
                            //holder.imgLike.setImageDrawable(context.getResources().getDrawable(R.drawable.likegray));
                        }
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return lsPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView txtDesc;
        TextView txtDate;
        ImageView imgPostUser;
        CircleImageView imgUser;
        TextView txtLikeAcount;
        TextView txtCommentCount;
        ImageView imgLike;
        TextView txtUser;
        ImageView imgComments;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imgLike = view.findViewById(R.id.imgLike);
            imgComments = view.findViewById(R.id.imgComments);
        }

        public void setDescText(String desc) {
            txtDesc = view.findViewById(R.id.txtDesc);
            txtDesc.setText(desc);
        }

        public void setDateText(String date) {
            txtDate = view.findViewById(R.id.txtDate);
            txtDate.setText(date);
        }

        public void setImagePost(String image) {
            imgPostUser = view.findViewById(R.id.imgPostUser);
            Glide.with(context).load(image).into(imgPostUser);

        }
        public void setImageUser(String imageUser){
            imgUser = view.findViewById(R.id.imgUser);
            if(!TextUtils.isEmpty(imageUser))
                Glide.with(context).load(imageUser).into(imgUser);
        }

        public void setUsername(String username){
            txtUser = view.findViewById(R.id.txtUser);
            txtUser.setText(username);
        }

        public void updateLikesCount(int count) {
            txtLikeAcount = view.findViewById(R.id.txtLikeAcount);
            txtLikeAcount.setText(count + " thích");
        }

        public void updateCommentCount(int count){
            txtCommentCount = view.findViewById(R.id.txtCommentCount);
            txtCommentCount.setText(count+ " Bình luận");
        }
    }
}
