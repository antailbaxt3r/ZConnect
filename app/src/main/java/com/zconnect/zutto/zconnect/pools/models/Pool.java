package com.zconnect.zutto.zconnect.pools.models;
/*
 * $pushID_pools
     * status : AC/UP
     * timeStampCreated :
     * timeStampActivated :
     * deliveryTime :
     * poolPushID :
     * poolID :
     * shopID :
     * name :
     * description :
     * imageURL :
     * offerType :
     * upvote :
     * totalOrders : number
 */

import android.os.Bundle;

import java.util.HashMap;

public class Pool {

    //URLS
    public static final String URL_POOL = "communities/%s/features/shops/pools/current";
    //                                                    communityID
    public static final String URL_POOL_UP_VOTE = "communities/%s/features/shops/pools/current/%s/upvoteList/%s";
    //                                                    communityID ,                      poolID,        UID


    //Node name
    public static final String STATUS = "status";
    public static final String CREATED_TIMESTAMP = "timeStampCreated";
    public static final String ACTIVATED_TIMESTAMP = "timeStampActivated";
    public static final String DELIVERY_TIME = "deliveryTime";
    public static final String POOL_PUSH_ID = "poolPushID";
    public static final String POOL_ID = "poolID";
    public static final String SHOP_ID = "shopID";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE_URL = "imageURL";
    public static final String OFFER_TYPE = "offerType";
    public static final String UP_VOTE = "upvote";
    public static final String UP_VOTE_LIST = "upvoteList";
    public static final String TOTAL_ORDERS = "totalOrders";

    //Node values
    public static final String STATUS_UPCOMING = "upcoming";
    public static final String STATUS_ACTIVE = "active";

    private String poolPushID;
    private long deliveryTime, timeStampCreated, timeStampActivated;
    private String status;
    private String poolID;
    private String shopID;
    private String name;
    private String offerType;
    private String description;
    private String imageURL;
    private HashMap<String, Integer> upvoteList;
    private int upvote;
    private int totalOrder;

    public Pool() {

    }

    public static Pool getPool(Bundle b) {
        Pool pool = new Pool();

        if (b.containsKey(POOL_PUSH_ID))
            pool.setPoolPushID(b.getString(POOL_PUSH_ID));

        if (b.containsKey(DELIVERY_TIME))
            pool.setDeliveryTime(b.getLong(DELIVERY_TIME));

        if (b.containsKey(ACTIVATED_TIMESTAMP))
            pool.setTimeStampActivated(b.getLong(ACTIVATED_TIMESTAMP));

        if (b.containsKey(CREATED_TIMESTAMP))
            pool.setTimeStampCreated(b.getLong(CREATED_TIMESTAMP));

        if (b.containsKey(STATUS))
            pool.setStatus(b.getString(STATUS));

        if (b.containsKey(POOL_ID))
            pool.setPoolID(b.getString(POOL_ID));

        if (b.containsKey(SHOP_ID))
            pool.setShopID(b.getString(SHOP_ID));

        if (b.containsKey(NAME))
            pool.setName(b.getString(NAME));

        if (b.containsKey(DESCRIPTION))
            pool.setDescription(b.getString(DESCRIPTION));

        if (b.containsKey(IMAGE_URL))
            pool.setImageURL(b.getString(IMAGE_URL));

        if (b.containsKey(OFFER_TYPE))
            pool.setOfferType(b.getString(OFFER_TYPE));

        if (b.containsKey(UP_VOTE))
            pool.setUpvote(b.getInt(UP_VOTE));

        if (b.containsKey(TOTAL_ORDERS))
            pool.setTotalOrder(b.getInt(TOTAL_ORDERS));

        return pool;
    }

    public String getPoolPushID() {
        return poolPushID;
    }

    public void setPoolPushID(String poolPushID) {
        this.poolPushID = poolPushID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPoolID() {
        return poolID;
    }

    public void setPoolID(String poolID) {
        this.poolID = poolID;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }

    public int getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(int totalOrder) {
        this.totalOrder = totalOrder;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public long getTimeStampCreated() {
        return timeStampCreated;
    }

    public void setTimeStampCreated(long timeStampCreated) {
        this.timeStampCreated = timeStampCreated;
    }

    public long getTimeStampActivated() {
        return timeStampActivated;
    }

    public void setTimeStampActivated(long timeStampActivated) {
        this.timeStampActivated = timeStampActivated;
    }

    public HashMap<String, Integer> getUpvoteList() {
        if (upvoteList == null) return new HashMap<String, Integer>();
        return upvoteList;
    }

    public void setUpvoteList(HashMap<String, Integer> upvoteList) {
        this.upvoteList = upvoteList;
    }


    public HashMap<String, Object> getHashMap() {

        HashMap<String, Object> mp = new HashMap<>();

        mp.put(POOL_PUSH_ID,this.poolPushID);
        mp.put(DELIVERY_TIME, this.deliveryTime);
        mp.put(ACTIVATED_TIMESTAMP,this.timeStampActivated);
        mp.put(CREATED_TIMESTAMP,this.timeStampCreated);
        mp.put(STATUS, this.status);
        mp.put(SHOP_ID, this.shopID);
        mp.put(POOL_ID, this.poolID);
        mp.put(NAME, this.name);
        mp.put(DESCRIPTION, this.description);
        mp.put(IMAGE_URL, this.imageURL);
        mp.put(OFFER_TYPE, this.offerType);
        mp.put(UP_VOTE, this.getUpvote());
        mp.put(UP_VOTE_LIST,this.upvoteList);
        mp.put(TOTAL_ORDERS, this.totalOrder);

        return mp;
    }

    public Bundle getBundle() {
        Bundle b = new Bundle();

        if (this.getPoolPushID() != null) {
            b.putString(POOL_PUSH_ID, this.getPoolPushID());
        }

        b.putLong(DELIVERY_TIME, this.deliveryTime);
        b.putLong(CREATED_TIMESTAMP, this.timeStampCreated);
        b.putLong(ACTIVATED_TIMESTAMP, this.timeStampActivated);

        if (this.getStatus() != null) {
            b.putString(STATUS, this.status);
        }

        if (this.getPoolID() != null) {
            b.putString(POOL_ID, this.getPoolID());
        }
        if (this.getShopID() != null) {
            b.putString(SHOP_ID, this.getShopID());
        }
        if (this.getName() != null) {
            b.putString(NAME, this.getName());
        }

        if (this.getDescription() != null) {
            b.putString(DESCRIPTION, this.getDescription());
        }

        if (this.getImageURL() != null) {
            b.putString(IMAGE_URL, this.getImageURL());
        }

        if (this.getOfferType() != null) {
            b.putString(OFFER_TYPE, this.getOfferType());
        }

        b.putInt(UP_VOTE, this.upvote);
        b.putInt(TOTAL_ORDERS, this.totalOrder);

        return b;
    }

    public boolean isActive() {
        return status.compareTo(STATUS_ACTIVE) == 0;
    }

    public boolean isUpcoming() {
        return status.compareTo(STATUS_UPCOMING) == 0;
    }
}
