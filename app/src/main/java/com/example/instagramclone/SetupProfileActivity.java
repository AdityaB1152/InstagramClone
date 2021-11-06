package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.instagramclone.Models.User;
import com.example.instagramclone.databinding.ActivitySetupProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Profile");
        dialog.setCancelable(false);


        String email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent , 25);
            }
        });

        binding.setupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.newusername.getText().toString();

                if(username.equals("")){
                    binding.newusername.setError("Please Enter a Username");
                }

                else{
                    if(selectedImage!=null){
                        dialog.show();
                        StorageReference reference = storage.getReference().child("Photos").child("Posts").child(auth.getUid()).child("profile_photo");
                        reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull  Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String profileImage = uri.toString();
                                            String uid = auth.getUid();
                                            String username = binding.newusername.getText().toString();
                                            String bio = binding.newbio.getText().toString();

                                            User user = new User(uid , name , email , username , profileImage , bio);

                                            database.getReference().child("user").child(uid)
                                                    .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(SetupProfileActivity.this , MainActivity.class);
                                                    startActivity(intent);
                                                    finishAffinity();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else{
                        String uid = auth.getUid();
                        String username1 = binding.newusername.getText().toString();
                        String bio = binding.newbio.getText().toString();

                        User user = new User(uid , name , email , username1 , "No Image" , bio);

                        database.getReference().child("user").child(uid)
                                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(SetupProfileActivity.this , MainActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        });

                    }
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        binding.profile.setImageURI(data.getData());
        selectedImage = data.getData();
    }
}