package com.example.instagramclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.R;
import com.example.instagramclone.databinding.SampleGridBinding;

import java.util.ArrayList;

public class MyImageAdapter extends RecyclerView.Adapter<MyImageAdapter.ImageViewHolder>{
    private Context context;
    private ArrayList<Post> posts;

    public MyImageAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.sample_grid , parent ,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MyImageAdapter.ImageViewHolder holder, int position) {
        Post post = posts.get(position);

        Glide.with(context).load(post.getPostImage()).into(holder.binding.displayPost);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        SampleGridBinding binding;

        public ImageViewHolder(@NonNull  View itemView) {
            super(itemView);
            binding = SampleGridBinding.bind(itemView);
        }
    }
}
