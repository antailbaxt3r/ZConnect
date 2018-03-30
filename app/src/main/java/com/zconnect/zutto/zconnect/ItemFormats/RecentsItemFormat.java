package com.zconnect.zutto.zconnect.ItemFormats;

import java.security.Key;

/**
 * Created by shubhamk on 20/3/17.
 */

public class RecentsItemFormat {
    String name;
    String desc;
    String desc2;
    String imageurl;
    String feature;
    String id;
    private String Key;
    private String DT;

    //new ui
    private long PostTimeMillis;
    //for infone
    String infoneContactName, infoneContactCategory,infoneContactCategoryName;
    //for cabpool
    String cabpoolSource;
    String cabpoolDestination;
    String cabpoolDate;
    String cabpoolTime;
        //for events
    String eventDate;
        //for storeroom
    String productPrice;
        //for new users
    String communityName;

    private PostedByDetails PostedBy;
    //

    public RecentsItemFormat(String name, String desc, String desc2, String imageurl, String feature, String id, String DT, String cabpoolSource, String cabpoolDestination, String cabpoolDate, String cabpoolTime, String eventDate, String productPrice, String Key, long PostTimeMillis, PostedByDetails PostedBy, String infoneContactCategory, String infoneContactName, String infoneContactCategoryName, String communityName) {
        this.name = name;
        this.desc = desc;
        this.desc2 = desc2;
        this.imageurl = imageurl;
        this.feature = feature;
        this.Key= Key;
        this.id = id;
        this.DT=DT;

        //new ui
        this.PostTimeMillis = PostTimeMillis;
        this.PostedBy = PostedBy;
        this.infoneContactName = infoneContactName;
        this.infoneContactCategory = infoneContactCategory;
        this.infoneContactCategoryName = infoneContactCategoryName;
        this.cabpoolSource = cabpoolSource;
        this.cabpoolDestination = cabpoolDestination;
        this.cabpoolDate = cabpoolDate;
        this.cabpoolTime = cabpoolTime;
        this.eventDate = eventDate;
        this.productPrice = productPrice;
        this.communityName = communityName;
    }

    public RecentsItemFormat() {

    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getFeature() {

        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getImageurl() {

        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDesc() {

        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDT() {
        return DT;
    }


    public void setDT(String DT) {
        this.DT = DT;
    }

    //new ui
    public long getPostTimeMillis() { return PostTimeMillis; }
        //for infone
    public String getInfoneContactName() { return infoneContactName; }
    public String getInfoneContactCategory() { return infoneContactCategory; }

    public String getInfoneContactCategoryName() {
        return infoneContactCategoryName;
    }

    public void setInfoneContactCategoryName(String infoneContactCategoryName) {
        this.infoneContactCategoryName = infoneContactCategoryName;
    }

        //for cabpool
    public String getCabpoolSource() { return cabpoolSource; }
    public String getCabpoolDestination() { return cabpoolDestination; }
    public String getCabpoolDate() { return cabpoolDate; }
    public String getCabpoolTime() { return cabpoolTime; }
        //for events
    public String getEventDate() { return  eventDate; }
        //for storeroom
    public String getProductPrice() { return productPrice; }
    //
    public String getKey() {
        return Key;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }

    public PostedByDetails getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(PostedByDetails postedBy) {
        PostedBy = postedBy;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

}

