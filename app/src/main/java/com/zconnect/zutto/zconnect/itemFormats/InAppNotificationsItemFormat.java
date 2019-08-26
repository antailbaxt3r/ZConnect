package com.zconnect.zutto.zconnect.itemFormats;



import java.util.HashMap;

public class InAppNotificationsItemFormat {

    private String title;
    private String desc;
    private long PostTimeMillis;
    private HashMap<String,Boolean> seen;
    private String type;
    private HashMap<String, Object> metadata;
    private String key;
    private UserItemFormat notifiedBy;
    private String scope;
    public InAppNotificationsItemFormat()
    {}


    public InAppNotificationsItemFormat(String title, String desc, long PostTimeMillis, String type,UserItemFormat notifiedBy, String scope,HashMap<String,Boolean> seen, HashMap<String, Object> metadata, String key) {
        this.title = title;
        this.desc = desc;
        this.PostTimeMillis = PostTimeMillis;
        this.type = type;
        this.seen = seen;
        this.metadata = metadata;
        this.key = key;
        this.notifiedBy=notifiedBy;
        this.scope=scope;
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

    public long getPostTimeMillis() {
        return PostTimeMillis;
    }

    public void setPostTimeMillis(long postTimeMillis) {
        PostTimeMillis = postTimeMillis;
    }

    public HashMap<String, Boolean> isSeen() {
        return seen;
    }

    public void setSeen(HashMap<String, Boolean> seen) {
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

    public UserItemFormat getNotifiedBy() {
        return notifiedBy;
    }

    public void setNotifiedBy(UserItemFormat notifiedBy) {
        this.notifiedBy = notifiedBy;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
