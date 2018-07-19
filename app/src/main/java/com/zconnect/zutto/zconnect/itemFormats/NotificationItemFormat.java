package com.zconnect.zutto.zconnect.itemFormats;

public class NotificationItemFormat {

    private String notificationIdentifier;
    private String communityName;
    private String userName;
    private String userImage;

    private String userMobileNumber;

    private String itemKey;
    private String itemName;



    public NotificationItemFormat(String notificationIdentifier){
        this.notificationIdentifier = notificationIdentifier;
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


}
