package com.zconnect.zutto.zconnect.itemFormats;

/**
 * Created by akhiller on 25/3/18.
 */

public class PostedByDetails {
    private String Username, ImageThumb, UID;

    public PostedByDetails() {

    }

    public PostedByDetails(String username, String imageThumb, String UID) {
        Username = username;
        ImageThumb = imageThumb;
        this.UID = UID;
    }

    public String getUsername() { return  Username; }
    public String getImageThumb() { return  ImageThumb; }
    public String getUID() { return UID; }
}
