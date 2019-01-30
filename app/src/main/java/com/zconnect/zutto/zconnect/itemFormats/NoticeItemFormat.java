package com.zconnect.zutto.zconnect.itemFormats;

import android.net.Uri;

import java.net.URL;

public class NoticeItemFormat {
    private String key;
    private String imageURL;
    private String imageThumbURL;
    private String title;
    private PostedByDetails postedByDetails;
    private long postTimeMillis;
    private ExpiryDateItemFormat expiryDate;

    public NoticeItemFormat(){

    }

    public NoticeItemFormat(String key, String imageURL, String imageThumbURL, String title, PostedByDetails postedByDetails, long postTimeMillis, ExpiryDateItemFormat expiryDate) {
        this.key = key;
        this.imageURL = imageURL;
        this.imageThumbURL = imageThumbURL;
        this.title = title;
        this.postedByDetails = postedByDetails;
        this.postTimeMillis = postTimeMillis;
        this.expiryDate = expiryDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageThumbURL() {
        return imageThumbURL;
    }

    public void setImageThumbURL(String imageThumbURL) {
        this.imageThumbURL = imageThumbURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PostedByDetails getPostedByDetails() {
        return postedByDetails;
    }

    public void setPostedByDetails(PostedByDetails postedByDetails) {
        this.postedByDetails = postedByDetails;
    }

    public long getPostTimeMillis() {
        return postTimeMillis;
    }

    public void setPostTimeMillis(long postTimeMillis) {
        postTimeMillis = postTimeMillis;
    }

    public ExpiryDateItemFormat getExpiryDate() {
        return expiryDate;
    }
}
