package com.example.instagramclone.Models;

import java.util.ArrayList;

public class Post {
    private  String postid , uid , username , profileImage , postImage , description , likes ;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }


    public Post() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public Post(String postid , String uid , String username, String profileImage, String postImage, String description, String likes) {
        this.postid = postid;
        this.uid = uid;
        this.username = username;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.description = description;
        this.likes = likes;
    }
}
