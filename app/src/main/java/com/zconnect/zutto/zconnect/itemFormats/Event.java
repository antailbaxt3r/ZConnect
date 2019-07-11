package com.zconnect.zutto.zconnect.itemFormats;

import java.io.Serializable;

/**
 * Created by Lokesh Garg on 08-02-2017.
 */

public class Event implements Serializable {


    private double lon = 0, lat = 0, BoostCount;
    private long EventTimeMillis, PostTimeMillis;
    private String EventName;
    private String EventDescription;
    private String EventImage;
    private String EventDate;
    private String FormatDate;
    private String Key;
    private String eventForumID;
    private String Venue;
    private String UserID;
    private String Verified;
    private PostedByDetails PostedBy;
    
    public Event() {

    }

    public Event(double lon, double lat, String EventName, String EventDescription, String EventImage, String EventDate, Long EventTimeMillis, String FormatDate, String Key, String Venue, String UserID, double BoostCount, String Verified, PostedByDetails PostedBy, long PostTimeMillis, String eventForumID) {
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
        this.eventForumID = eventForumID;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getBoostCount() {
        return BoostCount;
    }

    public void setBoostCount(double boostCount) {
        BoostCount = boostCount;
    }

    public long getEventTimeMillis() {
        return EventTimeMillis;
    }

    public void setEventTimeMillis(long eventTimeMillis) {
        EventTimeMillis = eventTimeMillis;
    }

    public long getPostTimeMillis() {
        return PostTimeMillis;
    }

    public void setPostTimeMillis(long postTimeMillis) {
        PostTimeMillis = postTimeMillis;
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

    public void setEventImage(String eventImage) {
        EventImage = eventImage;
    }

    public String getEventDate() {
        return EventDate;
    }

    public void setEventDate(String eventDate) {
        EventDate = eventDate;
    }

    public String getFormatDate() {
        return FormatDate;
    }

    public void setFormatDate(String formatDate) {
        FormatDate = formatDate;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getVenue() {
        return Venue;
    }

    public void setVenue(String venue) {
        Venue = venue;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getVerified() {
        return Verified;
    }

    public void setVerified(String verified) {
        Verified = verified;
    }

    public PostedByDetails getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(PostedByDetails postedBy) {
        PostedBy = postedBy;
    }
    public String getEventForumID() {
        return eventForumID;
    }

    public void setEventForumID(String eventForumID) {
        this.eventForumID = eventForumID;
    }




}
