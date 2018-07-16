package com.zconnect.zutto.zconnect.itemFormats;

/**
 * Created by Lokesh Garg on 02-04-2018.
 */

public class CabPoolLocationFormat {

    private String locationName;
    private Long PostTimeMillis;
    private PostedByDetails PostedBy;

    public CabPoolLocationFormat(){

    }

    public CabPoolLocationFormat(String locationName, Long PostTimeMillis, PostedByDetails PostedBy){
        this.locationName = locationName;
        this.PostTimeMillis = PostTimeMillis;
        this.PostedBy = PostedBy;
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

}
