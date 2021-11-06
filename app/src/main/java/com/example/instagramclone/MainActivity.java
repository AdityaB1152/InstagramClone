package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagramclone.Fragments.HomeFragment;
import com.example.instagramclone.Fragments.NotificationFragment;
import com.example.instagramclone.Fragments.PostFragment;
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.Fragments.SearchFragment;
import com.example.instagramclone.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    BottomNavigationView navigationView;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        navigationView = binding.navigation;

        navigationView.getMenu().findItem(R.id.home).setChecked(true);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        FragmentTransaction homeTransaction = getSupportFragmentManager().beginTransaction();
                        homeTransaction.replace(R.id.frame , new HomeFragment());
                        homeTransaction.commit();
                        break;
                    case R.id.search:
                        FragmentTransaction searchTransaction = getSupportFragmentManager().beginTransaction();
                        searchTransaction.replace(R.id.frame , new SearchFragment());
                        searchTransaction.commit();
                        break;
                    case R.id.plus:
                        FragmentTransaction postTrans = getSupportFragmentManager().beginTransaction();
                        postTrans.replace(R.id.frame , new PostFragment());
                        postTrans.commit();
                        break;

                    case R.id.profile:
                        FragmentTransaction userTrans = getSupportFragmentManager().beginTransaction();
                        userTrans.replace(R.id.frame , new ProfileFragment());
                        userTrans.commit();
                        break;


                }

                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(MainActivity.this , LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


}