package com.zconnect.zutto.zconnect.itemFormats;

import java.util.ArrayList;

/**
 * Created by tanmay on 24/3/18.
 */

public class InfoneContactsRVItem {

    private String name;
    private String views;
    private String imageThumb;
    private Boolean contactHidden;
    private ArrayList<String> phoneNums;
    private String infoneUserId;
    private String desc;
    public boolean isChecked;
    public InfoneContactsRVItem(String name, String views, String imageThumb, ArrayList<String> phoneNums, String infoneUserId, Boolean contactHidden, String desc) {
        this.name = name;
        this.views = views;
        this.imageThumb = imageThumb;
        this.phoneNums = phoneNums;
        this.infoneUserId = infoneUserId;
        this.contactHidden = contactHidden;
        this.desc = desc;
    }

    public InfoneContactsRVItem(){

    }

    public String getDesc() { return desc; }

    public void setDesc(String desc) { this.desc = desc; }

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

    public Boolean getContactHidden() {
        return contactHidden;
    }

    public void setContactHidden(Boolean contactHidden) {
        this.contactHidden = contactHidden;
    }
}
