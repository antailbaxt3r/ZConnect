package com.zconnect.zutto.zconnect.pools.models;

import com.google.firebase.database.Exclude;

public class ActivePool {

    @Exclude
    private String ID;
    private String name;
    private String joined;
    private String imageURL;
    private String deliveryTime;
    private String offer;

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

    public static ActivePool dummyValues() {
        ActivePool d = new ActivePool();
        d.setName("Tuesday Treat");
        d.setDeliveryTime("Tues 8:45 PM");
        d.setOffer("5% off on 3 orders");
        d.setJoined("5");
        d.setImageURL("https://dummyimage.com/120x120/736573/fff.png&text=Logo");
        return d;
    }
}
