package com.zconnect.zutto.zconnect.ItemFormats;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

/**
 * Created by Lokesh Garg on 08-02-2017.
 */

public class Event implements Serializable {


    private double lon = 0, lat = 0, BoostCount;
    private long EventTimeMillis;
    private String EventName,
            EventDescription,
            EventImage,
            EventDate,
            FormatDate,
            Key,
            Venue,
            Boosters,
            UserId,
            Verified;

    private PostedByDetails PostedBy;
    public Event() {

    }

    public Event(double lon, double lat, String eventName, String eventDescription, String eventImage, String eventDate, Long eventTimeMillis, String formatDate, String key, String venue, String boosters, String userid, double boostcount,String verified, PostedByDetails postedBy) {
        this.lon = lon;
        this.lat = lat;
        EventName = eventName;
        EventDescription = eventDescription;
        EventImage = eventImage;
        EventDate = eventDate;
        EventTimeMillis = eventTimeMillis;
        FormatDate = formatDate;
        Key = key;
        Venue = venue;
        Boosters = boosters;
        UserId = userid;
        BoostCount = boostcount;
        Verified = verified;
        PostedBy = postedBy;
    }

    public String getVerified(){
        return Verified;
    }

    public double getBoostCount() {
        return BoostCount;
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

    public Long getEventTimeMillis() {
        return EventTimeMillis;
    }

    public String getFormatDate() {
        return FormatDate;
    }

    public String getKey() {
        return Key;
    }

    public String getEventName() {
        Log.d("QQQ Eventname", EventName);
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

    public void setVerified(String verified) {
        this.Verified = verified;
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

    public String getUserID() {
        return UserId;
    }

    public PostedByDetails getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(PostedByDetails postedBy) {
        PostedBy = postedBy;
    }
//
//    public PostedByDetails getPostedBy() {
//        Log.d("QQQ username", PostedBy.getUsername());
//        return PostedBy; }
//    public String getPostedByImageThumb() { return PostedBy.getImageThumb(); }


}
