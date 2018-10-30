package com.example.datvu.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.datvu.model.Chat;
import com.example.datvu.uschool.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    List<Chat> lsChat;
    FirebaseAuth mAuth;
    String userIDCurrent;

    public ChatAdapter(List<Chat> lsChat) {
        this.lsChat = lsChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chat_item,parent,false);
        mAuth = FirebaseAuth.getInstance();
        userIDCurrent = mAuth.getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = lsChat.get(position);
        if(chat.getUserID().equals(userIDCurrent)){
          holder.txtSend.setVisibility(View.VISIBLE);
          holder.txtReceive.setVisibility(View.INVISIBLE);
          holder.txtSend.setText(chat.getMessage());
        }else{
            holder.txtReceive.setVisibility(View.VISIBLE);
            holder.txtSend.setVisibility(View.INVISIBLE);
            holder.txtReceive.setText(chat.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return lsChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView txtReceive;
        TextView txtSend;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            txtSend = view.findViewById(R.id.txtSend);
            txtReceive = view.findViewById(R.id.txtReceive);
        }
    }
}
