package com.example.instagramclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.MainActivity;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.OtherProfile;
import com.example.instagramclone.R;
import com.example.instagramclone.databinding.SearchModelBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private Context context;
    private ArrayList<User> users;

    public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_model , parent ,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  UsersAdapter.UserViewHolder holder, int position) {

        User user = users.get(position);

        holder.binding.searchName.setText(user.getName());
        holder.binding.searchUsername.setText(user.getUsername());

        Glide.with(context).load(user.getProfileImage()).into(holder.binding.searchProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    Intent intent = new Intent(context, OtherProfile.class);
                    intent.putExtra("name", user.getName());
                    intent.putExtra("username", user.getUsername());
                    intent.putExtra("profileImage", user.getProfileImage());
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("bio" , user.getBio());
                    context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        SearchModelBinding binding;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = SearchModelBinding.bind(itemView);
        }
    }
}
