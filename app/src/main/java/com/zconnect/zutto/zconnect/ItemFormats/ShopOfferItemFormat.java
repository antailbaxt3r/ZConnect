package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 9/4/17.
 */

public class ShopOfferItemFormat {
    String Title;
    String Description;
    String ImageUrl;
    String shopId;

    public ShopOfferItemFormat(String title, String description, String imageUrl, String shopId) {
        Title = title;
        Description = description;
        ImageUrl = imageUrl;
        this.shopId = shopId;
    }

    public ShopOfferItemFormat() {

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
