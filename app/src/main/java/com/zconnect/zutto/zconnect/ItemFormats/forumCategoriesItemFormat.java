package com.zconnect.zutto.zconnect.ItemFormats;

import android.provider.Contacts;

/**
 * Created by shubhamk on 9/2/17.
 */

public class forumCategoriesItemFormat {
    private String name;
    private String tabUID;
    private String catUID;


    public forumCategoriesItemFormat(String name, String catUID, String tabUID) {
        this.name = name;
        this.tabUID = tabUID;
        this.catUID = catUID;
    }

    public forumCategoriesItemFormat() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTabUID() {
        return tabUID;
    }

    public void setTabUID(String tabUID) {
        this.tabUID = tabUID;
    }

    public String getCatUID() {
        return catUID;
    }

    public void setCatUID(String catUID) {
        this.catUID = catUID;
    }

}


