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

import java.io.Serializable;
import java.util.HashMap;

public class Pool implements Serializable {

    //URLS
    public static final String URL_POOL = "communities/%s/features/shops/pools/current";
    //                                                    communityID
    public static final String URL_POOL_UP_VOTE = "communities/%s/features/shops/pools/current/%s/upvoteList/%s";
    //                                                    communityID ,                      poolID,        UID


    //Node name
    public static final String STATUS = "status";
    public static final String CREATED_TIMESTAMP = "timeStampCreated";
    public static final String ACTIVATED_TIMESTAMP = "timeStampActivated";
    public static final String DEADLINE_TIMESTAMP = "timestampOrderReceivingDeadline";
    public static final String DELIVERY_TIME = "deliveryTime";
    public static final String POOL_PUSH_ID = "poolPushID";
    public static final String POOL_INFO = "poolInfo";
    public static final String UP_VOTE = "upvote";
    public static final String UP_VOTE_LIST = "upvoteList";
    public static final String TOTAL_ORDERS = "totalOrder";
    public static final String ORDER_RECEIVING_STATUS = "orderReceivingStatus";

    //tabID for forum
    public static final String POOL_FORUM_TAB_ID = "shopPools";

    //Node values
    public static final String STATUS_UPCOMING = "upcoming";
    public static final String STATUS_ACTIVE = "active";

    private String poolPushID;
    private long deliveryTime, timeStampCreated, timeStampActivated, timestampOrderReceivingDeadline;
    private String status;
    private PoolInfo poolInfo;
    private HashMap<String, Integer> upvoteList = new HashMap<>();
    private int upvote;
    private int totalOrder;
    private boolean orderReceivingStatus;

    public Pool() {

    }

    public Pool(String poolPushID, long deliveryTime, long timeStampCreated, long timeStampActivated, String status, PoolInfo poolInfo, HashMap<String, Integer> upvoteList, int upvote, int totalOrder, long timestampOrderReceivingDeadline, boolean orderReceivingStatus) {
        this.poolPushID = poolPushID;
        this.deliveryTime = deliveryTime;
        this.timeStampCreated = timeStampCreated;
        this.timeStampActivated = timeStampActivated;
        this.status = status;
        this.poolInfo = poolInfo;
        this.upvoteList = upvoteList;
        this.upvote = upvote;
        this.totalOrder = totalOrder;
        this.timestampOrderReceivingDeadline = timestampOrderReceivingDeadline;
        this.orderReceivingStatus = orderReceivingStatus;
    }

    public String getPoolPushID() {
        return poolPushID;
    }

    public void setPoolPushID(String poolPushID) {
        this.poolPushID = poolPushID;
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

    public long getTimestampOrderReceivingDeadline() {
        return timestampOrderReceivingDeadline;
    }

    public void setTimestampOrderReceivingDeadline(long timestampOrderReceivingDeadline) {
        this.timestampOrderReceivingDeadline = timestampOrderReceivingDeadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PoolInfo getPoolInfo() {
        return poolInfo;
    }

    public void setPoolInfo(PoolInfo poolInfo) {
        this.poolInfo = poolInfo;
    }

    public HashMap<String, Integer> getUpvoteList() {
        return upvoteList;
    }

    public void setUpvoteList(HashMap<String, Integer> upvoteList) {
        this.upvoteList = upvoteList;
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

    public boolean isOrderReceivingStatus() {
        return orderReceivingStatus;
    }

    public void setOrderReceivingStatus(boolean orderReceivingStatus) {
        this.orderReceivingStatus = orderReceivingStatus;
    }

    //    public HashMap<String, Object> getHashMap() {
//
//        HashMap<String, Object> mp = new HashMap<>();
//
//        mp.put(POOL_PUSH_ID,this.poolPushID);
//        mp.put(DELIVERY_TIME, this.deliveryTime);
//        mp.put(ACTIVATED_TIMESTAMP,this.timeStampActivated);
//        mp.put(CREATED_TIMESTAMP,this.timeStampCreated);
//        mp.put(STATUS, this.status);
//        mp.put(SHOP_ID, this.shopID);
//        mp.put(POOL_ID, this.poolID);
//        mp.put(NAME, this.name);
//        mp.put(DESCRIPTION, this.description);
//        mp.put(IMAGE_URL, this.imageURL);
//        mp.put(OFFER_TYPE, this.offerType);
//        mp.put(UP_VOTE, this.getUpvote());
//        mp.put(UP_VOTE_LIST,this.upvoteList);
//        mp.put(TOTAL_ORDERS, this.totalOrder);
//
//        return mp;
//    }
//
//    public Bundle getBundle() {
//        Bundle b = new Bundle();
//
//        if (this.getPoolPushID() != null) {
//            b.putString(POOL_PUSH_ID, this.getPoolPushID());
//        }
//
//        b.putLong(DELIVERY_TIME, this.deliveryTime);
//        b.putLong(CREATED_TIMESTAMP, this.timeStampCreated);
//        b.putLong(ACTIVATED_TIMESTAMP, this.timeStampActivated);
//
//        if (this.getStatus() != null) {
//            b.putString(STATUS, this.status);
//        }
//
//        if (this.getPoolID() != null) {
//            b.putString(POOL_ID, this.getPoolID());
//        }
//        if (this.getShopID() != null) {
//            b.putString(SHOP_ID, this.getShopID());
//        }
//        if (this.getName() != null) {
//            b.putString(NAME, this.getName());
//        }
//
//        if (this.getDescription() != null) {
//            b.putString(DESCRIPTION, this.getDescription());
//        }
//
//        if (this.getImageURL() != null) {
//            b.putString(IMAGE_URL, this.getImageURL());
//        }
//
//        if (this.getOfferType() != null) {
//            b.putString(OFFER_TYPE, this.getOfferType());
//        }
//
//        b.putInt(UP_VOTE, this.upvote);
//        b.putInt(TOTAL_ORDERS, this.totalOrder);
//
//        return b;
//    }

    public boolean isActive() {
        return status.compareTo(STATUS_ACTIVE) == 0;
    }

    public boolean isUpcoming() {
        return status.compareTo(STATUS_UPCOMING) == 0;
    }
}
