package com.zconnect.zutto.zconnect.ItemFormats;

import java.util.ArrayList;

/**
 * Created by shubhamk on 23/3/18.
 */

public class InfoneTabs {
    String title;
    ArrayList<InfoneCategories> infoneCategories;

    public InfoneTabs(String title, ArrayList<InfoneCategories> infoneCategories) {
        this.title = title;
        this.infoneCategories = infoneCategories;
    }

    public InfoneTabs() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<InfoneCategories> getInfoneCategories() {
        return infoneCategories;
    }

    public void setInfoneCategories(ArrayList<InfoneCategories> infoneCategories) {
        this.infoneCategories = infoneCategories;
    }
}
