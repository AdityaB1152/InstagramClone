package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.databinding.ActivityEditProfileBinding;
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

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    FirebaseDatabase database;
    FirebaseStorage storage;
    User user;
    Uri selectedImage;
    FirebaseAuth auth;
    String existingImage;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(EditProfileActivity.this);
        dialog.setMessage("Updating Profile");
        dialog.setCancelable(false);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        database.getReference().child("user").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);

                        existingImage = user.getProfileImage();
                        Glide.with(EditProfileActivity.this).load(user.getProfileImage()).into(binding.editProfileDP);
                        binding.editName.setText(user.getName());
                        binding.editUsername.setText(user.getUsername());

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });





        binding.changephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                startActivityForResult(intent , 25);
            }
        });


        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                if(selectedImage!=null) {
                    StorageReference reference = storage.getReference().child("Photos").child("Posts").child(auth.getUid()).child("profile_photo");

                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String profileImage = uri.toString();

                                    User user = new User(auth.getUid(), binding.editName.getText().toString(), auth.getCurrentUser().getEmail(), binding.editUsername.getText().toString(), profileImage , binding.editBio.getText().toString());

                                    database.getReference().child("user").child(auth.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
                else{

                    User user = new User(auth.getUid() , binding.editName.getText().toString() , auth.getCurrentUser().getEmail() ,
                            binding.editUsername.getText().toString() , existingImage , binding.editBio.getText().toString());

                    database.getReference().child("user").child(auth.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();
                            finish();
                        }
                    });

                }
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            if(data.getData()!=null){
                selectedImage = data.getData();
                binding.editProfileDP.setImageURI(data.getData());
            }
        }
    }
}