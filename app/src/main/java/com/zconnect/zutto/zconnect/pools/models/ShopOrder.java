package com.zconnect.zutto.zconnect.pools.models;

import android.os.Bundle;

import com.google.firebase.database.Exclude;

public class ShopOrder {

    //URLS
    public static final String URL_ORDER_BASIC_INFO = "shops/shopDetails/%s/orders/%s";
    //                                                                 userID
    public static final String URL_ORDER_ITEM_LIST = "shops/shopDetails/%s/orders/%s/%s/items";
    //                                                         shopID     poolID,payID

    //Node names
    public static final String RAZOR_PAY_ID = "razorPayID";
    public static final String AMOUNT = "amount";
    public static final String ORDER_STATUS = "orderStatus";
    public static final String POOL_ID = "poolID";
    public static final String POOL_NAME = "poolName";
    public static final String POOL_PUSH_ID = "poolPushID";
    public static final String SHOP_ID = "shopID";
    public static final String SHOP_ORDER_ID = "shopOrderID";


    @Exclude
    private String ID;
    private String razorPayID;
    private int amount;
    private String orderStatus;
    private String poolID;
    private String poolName;
    private String poolPushID;
    private String shopID;

    public String getID() {
        return ID;
    }

    public Bundle getBundle() {
        Bundle b = new Bundle();
        if (this.ID != null)
            b.putString(SHOP_ORDER_ID, this.ID);
        if (this.razorPayID != null)
            b.putString(RAZOR_PAY_ID, this.razorPayID);
        if (this.orderStatus != null)
            b.putString(ORDER_STATUS, this.orderStatus);
        if (this.poolID != null)
            b.putString(POOL_ID, this.poolID);
        if (this.poolName != null)
            b.putString(POOL_NAME, this.poolName);
        if(this.poolPushID != null)
            b.putString(POOL_PUSH_ID,this.poolPushID);
        if(this.shopID != null)
            b.putString(SHOP_ID,this.shopID);
        b.putInt(AMOUNT,this.amount);
        return b;
    }
    public static ShopOrder getShopOrder(Bundle b){
        ShopOrder o = new ShopOrder();
        if(b.containsKey(SHOP_ORDER_ID))
            o.setID(b.getString(SHOP_ORDER_ID));
        if(b.containsKey(RAZOR_PAY_ID))
            o.setRazorPayID(b.getString(RAZOR_PAY_ID));
        if(b.containsKey(ORDER_STATUS))
            o.setOrderStatus(b.getString(ORDER_STATUS));
        if(b.containsKey(POOL_ID))
            o.setPoolID(b.getString(POOL_ID));
        if(b.containsKey(POOL_NAME))
            o.setPoolName(b.getString(POOL_NAME));
        if(b.containsKey(POOL_PUSH_ID))
            o.setPoolPushID(b.getString(POOL_PUSH_ID));
        if(b.containsKey(SHOP_ID))
            o.setShopID(b.getString(SHOP_ID));
        if(b.containsKey(AMOUNT))
            o.setAmount(b.getInt(AMOUNT));


        return o;
    }


    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRazorPayID() {
        return razorPayID;
    }

    public void setRazorPayID(String razorPayID) {
        this.razorPayID = razorPayID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPoolID() {
        return poolID;
    }

    public void setPoolID(String poolID) {
        this.poolID = poolID;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getPoolPushID() {
        return poolPushID;
    }

    public void setPoolPushID(String poolPushID) {
        this.poolPushID = poolPushID;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }
}
