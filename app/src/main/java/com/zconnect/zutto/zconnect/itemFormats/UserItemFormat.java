package com.zconnect.zutto.zconnect.itemFormats;

import android.text.BoringLayout;

/**
 * Created by Lokesh Garg on 29-03-2018.
 */

public class UserItemFormat {

    private String username, email,mobileNumber,whatsAppNumber, about,imageURL,imageURLThumbnail,infoneType,skillTags,userUID,userType;

    public String getAnonymousUsername() {
        if (anonymousUsername!=null) {
            return anonymousUsername;
        }else {
            return "Anonymous";
        }
    }

    public void setAnonymousUsername(String anonymousUsername) {
        this.anonymousUsername = anonymousUsername;
    }

    private String anonymousUsername;

    Boolean contactHidden;
    public UserItemFormat(){

    }

    public  UserItemFormat(String username, String email, String mobileNumber, String whatsAppNumber, String about, String imageURL, String imageURLThumbnail, String infoneType, String skillTags, String userUID, String userType, Boolean contactHidden,String forumUsername){
        this.username = username;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.whatsAppNumber= whatsAppNumber;
        this.about=about;
        this.imageURL= imageURL;
        this.imageURLThumbnail = imageURLThumbnail;
        this.infoneType = infoneType;
        this.skillTags = skillTags;
        this.userUID = userUID;
        this.userType = userType;
        this.contactHidden = contactHidden;
        this.anonymousUsername = forumUsername;
    }



    public String getUsername() {
        if (username!=null)
        {
            return username;
        } else {
            return " ";
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getWhatsAppNumber() {
        return whatsAppNumber;
    }

    public void setWhatsAppNumber(String whatsAppNumber) {
        this.whatsAppNumber = whatsAppNumber;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURLThumbnail() {
        return imageURLThumbnail;
    }

    public void setImageURLThumbnail(String imageURLThumbnail) {
        this.imageURLThumbnail = imageURLThumbnail;
    }

    public String getInfoneType() {
        return infoneType;
    }

    public void setInfoneType(String infoneType) {
        this.infoneType = infoneType;
    }

    public String getSkillTags() {
        return skillTags;
    }

    public void setSkillTags(String skillTags) {
        this.skillTags = skillTags;
    }

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


    public Boolean getContactHidden() {
        return contactHidden;
    }

    public void setContactHidden(Boolean contactHidden) {
        this.contactHidden = contactHidden;
    }
}

