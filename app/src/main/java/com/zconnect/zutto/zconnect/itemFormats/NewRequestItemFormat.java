package com.zconnect.zutto.zconnect.itemFormats;

public class NewRequestItemFormat {

    private String locationName,key;
    private Long PostTimeMillis;
    private PostedByDetails PostedBy;

    public NewRequestItemFormat()
    {

    }

    public NewRequestItemFormat(String locationName,String key,Long postTimeMillis,PostedByDetails postedBy)
    {
        this.locationName = locationName;
        this.key = key;
        this.PostTimeMillis = postTimeMillis;
        this.PostedBy = postedBy;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Long getPostTimeMillis() {
        return PostTimeMillis;
    }

    public void setPostTimeMillis(Long postTimeMillis) {
        PostTimeMillis = postTimeMillis;
    }

    public PostedByDetails getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(PostedByDetails postedBy) {
        PostedBy = postedBy;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
