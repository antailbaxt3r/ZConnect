package com.zconnect.zutto.zconnect.itemFormats;

import java.util.HashMap;
import java.util.Vector;

public class CounterItemFormat {
    String userID, uniqueID;
    long timestamp;
    HashMap<String, String> meta = new HashMap<>();

    public CounterItemFormat() {

    }

    public CounterItemFormat(String userID, String uniqueID, long timestamp, HashMap<String, String> meta) {
        this.userID = userID;
        this.uniqueID = uniqueID;
        this.timestamp = timestamp;
        this.meta = meta;
    }

    public HashMap<String, String> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, String> meta) {
        this.meta = meta;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
