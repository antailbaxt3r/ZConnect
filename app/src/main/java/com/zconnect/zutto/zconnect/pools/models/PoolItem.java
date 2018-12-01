package com.zconnect.zutto.zconnect.pools.models;

import com.google.firebase.database.Exclude;

public class PoolItem {

    // URLS
    public static final String URL_POOL_ITEM = "communities/%s/shopOwner/%s/pools/poolDetails/%s";
    //                                                    communityID ,   shopID ,    poolID


    //Node names
    private static final String NAME = "name";
    private static final String PRICE = "price";
    private static final String DESCRIPTION = "description";
    private static final String IMAGE_URL = "imageURL";
    private static final String QUANTITY = "quantity";

    @Exclude
    private String ID;
    private String name;
    private int price;
    private String description;
    private String imageURL;
    private int quantity;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
