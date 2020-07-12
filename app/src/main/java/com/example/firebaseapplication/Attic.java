package com.example.firebaseapplication;

public class Attic {
    private String title, description, postImage, displayName, profilePhoto, time,date;

    public Attic(String title, String description, String postImage, String displayName, String profilePhoto,
                 String time, String date){
        this.title = title;
        this.description= description;
        this.postImage = postImage;
        this.displayName = displayName;
        this.profilePhoto = profilePhoto;
        this.time = time;
        this.date = date;
    }

    public Attic(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
