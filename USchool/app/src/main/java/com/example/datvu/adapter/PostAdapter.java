package com.example.datvu.adapter;

import android.content.Context;
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
import com.example.datvu.model.User;
import com.example.datvu.uschool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> lsPost;
    FirebaseFirestore db;
    Context context;

    public PostAdapter(List<Post> lsPost) {
        this.lsPost = lsPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_post_item,parent,false);
        db = FirebaseFirestore.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String desc = lsPost.get(position).getDescription();
        holder.setDescText(desc);

        long millisecond = lsPost.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
        holder.setDateText(dateString);

        String imagePost = lsPost.get(position).getImageCompress();
        holder.setImagePost(imagePost);

        String userID = lsPost.get(position).getUserID();
        layThongTinUser(userID,holder);

    }

    private void layThongTinUser(String userID, final ViewHolder holder) {
        final User user = new User();
        db.collection("User")
                .document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()) {
                                user.setFirstName(task.getResult().getString("firstName"));
                                user.setLastName(task.getResult().getString("lastName"));
                                user.setPhone(task.getResult().getString("phone"));
                                user.setImage(task.getResult().getString("image"));

                                holder.setUser(user);
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return lsPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView txtDesc;
        TextView txtDate;
        ImageView imgPostUser;
        CircleImageView imgUser;
        TextView txtName;
        public ViewHolder(View itemView) {
            super(itemView);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            view = itemView;
        }
        public void setDescText(String desc){
            txtDesc = view.findViewById(R.id.txtDesc);
            txtDesc.setText(desc);
        }

        public void setDateText(String date){
            txtDate = view.findViewById(R.id.txtDate);
            txtDate.setText(date);
        }

        public void setImagePost(String image){
            imgPostUser = view.findViewById(R.id.imgPostUser);
            Glide.with(context).load(image).into(imgPostUser);

        }

        public void setUser(User user){
           txtName = view.findViewById(R.id.txtName);
           imgUser = view.findViewById(R.id.imgUser);
           if(!TextUtils.isEmpty(user.getImage())) {
               Glide.with(context).load(user.getImage()).into(imgUser);
           }
           txtName.setText(user.getFirstName()+" "+user.getLastName());
        }
    }
}
