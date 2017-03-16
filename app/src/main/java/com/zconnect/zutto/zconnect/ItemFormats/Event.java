package com.zconnect.zutto.zconnect.ItemFormats;

import java.io.Serializable;

/**
 * Created by Lokesh Garg on 08-02-2017.
 */

public class Event implements Serializable {


    Double lon, lat;
    private String EventName,
            EventDescription,
            EventImage,
            EventDate,
            FormatDate,
            Key,
            Venue;
    public Event() {

    }

    public Event(String eventName, String eventDescription, String eventImage, String eventDate, String formatDate, String key, String venue, Double lon, Double lat) {
        EventName = eventName;
        EventDescription = eventDescription;
        EventImage = eventImage;
        EventDate = eventDate;
        FormatDate = formatDate;
        Key = key;
        Venue = venue;
        this.lon = lon;
        this.lat = lat;
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
        return Venue.length() == 0 ? "N/A" : Venue;
    }


    public Double getLon() {
        return lon;
    }

    public Double getLat() {
        return lat;
    }
}
