package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by Lokesh Garg on 31-03-2017.
 */

public class GalleryFormat {
    String ImageName;
    String ImageUrl;

    public GalleryFormat(String ImageName, String ImageUrl) {
        this.ImageName = ImageName;
        this.ImageUrl = ImageUrl;
    }

    public GalleryFormat() {

    }

    public String getImage() {
        return ImageUrl;
    }

    public void setImage(String imageurl) {
        this.ImageUrl = imageurl;
    }

    public String getTitle() {
        return ImageName;
    }

    public void setTitle(String name) {
        this.ImageName = name;
    }
}
