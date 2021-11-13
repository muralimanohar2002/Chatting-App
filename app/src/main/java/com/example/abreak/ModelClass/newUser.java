package com.example.abreak.ModelClass;

public class newUser {

    public newUser(){
        
    }
    private String uid, name, profilePicture, phoneNum;

    public newUser(String uid, String name, String profilePicture, String phoneNum) {
        this.uid = uid;
        this.name = name;
        this.profilePicture = profilePicture;
        this.phoneNum = phoneNum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
