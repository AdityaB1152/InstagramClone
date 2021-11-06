package com.example.instagramclone.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Adapters.UsersAdapter;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.example.instagramclone.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    ArrayList<User> users;
    UsersAdapter adapter;
    FirebaseDatabase database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater , container , false );

        database = database.getInstance();

        users = new ArrayList<>();
        adapter = new UsersAdapter(getContext() , users );
        binding.userList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.userList.setAdapter(adapter);

        /*
        Code for back Button
         */

        binding.searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction homeTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                homeTransaction.replace(R.id.frame , new HomeFragment());
                homeTransaction.commit();
            }
        });
/*
-----------------------------------------------------------------------------------------------------------------------------------

        List of Users in Search Fragment
 */

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
        Searching Users with FirebaseDatabase Query and Updating the ListView
         */

        binding.searchBox.addTextChangedListener(new TextWatcher() {
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

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = binding.searchBox.getText().toString();
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

        return binding.getRoot();
    }


}