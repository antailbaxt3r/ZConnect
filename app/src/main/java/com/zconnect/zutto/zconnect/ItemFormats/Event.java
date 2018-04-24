package com.zconnect.zutto.zconnect.ItemFormats;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

/**
 * Created by Lokesh Garg on 08-02-2017.
 */

public class Event implements Serializable {


    private double lon = 0, lat = 0, BoostCount;
    private long EventTimeMillis, PostTimeMillis;
    private String EventName,
            EventDescription,
            EventImage,
            EventDate,
            FormatDate,
            Key,
            Venue,
            UserID,
            Verified;

    private PostedByDetails PostedBy;
    public Event() {

    }

    public Event(double lon, double lat, String EventName, String EventDescription, String EventImage, String EventDate, Long EventTimeMillis, String FormatDate, String Key, String Venue, String UserID, double BoostCount,String Verified, PostedByDetails PostedBy, long PostTimeMillis) {
        this.lon = lon;
        this.lat = lat;
        this.EventName = EventName;
        this.EventDescription = EventDescription;
        this.EventImage = EventImage;
        this.EventDate = EventDate;
        this.EventTimeMillis = EventTimeMillis;
        this.FormatDate = FormatDate;
        this.Key = Key;
        this.Venue = Venue;
        this.UserID = UserID;
        this.BoostCount = BoostCount;
        this.Verified = Verified;
        this.PostedBy = PostedBy;
        this.PostTimeMillis = PostTimeMillis;
    }

    public String getVerified(){
        return Verified;
    }

    public double getBoostCount() {
        return BoostCount;
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
        return UserID;
    }

    public PostedByDetails getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(PostedByDetails postedBy) {
        PostedBy = postedBy;
    }

    public long getPostTimeMillis() { return PostTimeMillis; }
//
//    public PostedByDetails getPostedBy() {
//        Log.d("QQQ username", PostedBy.getUsername());
//        return PostedBy; }
//    public String getPostedByImageThumb() { return PostedBy.getImageThumb(); }


}
