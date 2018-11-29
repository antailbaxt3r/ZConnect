package com.zconnect.zutto.zconnect.pools.models;

import android.os.Bundle;

import com.google.firebase.database.Exclude;

public class UpcomingPool {

    public static final String URL_UPCOMING_POOL = "communities/"+
            "%s/features/pools/upcomingPools";


    // nodes name
    public static final String NAME = "name";
    public static final String IMAGE_URL = "imageURL";
    public static final String OFFER = "offer";
    public static final String DELIVERY_TIME = "deliveryTime";
    public static final String EXPIRY_TIME = "expiryTime";
    public static final String UP_VOTE = "upVote";
    public static final String SHOP_ID = "shopID";
    public static final String CATEGORY_ID = "categoryID";


    @Exclude
    private String ID;
    private String name;
    private String upVote;
    private String imageURL;
    private String deliveryTime;
    private String offer;
    private String shopID;
    private String categoryID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        if(imageURL==null)return "";
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getUpVote() {
        return upVote;
    }

    public void setUpVote(String upVote) {
        this.upVote = upVote;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public static UpcomingPool dummyValues() {
        UpcomingPool d = new UpcomingPool();
        d.setName("Tuesday Treat");
        d.setDeliveryTime("Tues 8:45 PM");

        d.setUpVote("5");
        d.setImageURL("https://dummyimage.com/120x120/736573/fff.png&text=Logo");
        return d;
    }

    public static UpcomingPool getPool(Bundle b){
        UpcomingPool pool = new UpcomingPool();
        if(b.containsKey("poolID"))
            pool.setID(b.getString("poolID"));
        if(b.containsKey(NAME))
            pool.setName(b.getString(NAME));
        if(b.containsKey(UP_VOTE))
            pool.setUpVote(b.getString(UP_VOTE));
        if(b.containsKey(IMAGE_URL))
            pool.setImageURL(b.getString(IMAGE_URL));
        if(b.containsKey(DELIVERY_TIME))
            pool.setDeliveryTime(b.getString(DELIVERY_TIME));
        if(b.containsKey(OFFER))
            pool.setOffer(b.getString(OFFER));
        if(b.containsKey(SHOP_ID))
            pool.setShopID(b.getString(SHOP_ID));
        if(b.containsKey(CATEGORY_ID))
            pool.setCategoryID(b.getString(CATEGORY_ID));

        return pool;
    }
    public Bundle getBundle() {
        Bundle b = new Bundle();
        b.putString("poolID", this.ID);
        b.putString(NAME, this.name);
        b.putString(IMAGE_URL, this.imageURL);
        b.putString(OFFER, this.offer);
        b.putString(UP_VOTE, this.upVote);
        b.putString(DELIVERY_TIME, this.deliveryTime);
        b.putString(SHOP_ID, this.shopID);
        b.putString(CATEGORY_ID,this.categoryID);

        return b;
    }
}
