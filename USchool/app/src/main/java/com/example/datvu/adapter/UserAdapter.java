package com.example.datvu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datvu.model.User;
import com.example.datvu.uschool.ChatActivity;
import com.example.datvu.uschool.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<User> lsUser;
    Context context;

    public UserAdapter(List<User> lsUser) {
        this.lsUser = lsUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_item,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String imageUser = lsUser.get(position).getImage();
        holder.setImageUser(imageUser);

        String username = lsUser.get(position).getLastName()+" "+lsUser.get(position).getFirstName();
        holder.setUsername(username);

        holder.cardUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userID",lsUser.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lsUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgUser;
        TextView txtUser;
        CardView cardUser;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            cardUser = view.findViewById(R.id.cardUser);
        }

        public void setImageUser(String imageUser){
            imgUser = view.findViewById(R.id.imgUser);
            if(!TextUtils.isEmpty(imageUser)){
                Glide.with(context).load(imageUser).into(imgUser);
            }
        }

        public void setUsername(String username){
            txtUser = view.findViewById(R.id.txtUser);
            txtUser.setText(username);
        }
    }
}
