package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 27/7/17.
 */

public class UsersListItemFormat {
    String name;
    String phonenumber;
    String imageThumb;
    String userUID;
    String userType;

    public UsersListItemFormat(String name, String phonenumber, String imageThumb, String userUID, String userType) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.imageThumb = this.imageThumb;
        this.userUID = userUID;
        this.userType = userType;
    }

    public UsersListItemFormat() {
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
