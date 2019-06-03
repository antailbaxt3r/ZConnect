package com.zconnect.zutto.zconnect.itemFormats;


import org.joda.time.DateTime;

import java.util.HashMap;

public class NotificationsModel {

    String title;
    String desc;
    DateTime date;
    boolean seen;
    int type;
    HashMap<String, String> metadata;
    String key;

    public NotificationsModel(String title, String desc, DateTime date, int type, boolean seen, HashMap<String, String> metadata, String key) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.type = type;
        this.seen = seen;
        this.metadata = metadata;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
