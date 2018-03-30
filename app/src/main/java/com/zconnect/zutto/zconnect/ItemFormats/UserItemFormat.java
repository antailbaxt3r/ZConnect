package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by Lokesh Garg on 29-03-2018.
 */

public class UserItemFormat {

    private String username, email,mobileNumber,whatsAppNumber, about,imageURL,imageURLThumbnail,infoneType,skillTags,userUID;

    public UserItemFormat(){

    }

    public  UserItemFormat(String username, String email, String mobileNumber, String whatsAppNumber, String about, String imageURL, String imageURLThumbnail, String infoneType, String skillTags, String userUID){
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
    }



    public String getUsername() {
        return username;
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

}

