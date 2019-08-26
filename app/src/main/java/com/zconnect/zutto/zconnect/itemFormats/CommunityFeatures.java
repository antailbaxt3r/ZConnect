package com.zconnect.zutto.zconnect.itemFormats;

public class CommunityFeatures {

    private String storeroom,events,links,cabpool,notices,shops,internships;

    public CommunityFeatures(String storeroom, String events, String links, String cabpool, String notices, String shops,String internships) {
        this.storeroom = storeroom;
        this.events = events;
        this.links = links;
        this.cabpool = cabpool;
        this.notices = notices;
        this.shops = shops;
        this.internships = internships;
    }

    public CommunityFeatures() {
    }

    public String getStoreroom() {
        if(storeroom!=null) {
            return storeroom;
        }else return "false";
    }

    public void setStoreroom(String storeroom) {
        this.storeroom = storeroom;
    }

    public String getEvents() {
        if(events!=null) {
            return events;
        }else return "false";
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public String getLinks() {
        if(links!=null) {
            return links;
        }else return "false";
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getCabpool() {
        if(cabpool!=null) {
            return cabpool;
        }else {
            return "false";
        }
    }

    public void setCabpool(String cabpool) {
        this.cabpool = cabpool;
    }

    public String getNotices() {
        if(notices!=null) {
            return notices;
        }else return "false";
    }

    public void setNotices(String notices) {
        this.notices = notices;
    }

    public String getShops() {
        if(shops!=null) {
            return shops;
        }else return "false";
    }

    public void setShops(String shops) {
        this.shops = shops;
    }

    public String getInternships() {
        if(internships!=null) {
            return internships;
        }else return "false";
    }

    public void setInternships(String internships) {
        this.internships = internships;
    }
}
