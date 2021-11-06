package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.instagramclone.Adapters.ChatUsers;
import com.example.instagramclone.Adapters.UsersAdapter;
import com.example.instagramclone.Fragments.HomeFragment;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.databinding.ActivityChatBinding;
import com.example.instagramclone.databinding.ActivityUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    ActivityUserBinding binding;
    ArrayList<User> users;
    ChatUsers adapter;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = ActivityUserBinding.inflate(getLayoutInflater());

        database = FirebaseDatabase.getInstance();

        users = new ArrayList<>();
        adapter = new ChatUsers(UserActivity.this , users );
        binding.chatList.setLayoutManager(new LinearLayoutManager(UserActivity.this));
        binding.chatList.setAdapter(adapter);
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());





        /*
        Code for Back Button
         */


        binding.chatBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

   //     List of Users in Search Fragment


        database.getReference().child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        if(!user.getUid().equals(FirebaseAuth.getInstance().getUid())){
                            users.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

        /*
        Searching Users in Chats section
         */

        binding.chatsearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s2, int start, int before, int count) {
                String s = s2.toString().toLowerCase();

                Query query = database.getReference().child("user").orderByChild("username")
                        .startAt(s)
                        .endAt(s+"\uf8ff");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            User user = ds.getValue(User.class);
                            users.add(user);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.chatsearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = binding.chatsearchBox.getText().toString();
                if(!s.equals("")){
                    Query query = database.getReference("user").orderByChild("username")
                            .startAt(s)
                            .endAt(s+"/uf8ff");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
                            users.clear();
                            for(DataSnapshot ds : snapshot.getChildren()){
                                User user = ds.getValue(User.class);
                                users.add(user);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });

                }
            }
        });
    }
}