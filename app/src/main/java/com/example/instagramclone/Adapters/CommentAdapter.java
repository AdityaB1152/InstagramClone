package com.example.instagramclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Models.Comment;
import com.example.instagramclone.R;
import com.example.instagramclone.databinding.SampleCommentBinding;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private ArrayList<Comment> comments;

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull

    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_comment , parent , false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  CommentAdapter.CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.binding.comment.setText(comment.getmComment());
        holder.binding.commentUsername.setText(comment.getUsername());

        Glide.with(context).load(comment.getProfileImage()).into(holder.binding.commentProfile);


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        SampleCommentBinding binding;

        public CommentViewHolder(@NonNull  View itemView) {

            super(itemView);

            binding = SampleCommentBinding.bind(itemView);
        }
    }


}
