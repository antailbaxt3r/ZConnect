package com.zconnect.zutto.zconnect.pools.models;

import com.google.firebase.database.Exclude;

public class ShopOrder {

    //Node names
    public static final String RAZOR_PAY_ID = "razorPayID";
    public static final String AMOUNT = "amount";
    public static final String ORDER_STATUS = "orderStatus";
    public static final String POOL_ID = "poolID";
    public static final String POOL_NAME = "poolName";
    public static final String POOL_PUSH_ID = "poolPushID";
    public static final String SHOP_ID = "shopID";


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
