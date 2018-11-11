package com.zconnect.zutto.zconnect.itemFormats;

import android.net.Uri;

import java.net.URL;

public class NoticeItemFormat {
    private Uri imageurl;
    private String name;

    public NoticeItemFormat(){

    }

    public android.net.Uri getImageurl() {
        return imageurl;
    }

    public void setImageurl(Uri imageurl) {
        this.imageurl = imageurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NoticeItemFormat(Uri imageurl, String name) {

        this.imageurl=imageurl;
        this.name=name;


    }

}
