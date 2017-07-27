package com.zconnect.zutto.zconnect.ItemFormats;

import java.util.ArrayList;

/**
 * Created by shubhamk on 26/7/17.
 */

public class CabItemFormat {
    String source;
    String destination;
    String date;
    String details;
    String time;
    String key;
    ArrayList<CabListItemFormat> cabListItemFormats;


    public CabItemFormat(String source, String destination, String date, String details, String time, String key, ArrayList<CabListItemFormat> cabListItemFormats) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.details = details;
        this.time = time;
        this.key = key;
        this.cabListItemFormats = cabListItemFormats;
    }

    public CabItemFormat() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<CabListItemFormat> getCabListItemFormats() {
        return cabListItemFormats;
    }

    public void setCabListItemFormats(ArrayList<CabListItemFormat> cabListItemFormats) {
        this.cabListItemFormats = cabListItemFormats;
    }
}
