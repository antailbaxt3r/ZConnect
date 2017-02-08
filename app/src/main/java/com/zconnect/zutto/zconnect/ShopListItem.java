package com.zconnect.zutto.zconnect;

/**
 * Created by shubhamk on 8/2/17.
 */

public class ShopListItem {
    String imageurl;
    ShopDetailsItem shopDetailsItem;

    public ShopListItem(String imageurl, ShopDetailsItem shopDetailsItem) {
        this.imageurl = imageurl;
        this.shopDetailsItem = shopDetailsItem;
    }

    public ShopListItem() {
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
