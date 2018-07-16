package com.zconnect.zutto.zconnect.itemFormats;

/**
 * Created by f390 on 29/12/17.
 */

public class ChatItemFormats {

    private long timeDate;
    private String uuid,name,message, imageThumb;
    private String messageType;
    private String photoURL;

    public ChatItemFormats(long timeDate, String uuid, String name, String message, String imageThumb, String messageType) {
        this.timeDate = timeDate;
        this.uuid = uuid;
        this.name = name;
        this.message = message;
        this.imageThumb = imageThumb;
        this.messageType =messageType;
    }

    public ChatItemFormats(String photoURL, long timeDate, String uuid, String name, String imageThumb, String messageType) {
        this.timeDate = timeDate;
        this.uuid = uuid;
        this.name = name;
        this.photoURL = photoURL;
        this.imageThumb = imageThumb;
        this.messageType =messageType;
    }

    public ChatItemFormats() {
    }

    public long getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(long timeDate) {
        this.timeDate = timeDate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
