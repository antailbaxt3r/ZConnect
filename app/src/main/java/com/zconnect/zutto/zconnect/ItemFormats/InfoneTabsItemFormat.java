package com.zconnect.zutto.zconnect.ItemFormats;

import java.util.ArrayList;

/**
 * Created by shubhamk on 23/3/18.
 */

public class InfoneTabsItemFormat {
    private String name;
    private String UID;


    public InfoneTabsItemFormat(String name, String UID) {
        this.name = name;
        this.UID = UID;
    }

    public InfoneTabsItemFormat() {}

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

//    public ArrayList<InfoneCategories> getInfoneCategories() {
//        return infoneCategories;
//    }
//
//    public void setInfoneCategories(ArrayList<InfoneCategories> infoneCategories) {
//        this.infoneCategories = infoneCategories;
//    }
}
