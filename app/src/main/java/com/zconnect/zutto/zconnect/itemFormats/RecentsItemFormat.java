package com.zconnect.zutto.zconnect.itemFormats;

/**
 * Created by shubhamk on 20/3/17.
 */

public class RecentsItemFormat {
    String name;
    String desc;
    String desc2;
    String imageurl;
    String feature;
    String id;
    private String Key;
    private String DT;

    String recentType;
    private String postID;

    //new ui
    private long PostTimeMillis;
    //for infone
    String infoneContactName, infoneContactCategoryName;
    //for cabpool
    String cabpoolSource;
    String cabpoolDestination;
    String cabpoolDate;
    String cabpoolTime;
    int cabpoolTimeTo = -1;
    int cabpoolTimeFrom = -1;
    //for events
    String eventDate;
    //for storeroom
    String productPrice;
    String productType;
    //for new users
    String communityName;

    //for messages
    String message;
    int msgLikes;
    int msgComments;

    //for Forums


    private PostedByDetails PostedBy;
    //

    public RecentsItemFormat(String name, String desc, String desc2, String imageurl, String feature, String id,
                             String DT, String cabpoolSource, String cabpoolDestination, String cabpoolDate,
                             String cabpoolTime, int cabpoolTimeFrom, int cabpoolTimeTo, String eventDate, String productPrice, String Key, long PostTimeMillis,
                             PostedByDetails PostedBy, String infoneContactName, String infoneContactCategoryName,
                             String communityName, String message, String recentType, int msgLikes, int msgComments, String productType) {
        this.name = name;
        this.desc = desc;
        this.desc2 = desc2;
        this.imageurl = imageurl;
        this.feature = feature;
        this.Key = Key;
        this.id = id;
        this.DT = DT;

        //new ui
        this.PostTimeMillis = PostTimeMillis;
        this.PostedBy = PostedBy;
        this.infoneContactName = infoneContactName;
        this.infoneContactCategoryName = infoneContactCategoryName;
        this.cabpoolSource = cabpoolSource;
        this.cabpoolDestination = cabpoolDestination;
        this.cabpoolDate = cabpoolDate;
        this.cabpoolTime = cabpoolTime;
        this.cabpoolTimeFrom = cabpoolTimeFrom;
        this.cabpoolTimeTo = cabpoolTimeTo;
        this.eventDate = eventDate;
        this.productPrice = productPrice;
        this.communityName = communityName;
        this.message = message;
        this.msgLikes = msgLikes;
        this.recentType = recentType;
        this.msgComments = msgComments;
        this.productType = productType;
    }

    public RecentsItemFormat() {

    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getFeature() {

        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getImageurl() {

        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDesc() {

        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDT() {
        return DT;
    }


    public void setDT(String DT) {
        this.DT = DT;
    }

    //new ui
    public long getPostTimeMillis() {
        return PostTimeMillis;
    }

    //for infone
    public String getInfoneContactName() {
        return infoneContactName;
    }

    public String getInfoneContactCategoryName() {
        return infoneContactCategoryName;
    }

    public void setInfoneContactCategoryName(String infoneContactCategoryName) {
        this.infoneContactCategoryName = infoneContactCategoryName;
    }

    //for cabpool
    public String getCabpoolSource() {
        return cabpoolSource;
    }

    public String getCabpoolDestination() {
        return cabpoolDestination;
    }

    public String getCabpoolDate() {
        return cabpoolDate;
    }

    public String getCabpoolTime() {
        return cabpoolTime;
    }

    public int getCabpoolTimeFrom() {
        return cabpoolTimeFrom;
    }

    public int getCabpoolTimeTo() {
        return cabpoolTimeTo;
    }

    //for events
    public String getEventDate() {
        return eventDate;
    }

    //for storeroom
    public String getProductPrice() {
        return productPrice;
    }

    //
    public String getKey() {
        return Key;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }

    public PostedByDetails getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(PostedByDetails postedBy) {
        PostedBy = postedBy;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getRecentType() {
        return recentType;
    }

    public void setRecentType(String recentType) {
        this.recentType = recentType;
    }

    public int getMsgComments() {
        return msgComments;
    }

    public void setMsgComments(int msgComments) {
        this.msgComments = msgComments;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductType() {
        return productType;
    }
}

