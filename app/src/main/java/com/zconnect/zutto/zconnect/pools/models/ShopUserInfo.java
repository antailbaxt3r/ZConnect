package com.zconnect.zutto.zconnect.pools.models;

public class ShopUserInfo {

    //URLS
    public static final String URL_SHOP_USER_INFO = "shops/shopUsers/info/%s";

    public static final String URL_SHOP_MEMBERS = "shops/shopDetails/%s/info/users/%s";

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String IMAGE_URL = "imageURL";
    public static final String IMAGE_THUMB = "imageThumb";
    public static final String SHOP_ID = "shopID";
    public static final String TYPE = "type";

    private String name;
    private String email;
    private String phoneNumber;
    private String imageURL;
    private String imageThumb;
    private String shopID;
    private String type;

    public ShopUserInfo() {

    }

    public ShopUserInfo(String name, String email, String phoneNumber, String imageURL, String imageThumb, String shopID, String type) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.imageURL = imageURL;
        this.imageThumb = imageThumb;
        this.shopID = shopID;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
