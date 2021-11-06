package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapters.MyImageAdapter;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.databinding.ActivityOtherProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OtherProfile extends AppCompatActivity {
    ActivityOtherProfileBinding binding;
    String name , username , profileImage , uid , bio;
    FirebaseDatabase database;
    ArrayList<Post> posts;
    MyImageAdapter adapter;
    Boolean following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        name = getIntent().getStringExtra("name");
        username = getIntent().getStringExtra("username");
        profileImage= getIntent().getStringExtra("profileImage");
        uid = getIntent().getStringExtra("uid");
        bio = getIntent().getStringExtra("bio");



        binding = ActivityOtherProfileBinding.inflate(getLayoutInflater());
        database = FirebaseDatabase.getInstance();
        setContentView(binding.getRoot());
        /*
        Setting up Users Profile
         */
        binding.otherUsername.setText(username);
        binding.otherName.setText(name);
        binding.otherBio.setText(bio);
        Glide.with(OtherProfile.this).load(profileImage).into(binding.otherDP);

        //Number of Followers , Following and Posts

        database.getReference().child("Follow").child(uid).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                binding.noFollowers.setText(snapshot.getChildrenCount()+"");}
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        database.getReference().child("Follow").child(uid).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                binding.noFollowing.setText(snapshot.getChildrenCount()+"");}
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });




        //Displaying Users Post on Profile
        posts = new ArrayList<>();
        adapter = new MyImageAdapter(OtherProfile.this , posts);

        binding.othergridview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(OtherProfile.this , 3);
        binding.othergridview.setLayoutManager(layoutManager);
        binding.othergridview.setAdapter(adapter);


        database.getReference().child("Photos").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot photo : snapshot.getChildren()){
                        Post post = photo.getValue(Post.class);
                        posts.add(post);
                    }
                    binding.noPosts.setText(posts.size()+"");
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
/*
Follow and Unfollowing a User
 */

        //Checking if User Already follows the Person
        database.getReference().child("Follow").child(FirebaseAuth.getInstance().getUid())
                .child("following").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.getValue(Boolean.class)){
                            binding.follow.setVisibility(View.GONE);
                            binding.following.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        //Following the User
        binding.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    database.getReference().child("Follow").child(FirebaseAuth.getInstance().getUid())
                            .child("following").child(uid).setValue(true);
                    database.getReference().child("Follow").child(uid).child("followers")
                            .child(FirebaseAuth.getInstance().getUid()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            binding.follow.setVisibility(View.GONE);
                            binding.following.setVisibility(View.VISIBLE);
                        }
                    });
            }
        });

        //Unfollowing the User
        binding.following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("Follow").child(FirebaseAuth.getInstance().getUid())
                        .child("following").child(uid).removeValue();
                database.getReference().child("Follow").child(uid).child("followers")
                        .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.following.setVisibility(View.GONE);
                        binding.follow.setVisibility(View.VISIBLE);

                    }
                });

            }
        });

        /*
        Back Button
         */
        binding.otherBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}