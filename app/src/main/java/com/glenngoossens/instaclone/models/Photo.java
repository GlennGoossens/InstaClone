package com.glenngoossens.instaclone.models;

/**
 * Created by Glenn on 18-2-2017.
 */

public class Photo {

    private String username;
    private byte[] byterArray;

    public Photo(){
    }

    public String getUser() {
        return username;
    }

    public void setUser(String username) {
        this.username = username;
    }

    public byte[] getByterArray() {
        return byterArray;
    }

    public void setByterArray(byte[] byterArray) {
        this.byterArray = byterArray;
    }
}
