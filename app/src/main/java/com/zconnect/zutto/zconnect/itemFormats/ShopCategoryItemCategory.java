package com.zconnect.zutto.zconnect.itemFormats;

/**
 * Created by shubhamk on 8/2/17.
 */

public class ShopCategoryItemCategory {
    String imageurl;
    String category;

    public ShopCategoryItemCategory(String imageurl, String category) {
        this.imageurl = imageurl;
        this.category = category;
    }

    public ShopCategoryItemCategory() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
