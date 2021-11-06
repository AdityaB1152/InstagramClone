package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapters.MessageAdapter;
import com.example.instagramclone.Models.Message;
import com.example.instagramclone.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessageAdapter adapter;
    ArrayList<Message> messages;

    FirebaseDatabase database;

    String senderRoom , reciverRoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        String reciverUid = getIntent().getStringExtra("uid");
        String senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + reciverUid;
        reciverRoom = reciverUid + senderUid;

        messages = new ArrayList<>();
        adapter = new MessageAdapter(this , messages , senderRoom , reciverRoom);


        String Username = getIntent().getStringExtra("username");
        String profileImage = getIntent().getStringExtra("profileImage");
        binding.activename.setText(Username);
        Glide.with(this).load(profileImage).into(binding.profiledp);

        binding.messsagerecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.messsagerecyclerView.setAdapter(adapter);





        database.getReference().child("chats").child(senderRoom).child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        messages.clear();

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Message message = dataSnapshot.getValue(Message.class);
                            message.setMessageId(dataSnapshot.getKey());
                            messages.add(message);

                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageTxt = binding.messageBox.getText().toString();
                Date date = new Date();
                Message message  = new Message(messageTxt , senderUid , date.getTime());

                String randomKey = database.getReference().push().getKey();


                database.getReference().child("chats").child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats").child(reciverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                binding.messageBox.setText("");

                            }
                        });
                    }
                });


            }
        });





    }
}

/*
1.ChatActivity
2.Adding Feelings to messages;

3.List of Followers/Following

4.Displaying the list of Followers and Following

5.(Optional)Notification
 */