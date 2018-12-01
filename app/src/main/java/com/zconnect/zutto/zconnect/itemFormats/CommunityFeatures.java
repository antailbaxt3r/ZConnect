package com.zconnect.zutto.zconnect.itemFormats;

public class CommunityFeatures {

    private String storeroom,events,links,cabpool,notices;

    public CommunityFeatures(String storeroom, String events, String links, String cabpool, String notices) {
        this.storeroom = storeroom;
        this.events = events;
        this.links = links;
        this.cabpool = cabpool;
        this.notices = notices;
    }

    public CommunityFeatures() {
    }

    public String getStoreroom() {
        return storeroom;
    }

    public void setStoreroom(String storeroom) {
        this.storeroom = storeroom;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getCabpool() {
        return cabpool;
    }

    public void setCabpool(String cabpool) {
        this.cabpool = cabpool;
    }

    public String getNotices() {
        return notices;
    }

    public void setNotices(String notices) {
        this.notices = notices;
    }
}
