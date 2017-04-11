package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by Lokesh Garg on 07-04-2017.
 */

public class OfferFormat {
    String Title, Description, ImageUrl, ShopNo;

    public OfferFormat() {

    }

    public OfferFormat(String Title, String Description, String ImageUrl, String ShopNo) {
        this.Title = Title;
        this.Description = Description;
        this.ImageUrl = ImageUrl;
        this.ShopNo = ShopNo;


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

    public String getShopNo() {
        return ShopNo;
    }

    public void setShopNo(String shopNo) {
        ShopNo = shopNo;
    }
}
