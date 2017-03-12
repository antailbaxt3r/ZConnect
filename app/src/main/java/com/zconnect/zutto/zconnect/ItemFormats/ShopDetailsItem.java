package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 8/2/17.
 */

public class ShopDetailsItem {
    String name;
    String number;
    String imageurl;
    String lat;
    String details;
    String lon;
    String menuurl;

    public ShopDetailsItem(String name, String imageurl, String number, String lat, String details, String lon, String menuurl) {
        this.name = name;
        this.imageurl = imageurl;
        this.number = number;
        this.lat = lat;
        this.details = details;
        this.lon = lon;
        this.menuurl = menuurl;
    }

    public ShopDetailsItem() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getMenuurl() {
        return menuurl;
    }

    public void setMenuurl(String menuurl) {
        this.menuurl = menuurl;
    }

    public String getLon() {

        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getDetails() {

        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLat() {

        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getNumber() {

        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
