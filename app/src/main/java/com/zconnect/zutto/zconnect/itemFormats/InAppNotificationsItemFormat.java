package com.zconnect.zutto.zconnect.itemFormats;


import org.joda.time.DateTime;

import java.util.HashMap;

public class InAppNotificationsItemFormat {

    private String title;
    private String desc;
    private long PostTimeMillis;
    private boolean seen;
    private String type;
    private HashMap<String, Object> metadata;
    private String key;

    public InAppNotificationsItemFormat(String title, String desc, long PostTimeMillis, String type, boolean seen, HashMap<String, Object> metadata, String key) {
        this.title = title;
        this.desc = desc;
        this.PostTimeMillis = PostTimeMillis;
        this.type = type;
        this.seen = seen;
        this.metadata = metadata;
        this.key = key;
    }

    public long getPostTimeMillis() {
        return PostTimeMillis;
    }

    public void setPostTimeMillis(long postTimeMillis) {
        PostTimeMillis = postTimeMillis;
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

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
