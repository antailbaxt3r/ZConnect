package com.zconnect.zutto.zconnect.itemFormats;

import java.util.Vector;

/**
 * Created by Lokesh Garg on 02-04-2018.
 */

public class CabPoolLocationFormat {

    private String locationUID;
    private String locationName;
    private Long PostTimeMillis;
    private PostedByDetails PostedBy;
    private Vector<UsersListItemFormat> usersListItemFormats;

    public CabPoolLocationFormat(){

    }

    public CabPoolLocationFormat(String locationName, Long PostTimeMillis, PostedByDetails PostedBy, Vector<UsersListItemFormat> usersListItemFormats){
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

    public String getLocationUID() {
        return locationUID;
    }

    public void setLocationUID(String locationUID) {
        this.locationUID = locationUID;
    }

}
