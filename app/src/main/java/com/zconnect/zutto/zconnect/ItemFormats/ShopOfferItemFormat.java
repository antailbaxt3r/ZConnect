package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 9/4/17.
 */

public class ShopOfferItemFormat {
    String name;
    String desc;
    String image;
    String key;

    public ShopOfferItemFormat(String name, String desc, String image, String key) {
        this.name = name;
        this.desc = desc;
        this.image = image;
        this.key = key;
    }

    public ShopOfferItemFormat() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getkey() {
        return key;
    }

    public void setkey(String key) {
        this.key = key;
    }
}
