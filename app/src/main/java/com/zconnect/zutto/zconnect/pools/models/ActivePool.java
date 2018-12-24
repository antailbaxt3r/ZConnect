package com.zconnect.zutto.zconnect.pools.models;

import android.os.Bundle;

import com.google.firebase.database.Exclude;

public class ActivePool {


    // static variables
    public static final String ACTIVE_POOLS = "activePools";
    public static final String ACTIVE_POOL = "activePool";
    public static final String URL_ACTIVE_POOL = "communities/" +
            "%s/features/pools/activePools";


    // nodes name
    public static final String NAME = "name";
    public static final String IMAGE_URL = "imageURL";
    public static final String OFFER = "offer";
    public static final String DELIVERY_TIME = "deliveryTime";
    public static final String EXPIRY_TIME = "expiryTime";
    public static final String JOINED = "joined";
    public static final String SHOP_ID = "shopID";
    public static final String CATEGORY_ID = "categoryID";


    @Exclude
    private String ID;
    private String name;
    private String joined;
    private String imageURL;
    private String deliveryTime;
    private String expiryTime;
    private String offer;
    private String shopID;
    private String categoryID;

    public ActivePool() {
    }

    public static ActivePool getPool(Bundle b) {
        ActivePool pool = new ActivePool();
        if (b.containsKey("poolID"))
            pool.setID(b.getString("poolID"));
        if (b.containsKey(NAME))
            pool.setName(b.getString(NAME));
        if (b.containsKey(JOINED))
            pool.setJoined(b.getString(JOINED));
        if (b.containsKey(IMAGE_URL))
            pool.setImageURL(b.getString(IMAGE_URL));
        if (b.containsKey(DELIVERY_TIME))
            pool.setDeliveryTime(b.getString(DELIVERY_TIME));
        if (b.containsKey(EXPIRY_TIME))
            pool.setExpiryTime(b.getString(EXPIRY_TIME));
        if (b.containsKey(OFFER))
            pool.setOffer(b.getString(OFFER));
        if (b.containsKey(SHOP_ID))
            pool.setShopID(b.getString(SHOP_ID));
        if (b.containsKey(CATEGORY_ID))
            pool.setCategoryID(b.getString(CATEGORY_ID));

        return pool;
    }

    public static ActivePool dummyValues() {
        ActivePool d = new ActivePool();
        d.setName("Tuesday Treat");
        d.setDeliveryTime("Tues 8:45 PM");
        d.setOffer("5% off on 3 orders");
        d.setJoined("5");
        d.setImageURL("https://dummyimage.com/120x120/736573/fff.png&text=Logo");
        return d;
    }

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

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getImageURL() {
        if (imageURL == null) return "";
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

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
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

    public Bundle getBundle() {
        Bundle b = new Bundle();
        b.putString("poolID", this.ID);
        b.putString(NAME, this.name);
        b.putString(JOINED, this.joined);
        b.putString(IMAGE_URL, this.imageURL);
        b.putString(DELIVERY_TIME, this.deliveryTime);
        b.putString(EXPIRY_TIME, this.expiryTime);
        b.putString(OFFER, this.offer);
        b.putString(SHOP_ID, this.shopID);
        b.putString(CATEGORY_ID, this.categoryID);

        return b;
    }
}
