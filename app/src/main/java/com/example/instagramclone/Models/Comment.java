package com.example.instagramclone.Models;

public class Comment {
    private String username , mComment , profileImage;

    public Comment() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Comment(String username, String mComment, String profileImage) {
        this.username = username;
        this.mComment = mComment;
        this.profileImage = profileImage;
    }
}
