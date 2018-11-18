package com.zconnect.zutto.zconnect.pools.models;

import com.google.firebase.database.Exclude;

public class PoolDish {

    @Exclude
    private String ID;
    private String name;
    private String description;
    private String imageURL;
    private String quantity;
    private String type;

    public static PoolDish dummyValues() {
        PoolDish d = new PoolDish();

        d.setImageURL("https://dummyimage.com/600x400/000/fff&text=Image");
        d.setName("Biryani");
        d.setQuantity("1");
        d.setDescription("Rs10 convince charge");

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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}