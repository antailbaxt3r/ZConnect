package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by tanmay on 24/3/18.
 */

public class InfoneCategoryModel {

    String name;
    String imageurl;
    String admin;
    String catId;
    String thumbnail;
    int totalContacts;

    public InfoneCategoryModel(){

    }

    public InfoneCategoryModel(String name, String imageurl, String admin, String catId, String thumbnail, int totalContacts) {
        this.name = name;
        this.imageurl = imageurl;
        this.admin = admin;
        this.catId=catId;
        this.thumbnail=thumbnail;
        this.totalContacts=totalContacts;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getTotalContacts() {
        return totalContacts;
    }

    public void setTotalContacts(int totalContacts) {
        this.totalContacts = totalContacts;
    }

}
