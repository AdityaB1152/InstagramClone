package com.example.instagramclone.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Adapters.PostAdapters;
import com.example.instagramclone.Adapters.StatusAdapter;
import com.example.instagramclone.ChatActivity;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.Models.Status;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.Models.UserStatus;
import com.example.instagramclone.UserActivity;
import com.example.instagramclone.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    ArrayList<Post> postList;
    PostAdapters postAdapters;
    FragmentHomeBinding binding;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;
    Uri selectedImage;
    StatusAdapter adapter;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog dialog;
    User user;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater , container , false );
        postList = new ArrayList<>();
        userStatuses = new ArrayList<>();
        postAdapters = new PostAdapters(getActivity() ,postList);
        adapter = new StatusAdapter(getActivity() , userStatuses);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Uploading Story");
        dialog.setCancelable(false);


        LinearLayoutManager storyLM =  new LinearLayoutManager(getActivity());
        storyLM.setOrientation(RecyclerView.HORIZONTAL);

        binding.stories.setLayoutManager(storyLM);




        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getActivity() , UserActivity.class);
                startActivity(intent);
            }
        });



        binding.postlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.postlist.setAdapter(postAdapters);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.stories.setLayoutManager(layoutManager);
        binding.stories.setAdapter(adapter);

        database.getReference().child("user").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

        /*
        Displaying Posts . i.e Adding Post Object to Arraylist<Post>
         */

        database.getReference().child("Photos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    postList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        for(DataSnapshot ds2 : ds.getChildren()){
                            Post post = ds2.getValue(Post.class);

                            postList.add(post);
                        }
                    }
                    postAdapters.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
/*
Displaying Story , adding Story Object to Arraylist<Story>
 */
        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userStatuses.clear();
                    for(DataSnapshot storySnapshot :snapshot.getChildren()){
                        UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastupdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();
                        for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()){
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }
                        status.setStatuses(statuses);
                        userStatuses.add(status);

                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

/*
Uploading Stories...
 */
        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent , 25);
            }
        });






        return binding.getRoot();
    }
/*
onActivityResult method to Upload story to storag and Database
 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            if(data.getData()!=null){
                dialog.show();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("stories").child(auth.getUid()).child(date.getTime()+"");
                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull  Task<UploadTask.TaskSnapshot> task) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UserStatus userStatus = new UserStatus();
                                userStatus.setName(user.getUsername());
                                userStatus.setProfileImage(user.getProfileImage());
                                userStatus.setLastUpdated(date.getTime());

                                HashMap<String , Object> obj = new HashMap<>();
                                obj.put("name" , userStatus.getName());
                                obj.put("profileImage"  , userStatus.getProfileImage());
                                obj.put("lastupdated" , userStatus.getLastUpdated());

                                String imageUrl = uri.toString();
                                Status status = new Status(imageUrl , userStatus.getLastUpdated());

                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .updateChildren(obj);

                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("statuses")
                                        .push()
                                        .setValue(status);

                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        }
    }
}