package com.zconnect.zutto.zconnect;

/**
 * Created by Lokesh Garg on 09-02-2017.
 */

public class homeRecyclerClass {
    Double lon, lat;
    private String Title, Description, Url, multiUse2, multiUse1, Phone_no;
    private String type, Key, Venue;

    public homeRecyclerClass() {

    }


    public homeRecyclerClass(Double lon, Double lat, String title, String venue, String description, String url, String multiUse2, String multiUse1, String phnNo, String type, String key) {
        Title = title;
        Description = description;
        Url = url;
        this.multiUse2 = multiUse2;
        this.type = type;
        this.multiUse1 = multiUse1;
        Phone_no = phnNo;
        this.lon = lon;
        this.lat = lat;

        Key = key;
        Venue = venue;
    }

    public String getKey() {
        return Key;
    }

    public String getTitle() {
        return Title;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return Description;
    }

    public String getUrl() {
        return Url;
    }

    public String getmultiUse2() {
        return multiUse2;
    }

    public String getmultiUse1() {
        return multiUse1;
    }

    public String getPhone_no() {
        return Phone_no;
    }

    public Double getLon() {
        return lon;
    }

    public Double getLat() {
        return lat;
    }

    public String getVenue() {
        return Venue.length() == 0 ? "Venue N/A" : Venue;
    }
}

