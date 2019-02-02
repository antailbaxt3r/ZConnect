package com.zconnect.zutto.zconnect.pools.models;

import com.zconnect.zutto.zconnect.itemFormats.PostedByDetails;

import java.util.Vector;

public class ShopInfo {

    //URLS
    public static final String URL_SHOP_INFO = "shops/shopDetails/%s/info";

    //Node names
    public static final String NAME = "name";
    public static final String COMMUNITY_ID = "communityID";
    public static final String ADDRESS = "address";
    public static final String NUMBER = "number";
    public static final String EMAIL = "email";
    public static final String OWNER_DETAILS = "ownerDetails";
    public static final String USERS = "users";

    private String name;
    private String communityID;
    private String address;
    private String number;
    private String email;
    private PostedByDetails ownerDetails;
    private Vector<ShopUserInfo> users;


    public ShopInfo() {
    }

    public ShopInfo(String name, String communityID, String address, String number, String email, PostedByDetails ownerDetails, Vector<ShopUserInfo> users) {
        this.name = name;
        this.communityID = communityID;
        this.address = address;
        this.number = number;
        this.email = email;
        this.ownerDetails = ownerDetails;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PostedByDetails getOwnerDetails() {
        return ownerDetails;
    }

    public void setOwnerDetails(PostedByDetails ownerDetails) {
        this.ownerDetails = ownerDetails;
    }

    public Vector<ShopUserInfo> getUsers() {
        return users;
    }

    public void setUsers(Vector<ShopUserInfo> users) {
        this.users = users;
    }
}
