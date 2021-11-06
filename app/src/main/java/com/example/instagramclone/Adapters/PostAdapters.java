package com.example.instagramclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.CommentActivity;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.R;
import com.example.instagramclone.databinding.SamplePostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PostAdapters extends RecyclerView.Adapter<PostAdapters.PostViewHolder>{
    Context context;
    ArrayList<Post> postList;

    public PostAdapters(Context context, ArrayList<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull

    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_post , parent ,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  PostAdapters.PostViewHolder holder, int position) {
        Post post = postList.get(position);



        holder.binding.postName.setText(post.getUsername());
        holder.binding.numLikes.setText(post.getLikes()+" Likes");
        holder.binding.smallPostName.setText(post.getUsername());
        holder.binding.postDescription.setText(post.getDescription());



        Glide.with(context).load(post.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.postProfile);

        Glide.with(context).load(post.getPostImage()).into(holder.binding.postImage);

        /*
        Liking and Unliking a post
         */

        holder.binding.unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.unlike.setVisibility(View.GONE);
                holder.binding.liked.setVisibility(View.VISIBLE);

                Integer updateLike = Integer.parseInt(post.getLikes())+1;
                post.setLikes(updateLike.toString());

                FirebaseDatabase.getInstance().getReference().child("Photos")
                        .child(post.getUid())
                        .child(post.getPostid()).setValue(post);
            }
        });

        holder.binding.liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.liked.setVisibility(View.GONE);
                holder.binding.unlike.setVisibility(View.VISIBLE);

                Integer updateLike = Integer.parseInt(post.getLikes())-1;
                post.setLikes(updateLike.toString());

                FirebaseDatabase.getInstance().getReference().child("Photos")
                        .child(post.getUid())
                        .child(post.getPostid()).setValue(post);

            }
        });

        /*
        Navigating to Comments Activity
         */

        holder.binding.viewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , CommentActivity.class);
                intent.putExtra("postid" , post.getPostid());
                context.startActivity(intent);

            }
        });

        /*
        Saving a Post
         */

        holder.binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Saved").child(FirebaseAuth.getInstance().getUid())
                        .child(post.getPostid())
                        .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        holder.binding.saved.setVisibility(View.VISIBLE);
                        holder.binding.save.setVisibility(View.GONE);

                        Toast.makeText(context, "Post Saved to your Profile", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        SamplePostBinding binding;

        public PostViewHolder(@NonNull  View itemView) {

            super(itemView);
            binding = SamplePostBinding.bind(itemView);


        }
    }
}

