package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by Lokesh Garg on 21-03-2017.
 */

public class Menu {

    String imageurl;

    public Menu(String imageurl) {
        this.imageurl = imageurl;
    }

    public Menu() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
