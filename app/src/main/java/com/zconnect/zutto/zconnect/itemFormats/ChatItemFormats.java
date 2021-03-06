package com.zconnect.zutto.zconnect.itemFormats;

import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;

/**
 * Created by f390 on 29/12/17.
 */

public class ChatItemFormats {

    private long timeDate = 0;
    private String uuid,name,message, imageThumb;
    private String messageType;
    private String photoURL;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    private String key;

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
        if (messageType!=null){
            return messageType;
        }else {
            return MessageTypeUtilities.KEY_ANONYMOUS_MESSAGE_STR;
        }

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
