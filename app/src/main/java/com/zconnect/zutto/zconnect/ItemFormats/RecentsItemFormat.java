package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 20/3/17.
 */

public class RecentsItemFormat {
    String name;
    String desc;
    String desc2;
    String imageurl;
    String feature;
    String id;

    public RecentsItemFormat(String name, String desc, String desc2, String imageurl, String feature, String id) {
        this.name = name;
        this.desc = desc;
        this.desc2 = desc2;
        this.imageurl = imageurl;
        this.feature = feature;
        this.id = id;
    }

    public RecentsItemFormat() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getFeature() {

        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getImageurl() {

        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDesc() {

        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

