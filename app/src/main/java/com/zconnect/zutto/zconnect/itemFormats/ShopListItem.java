package com.zconnect.zutto.zconnect.itemFormats;

/**
 * Created by shubhamk on 8/2/17.
 */

public class ShopListItem {
    String imageurl;
    String name;
    ShopDetailsItem shopDetailsItem;

    public ShopListItem(String imageurl, String name, ShopDetailsItem shopDetailsItem) {
        this.imageurl = imageurl;
        this.name = name;
        this.shopDetailsItem = shopDetailsItem;
    }

    public ShopListItem() {
    }

    public String getName() {
        if (shopDetailsItem != null) return shopDetailsItem.getName();
        else return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageurl() {
        if (shopDetailsItem != null) return shopDetailsItem.getImageurl();
        else return "";
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public ShopDetailsItem getShopDetailsItem() {
        return shopDetailsItem;
    }

    public void setShopDetailsItem(ShopDetailsItem shopDetailsItem) {
        this.shopDetailsItem = shopDetailsItem;
    }
}
