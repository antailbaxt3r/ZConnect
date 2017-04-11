package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 9/4/17.
 */

public class ShopOfferItemFormat {
    String name;
    String desc;
    String image;
    String ShopKey;

    public ShopOfferItemFormat(String name, String desc, String image, String ShopKey) {
        name = name;
        desc = desc;
        image = image;
        this.ShopKey = ShopKey;
    }

    public ShopOfferItemFormat() {

    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        name = name;
    }

    public String getdesc() {
        return desc;
    }

    public void setdesc(String desc) {
        desc = desc;
    }

    public String getimage() {
        return image;
    }

    public void setimage(String image) {
        image = image;
    }

    public String getShopKey() {
        return ShopKey;
    }

    public void setShopKey(String ShopKey) {
        this.ShopKey = ShopKey;
    }
}
