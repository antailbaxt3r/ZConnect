package com.zconnect.zutto.zconnect.itemFormats;

/**
 * Created by Lokesh Garg on 31-03-2017.
 */

public class GalleryFormat {
    String imageurl;

    public GalleryFormat(String imageurl) {
        this.imageurl = imageurl;
    }

    public GalleryFormat() {

    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
