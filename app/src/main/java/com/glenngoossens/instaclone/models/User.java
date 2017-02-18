package com.glenngoossens.instaclone.models;

/**
 * Created by Glenn on 18-2-2017.
 */

public class User {
    private String email;
    private String uid;

    public User(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
