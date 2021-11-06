package com.example.instagramclone.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.ChatActivity;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.example.instagramclone.databinding.SearchModelBinding;

import java.util.ArrayList;

public class ChatUsers extends RecyclerView.Adapter<ChatUsers.ChatUserViewHolder>{

    Context context;

    public ChatUsers(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    ArrayList<User> users;
    @NonNull

    @Override
    public ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_model , parent , false);
        return new ChatUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ChatUsers.ChatUserViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.searchUsername.setText(user.getUsername());
        holder.binding.searchName.setText("Tap to chat");
        Glide.with(context).load(user.getProfileImage())
                .into(holder.binding.searchProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ChatActivity.class);
                intent.putExtra("username" , user.getUsername());
                intent.putExtra("uid" , user.getUid());
                intent.putExtra("profileImage" , user.getProfileImage());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ChatUserViewHolder extends RecyclerView.ViewHolder{
        SearchModelBinding binding;

        public ChatUserViewHolder(@NonNull  View itemView) {

            super(itemView);
            binding = SearchModelBinding.bind(itemView);
        }
    }
}

