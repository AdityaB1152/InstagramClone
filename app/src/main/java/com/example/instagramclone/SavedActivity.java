package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.instagramclone.Adapters.MyImageAdapter;
import com.example.instagramclone.Adapters.PostAdapters;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.databinding.ActivitySavedBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedActivity extends AppCompatActivity {

    ActivitySavedBinding binding;
    MyImageAdapter adapter;
    ArrayList<Post> posts;
    FirebaseDatabase database;
    FirebaseAuth auth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySavedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        /*
        Displaying Saved Posts on profile
         */

        posts = new ArrayList<>();
        adapter = new MyImageAdapter(SavedActivity.this , posts);

        binding.saveview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(SavedActivity.this  , 3);
        binding.saveview.setLayoutManager(layoutManager);
        binding.saveview.setAdapter(adapter);


        database.getReference().child("Saved").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot photo : snapshot.getChildren()){
                        Post post = photo.getValue(Post.class);
                        posts.add(post);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        Back button
         */

        binding.savedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}