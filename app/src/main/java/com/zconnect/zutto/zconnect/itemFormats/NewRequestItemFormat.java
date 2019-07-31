package com.zconnect.zutto.zconnect.itemFormats;

public class NewRequestItemFormat {

    private String Name,key,Type,imageurl,thumbnail;
    private Long PostTimeMillis;
    private PostedByDetails PostedBy;

    private String link;

    public NewRequestItemFormat()
    {

    }

    public NewRequestItemFormat(String locationName,String imageurl,String thumbnail,String key,String type,Long postTimeMillis,PostedByDetails postedBy, String link)
    {
        this.Name = locationName;
        this.key = key;
        this.PostTimeMillis = postTimeMillis;
        this.PostedBy = postedBy;
        this.Type=type;
        this.imageurl = imageurl;
        this.thumbnail = thumbnail;
        this.link = link;
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

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
