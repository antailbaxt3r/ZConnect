package com.zconnect.zutto.zconnect.pools.models;

import com.google.firebase.database.Exclude;

public class PoolItem {

    // URLS
    public static final String URL_POOL_ITEM = "shops/shopDetails/%s/pools/poolDetails/%s";
    //                                                    communityID ,   shopID ,    poolID
    public static final String ITEM_ID = "itemID";


    //Node names
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE_URL = "imageURL";
    public static final String QUANTITY = "quantity";

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
        if (quantity == 0) return 0;
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
