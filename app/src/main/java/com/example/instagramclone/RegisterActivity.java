package com.example.instagramclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramclone.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    String email , name , password, username;
    FirebaseAuth mAuth;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);


        
        email = binding.newemail.getText().toString();
        name = binding.newname.getText().toString();
        password = binding.newpass.getText().toString();




        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = binding.newemail.getText().toString();
               String name = binding.newname.getText().toString();
                String password = binding.newpass.getText().toString();


                if(email.equals("")){
                    binding.newemail.setError("Please enter an valid email");
                }
                if(name.equals("")){
                    binding.newname.setError("Please enter your Name");
                }

                if(password.equals("")){
                    binding.newpass.setError("Please enter password");
                }
                else {
                    dialog.show();
                    registerUser(email , password );

                }

            }
        });

    }


    /*
    Firebase Authetication
     */
    private void registerUser(String email, String password ) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Registration Done! Welcome", Toast.LENGTH_SHORT).show();
                    Intent intent =  new Intent(RegisterActivity.this , SetupProfileActivity.class);
                    intent.putExtra("email" , binding.newemail.getText().toString());
                    intent.putExtra("name" , binding.newname.getText().toString());
                    startActivity(intent);
                    finish();
                }
                else{
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
    }

}