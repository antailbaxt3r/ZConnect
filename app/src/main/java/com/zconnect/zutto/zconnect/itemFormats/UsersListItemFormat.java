package com.zconnect.zutto.zconnect.itemFormats;

import android.content.Intent;

/**
 * Created by shubhamk on 27/7/17.
 */

public class UsersListItemFormat {
    private String name;
    private String phonenumber;
    private String imageThumb;
    private String userUID;
    private int count;
    private String userType;

    public UsersListItemFormat(String name, String phonenumber, String imageThumb, String userUID, String userType, int count) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.imageThumb = this.imageThumb;
        this.userUID = userUID;
        this.count = count;
        this.userType = userType;
    }

    public UsersListItemFormat() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPhonenumber() { return phonenumber; }

    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; }

    public void setImageThumb(String imageThumb) { this.imageThumb = imageThumb; }

    public String getImageThumb() { return imageThumb; }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
