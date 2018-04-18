package com.zconnect.zutto.zconnect.ItemFormats;

import android.provider.Contacts;

/**
 * Created by shubhamk on 9/2/17.
 */

public class forumCategoriesItemFormat {
    private String name;
    private String UID;
    private String tab;

    public forumCategoriesItemFormat(String name, String UID, String tab) {
        this.name = name;
        this.UID = UID;
        this.tab = tab;
    }

    public forumCategoriesItemFormat() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

}


