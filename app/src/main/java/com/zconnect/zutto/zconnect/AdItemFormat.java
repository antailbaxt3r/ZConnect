package com.zconnect.zutto.zconnect;

/**
 * Created by shubhamk on 10/2/17.
 */

public class AdItemFormat {
    String imageurl;

    public AdItemFormat(String imageurl) {
        this.imageurl = imageurl;
    }

    public AdItemFormat() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
