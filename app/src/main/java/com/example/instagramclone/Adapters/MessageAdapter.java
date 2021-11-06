package com.example.instagramclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Models.Message;
import com.example.instagramclone.R;
import com.example.instagramclone.databinding.ItemReceiveBinding;
import com.example.instagramclone.databinding.ItemSentBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    String senderRoom;
    String receiverRoom;
    public MessageAdapter(Context context , ArrayList<Message> messages , String senderRoom , String receiverRoom){
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent , parent , false);
            return new sentViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive , parent , false);
            return new reciverViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return  ITEM_SENT;
        }
        else{
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

       int [ ] reactions = new int[]{
               R.drawable.ic_fb_like,
               R.drawable.ic_fb_love,
               R.drawable.ic_fb_laugh,
               R.drawable.ic_fb_wow,
               R.drawable.ic_fb_sad,
               R.drawable.ic_fb_angry

        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(new int[]{
                        R.drawable.ic_fb_like,
                        R.drawable.ic_fb_love,
                        R.drawable.ic_fb_laugh,
                        R.drawable.ic_fb_wow,
                        R.drawable.ic_fb_sad,
                        R.drawable.ic_fb_angry
                })
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

            if(holder.getClass() == sentViewHolder.class){
                sentViewHolder sentViewHolder = (MessageAdapter.sentViewHolder)holder;
                sentViewHolder.binding.feeling.setImageResource(reactions[pos]);
                sentViewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else {
                reciverViewHolder ViewHolder = (MessageAdapter.reciverViewHolder)holder;
                ViewHolder.binding.feeling.setImageResource(reactions[pos]);
                ViewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }

            message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child("messages")
                    .child(message.getMessageId()).setValue(message);
            return true; // true is closing popup, false is requesting a new selection
        });
        if(holder.getClass()==(sentViewHolder.class)){
            sentViewHolder viewHolder = (sentViewHolder) holder;

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v , event);
                    return false;
                }
            });

            if(message.getMessage().equals("photo")){
                viewHolder.binding.chatImage.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);

                Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.chatImage);
            }
            viewHolder.binding.message.setText(message.getMessage());
            if(message.getFeeling()>=0){
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
        }
        else{

            reciverViewHolder viewHolder = (reciverViewHolder) holder;

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v , event);
                    return false;
                }
            });
            if(message.getMessage().equals("photo")){
                viewHolder.binding.chatImage.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);

                Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.chatImage);
            }
            viewHolder.binding.message.setText(message.getMessage());
            if(message.getFeeling()>=0){
                //message.setFeeling(reactions[(int)message.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class sentViewHolder extends RecyclerView.ViewHolder{
        ItemSentBinding binding;
        public sentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class reciverViewHolder extends RecyclerView.ViewHolder{
        ItemReceiveBinding binding ;
        public reciverViewHolder(@NonNull  View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }
}

