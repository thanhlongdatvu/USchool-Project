package com.example.datvu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datvu.model.Comment;
import com.example.datvu.uschool.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    public List<Comment> lsComment;
    public Context context;

    public CommentAdapter(List<Comment> lsComment) {
        this.lsComment = lsComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_comment_item,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String commentMessage = lsComment.get(position).getMessage();
        holder.setCommentMessage(commentMessage);

        String username = lsComment.get(position).getUsername();
        holder.setUsername(username);

        String imageUser = lsComment.get(position).getImgUser();
        holder.setImageUser(imageUser);
    }

    @Override
    public int getItemCount() {
        return lsComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView txtMessage;
        CircleImageView imgUser;
        TextView txtUser;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setCommentMessage(String message){
            txtMessage = view.findViewById(R.id.txtMessage);
            txtMessage.setText(message);
        }

        public void setImageUser(String image){
            imgUser = view.findViewById(R.id.imgUser);
            if(!TextUtils.isEmpty(image)) {
                Glide.with(context).load(image).into(imgUser);
            }
        }

        public void setUsername(String username){
            txtUser = view.findViewById(R.id.txtUser);
            txtUser.setText(username);
        }
    }
}
