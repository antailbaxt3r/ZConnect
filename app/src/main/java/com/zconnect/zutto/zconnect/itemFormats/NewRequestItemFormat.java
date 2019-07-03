package com.zconnect.zutto.zconnect.itemFormats;

public class NewRequestItemFormat {

    private String Name,key,Type;
    private Long PostTimeMillis;
    private PostedByDetails PostedBy;

    public NewRequestItemFormat()
    {

    }

    public NewRequestItemFormat(String locationName,String key,String type,Long postTimeMillis,PostedByDetails postedBy)
    {
        this.Name = locationName;
        this.key = key;
        this.PostTimeMillis = postTimeMillis;
        this.PostedBy = postedBy;
        this.Type=type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String locationName) {
        this.Name = locationName;
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
