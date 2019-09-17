package com.zconnect.zutto.zconnect.pools.models;

import java.io.Serializable;

public class PoolItem implements Serializable{

    // URLS
    public static final String URL_POOL_ITEM = "shops/shopDetails/%s/poolTemplates/poolDetails/%s";
    //                                                         shopID               poolID

    //Node names
    public static final String ITEM_ID = "itemID";
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE_URL = "imageURL";
    public static final String IMAGE_THUMB = "imageThumb";
    public static final String MAX_QUANTITY = "maxQuantity";
    public static final String QUANTITY = "quantity";

    private String itemID;
    private String name;
    private int price;
    private String description;
    private String imageURL;
    private String imageThumb;
    private int maxQuantity;
    private int quantity;
    private boolean visible;

    public PoolItem() {
    }

    public PoolItem(String itemID, String name, int price, String description, String imageURL, String imageThumb, int maxQuantity, int quantity, boolean visible) {
        this.itemID = itemID;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageURL = imageURL;
        this.imageThumb = imageThumb;
        this.maxQuantity = maxQuantity;
        this.quantity = quantity;
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
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

    public String getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
