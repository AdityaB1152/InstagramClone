package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapters.CommentAdapter;
import com.example.instagramclone.Models.Comment;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.databinding.ActivityCommentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    CommentAdapter adapter;
    ArrayList<Comment> comments;
    String postId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        postId = getIntent().getStringExtra("postid");

        comments = new ArrayList<>();
        adapter = new CommentAdapter(CommentActivity.this , comments);

    /*
    Displaying Profile Picture of Current User
     */
        database.getReference().child("user").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(CommentActivity.this).load(user.getProfileImage()).into(binding.commentProfile);

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

        /*
        Displaying Comments
         */

        binding.commentList.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        binding.commentList.setAdapter(adapter);

        database.getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()){
                    comments.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Comment comment = dataSnapshot.getValue(Comment.class);
                        comments.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });


        /*
        Adding Comments
         */

        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                database.getReference().child("user").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        String username = user.getUsername();
                        String profileImage = user.getProfileImage();
                        String postId = getIntent().getStringExtra("postid");
                        String mComment = binding.commentBox.getText().toString();

                        Comment comment = new Comment(username , mComment , profileImage);

                        database.getReference().child("Comments").child(postId).child(date.getTime()+"")
                                .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                binding.commentBox.setText("");
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
            }
        });
        /*
        Back Button
         */

        binding.commentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });






    }
}