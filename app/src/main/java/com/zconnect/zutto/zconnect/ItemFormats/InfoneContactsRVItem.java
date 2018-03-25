package com.zconnect.zutto.zconnect.ItemFormats;

import java.util.ArrayList;

/**
 * Created by tanmay on 24/3/18.
 */

public class InfoneContactsRVItem {

    private String name;
    private String views;
    private String imageThumb;
    private ArrayList<String> phoneNums;
    private String infoneUserId;

    public InfoneContactsRVItem(String name, String views, String imageThumb, ArrayList<String> phoneNums, String infoneUserId) {
        this.name = name;
        this.views = views;
        this.imageThumb = imageThumb;
        this.phoneNums = phoneNums;
        this.infoneUserId = infoneUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }

    public ArrayList<String> getPhoneNums() {
        return phoneNums;
    }

    public void setPhoneNums(ArrayList<String> phoneNums) {
        this.phoneNums = phoneNums;
    }

    public String getInfoneUserId() {
        return infoneUserId;
    }

    public void setInfoneUserId(String infoneUserId) {
        this.infoneUserId = infoneUserId;
    }
}
