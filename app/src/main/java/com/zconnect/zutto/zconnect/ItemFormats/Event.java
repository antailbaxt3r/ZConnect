package com.zconnect.zutto.zconnect.ItemFormats;

import java.io.Serializable;

/**
 * Created by Lokesh Garg on 08-02-2017.
 */

public class Event implements Serializable {


    double lon = 0, lat = 0;
    private String EventName,
            EventDescription,
            EventImage,
            EventDate,
            FormatDate,
            Key,
            Venue,
            Boosters;
    public Event() {

    }

    public Event(double lon, double lat, String eventName, String eventDescription, String eventImage, String eventDate, String formatDate, String key, String venue, String boosters) {
        this.lon = lon;
        this.lat = lat;
        EventName = eventName;
        EventDescription = eventDescription;
        EventImage = eventImage;
        EventDate = eventDate;
        FormatDate = formatDate;
        Key = key;
        Venue = venue;
        Boosters = boosters;
    }

    public String getBoosters() {
        return Boosters;
    }

    public void setBoosters(String boosters) {
        Boosters = boosters;
    }

    public String getEventDate() {
        return EventDate;
    }

    public String getFormatDate() {
        return FormatDate;
    }

    public String getKey() {
        return Key;
    }

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public String getEventDescription() {
        return EventDescription;
    }

    public void setEventDescription(String eventDescription) {
        EventDescription = eventDescription;
    }

    public String getEventImage() {
        return EventImage;
    }

    public String getVenue() {
        return Venue.length() == 0 ? "Venue : N/A" : Venue;
    }


    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}
