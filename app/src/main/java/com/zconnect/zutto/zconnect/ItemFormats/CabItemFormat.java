package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 26/7/17.
 */

public class CabItemFormat {
    String source;
    String destination;
    String date;
    String details;
    String time;

    public CabItemFormat(String source, String destination, String date, String details, String time) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.details = details;
        this.time = time;
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
}
