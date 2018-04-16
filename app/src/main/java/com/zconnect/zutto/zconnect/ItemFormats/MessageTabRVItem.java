package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by tanmay on 25/3/18.
 */

public class MessageTabRVItem {

    private String sender,message,type,chatUID;
    private double timeStamp;

    public MessageTabRVItem(String sender, String message,double timeStamp, String type, String chatUID) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = timeStamp;
        this.type = type;
        this.chatUID = chatUID;
    }

    public  MessageTabRVItem(){}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(double timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChatUID() {
        return chatUID;
    }

    public void setChatUID(String chatUID) {
        this.chatUID = chatUID;
    }
}
