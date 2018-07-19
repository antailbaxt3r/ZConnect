package com.zconnect.zutto.zconnect.itemFormats;

public class NotificationItemFormat {

    private String notificationIdentifier;
    private String communityName;

    private String userKey;
    private String userName;
    private String userImage;
    private String userMobileNumber;

    private String itemKey;
    private String itemName;
    private String itemImage;
    private String itemLocation;
    private String itemPrice;

    private String itemCategory;
    private String itemCategoryUID;


    public NotificationItemFormat(String notificationIdentifier, String userKey){
        this.notificationIdentifier = notificationIdentifier;
        this.userKey = userKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNotificationIdentifier() {
        return notificationIdentifier;
    }

    public void setNotificationIdentifier(String notificationIdentifier) {
        this.notificationIdentifier = notificationIdentifier;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }


    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemCategoryUID() {
        return itemCategoryUID;
    }

    public void setItemCategoryUID(String itemCategoryUID) {
        this.itemCategoryUID = itemCategoryUID;
    }


}
