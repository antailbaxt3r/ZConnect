package com.zconnect.zutto.zconnect.pools.models;
/*
status : AC/UP

    diliveryTime :
    poolID :
    shopID :
    name :
    type :
    deliveryTime :
    description :
    imageURL :
    upvote :
    totalOrdered : number

 */

import android.os.Bundle;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Pool {

    //URLS
    public static final String URL_POOL = "communities/%s/features/shops/pools/active";
    //                                                    communityID


    //Node name
    public static final String STATUS = "status";
    public static final String POOL_ID = "poolID";
    public static final String SHOP_ID = "shopID";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE_URL = "imageURL";
    public static final String DELIVERY_TIME = "deliveryTime";
    public static final String UP_VOTE = "upVote";
    public static final String TOTAL_ORDER = "totalOrder";

    //Node values
    public static final String STATUS_UPCOMING = "upcoming";
    public static final String STATUS_ACTIVE = "active";


    @Exclude
    private String ID;
    private String status;
    private String poolID;
    private String shopID;
    private String name;
    private String type;
    private String description;
    private String imageURL;
    private long deliveryTime;
    private int upVote;
    private int totalOrder;

    public Pool() {
    }

    public Pool(PoolInfo poolInfo) {
        this.name = poolInfo.getName();
        this.type = poolInfo.getType();
        this.poolID = poolInfo.getID();
        this.description = poolInfo.getDescription();
        this.imageURL = poolInfo.getImageURL();
        this.upVote = 0;
        this.totalOrder = 0;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getUpVote() {
        return upVote;
    }

    public void setUpVote(int upVote) {
        this.upVote = upVote;
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

    public HashMap<String, Object> getHashMap() {
        HashMap<String, Object> mp = new HashMap<>();

        mp.put(STATUS, this.status);
        mp.put(SHOP_ID, this.shopID);
        mp.put(POOL_ID, this.poolID);
        mp.put(NAME, this.name);
        mp.put(TYPE, this.type);
        mp.put(DESCRIPTION, this.description);
        mp.put(IMAGE_URL, this.imageURL);
        mp.put(DELIVERY_TIME, this.deliveryTime);
        mp.put(UP_VOTE, this.upVote);
        mp.put(TOTAL_ORDER, this.totalOrder);

        return mp;
    }
    public Bundle getBundle() {
        Bundle b = new Bundle();
        if(this.ID != null){
            b.putString("pushID",this.getID());
        }
        if(this.status != null){
            b.putString(STATUS,this.status);
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
        if (this.getType() != null) {
            b.putString(TYPE, this.getType());
        }
        if (this.getDescription() != null) {
            b.putString(DESCRIPTION, this.getDescription());
        }

        if (this.getImageURL() != null) {
            b.putString(IMAGE_URL, this.getImageURL());
        }

        b.putLong(DELIVERY_TIME,this.deliveryTime);
        b.putInt(UP_VOTE,this.upVote);
        b.putInt(TOTAL_ORDER,this.totalOrder);

        return b;
    }
    public static Pool getPool(Bundle b){
        Pool pool = new Pool();
        if(b.containsKey("pushID"))
            pool.setID(b.getString("pushID"));

        if(b.containsKey(STATUS))
            pool.setStatus(b.getString(STATUS));

        if(b.containsKey(POOL_ID))
            pool.setPoolID(b.getString(POOL_ID));

        if(b.containsKey(SHOP_ID))
            pool.setShopID(b.getString(SHOP_ID));

        if(b.containsKey(NAME))
            pool.setName(b.getString(NAME));

        if(b.containsKey(TYPE))
            pool.setType(b.getString(TYPE));

        if(b.containsKey(DESCRIPTION))
            pool.setDescription(b.getString(DESCRIPTION));

        if(b.containsKey(IMAGE_URL))
            pool.setImageURL(b.getString(IMAGE_URL));

        if(b.containsKey(DELIVERY_TIME))
            pool.setDeliveryTime(b.getLong(DELIVERY_TIME));

        if(b.containsKey(UP_VOTE))
            pool.setUpVote(b.getInt(UP_VOTE));

        if(b.containsKey(TOTAL_ORDER))
            pool.setTotalOrder(b.getInt(TOTAL_ORDER));


        return pool;
    }

    public boolean isActive() {
        return status.compareTo(STATUS_ACTIVE) == 0;
    }

    public boolean isUpcoming() {
        return status.compareTo(STATUS_UPCOMING) == 0;
    }
}
