package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by f390 on 29/12/17.
 */

public class ChatItemFormats {

    private long timeDate;
    private String uuid,name,message;

    public ChatItemFormats(long timeDate, String uuid, String name, String message) {
        this.timeDate = timeDate;
        this.uuid = uuid;
        this.name = name;
        this.message = message;
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
}
