package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by tanmay on 24/3/18.
 */

public class Infone2CategoryModel {

    String name;
    String imageurl;
    String admin;
    String catId;

    public Infone2CategoryModel(){

    }

    public Infone2CategoryModel(String name, String imageurl, String admin,String catId) {
        this.name = name;
        this.imageurl = imageurl;
        this.admin = admin;
        this.catId=catId;
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
}
