package com.example.instagramclone.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.MainActivity;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.example.instagramclone.databinding.FragmentPostBinding;
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

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class PostFragment extends Fragment {
    FragmentPostBinding binding;
    Uri selectedImage;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Uploading Image");
        dialog.setCancelable(false);


        String username;
    /*
    Selecting Image from External Storage
     */
        binding.addToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 25);
            }
        });

        /*
        Username and ProfileImage
         */

        database.getReference().child("user").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    Glide.with(getActivity())
                            .load(user.getProfileImage())
                            .placeholder(R.drawable.avatar)
                            .into(binding.newPostProfile);
                    binding.newPostName.setText(user.getUsername());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        Uploading Image to Storage and creating database
         */
        binding.postImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImage != null) {
                    dialog.show();
                    Date date = new Date();
                    StorageReference reference = storage.getReference().child("Photos").child("Posts")
                            .child(auth.getUid()).child(""+date.getTime());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String postImage = uri.toString();
                                    database.getReference().child("user").child(auth.getUid())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        User user = snapshot.getValue(User.class);

                                                        String username = user.getUsername();
                                                        String profileImage = user.getProfileImage();
                                                        String description = binding.editTextTextMultiLine.getText().toString();

                                                        String likes = "0";
                                                        Date date = new Date();
                                                        String postid = date.getTime()+"";
                                                        Post post = new Post(postid, auth.getUid() , username, profileImage, postImage, description, likes);
                                                        database.getReference().child("Photos")
                                                                .child(auth.getUid())
                                                                .child(postid)
                                                                .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                dialog.dismiss();
                                                                FragmentTransaction homeTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                                homeTransaction.replace(R.id.frame , new HomeFragment());
                                                                homeTransaction.commit();
                                                            }
                                                        });


                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            });
                        }
                    });
                }
            }
        });

        return binding.getRoot();


    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        binding.newPost.setImageURI(data.getData());
        selectedImage = data.getData();



    }
}