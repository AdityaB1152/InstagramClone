package com.example.instagramclone.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapters.MyImageAdapter;
import com.example.instagramclone.EditProfileActivity;
import com.example.instagramclone.LoginActivity;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.example.instagramclone.SavedActivity;
import com.example.instagramclone.databinding.FragmentHomeBinding;
import com.example.instagramclone.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    MyImageAdapter adapter;
    ArrayList<Post> posts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding  = FragmentProfileBinding.inflate(inflater , container , false);
        database = FirebaseDatabase.getInstance();

        auth = FirebaseAuth.getInstance();
        database.getReference().child("user").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        binding.textView3.setText(user.getName());
                        binding.textView.setText(user.getUsername());
                        Glide.with(getActivity()).load(user.getProfileImage()).into(binding.circleImageView);
                        binding.bio.setText(user.getBio());

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
        /*
        Number of Followers and Following
         */

        database.getReference().child("Follow").child(auth.getUid()).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    binding.noFollowers.setText(snapshot.getChildrenCount()+"");}
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        database.getReference().child("Follow").child(auth.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    binding.noFollowing.setText(snapshot.getChildrenCount()+"");}
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        /*
        Logging out a User
         */

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent =  new Intent(getActivity() , LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();

            }
        });

        binding.textView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getActivity() , EditProfileActivity.class);
                startActivity(intent);
            }
        });

        /*
        Displaying Posts in Profile

         */

        posts = new ArrayList<>();
        adapter = new MyImageAdapter(getActivity() , posts);

        binding.gridview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity()  , 3);
        binding.gridview.setLayoutManager(layoutManager);
        binding.gridview.setAdapter(adapter);


        database.getReference().child("Photos").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot photo : snapshot.getChildren()){
                        Post post = photo.getValue(Post.class);

                        posts.add(post);
                        binding.noPosts.setText(posts.size()+"");
                    }

                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        Navigating to SavedPostActivity
         */

        binding.savedPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity() , SavedActivity.class);
                startActivity(intent);

            }
        });
        return binding.getRoot();

    }
}