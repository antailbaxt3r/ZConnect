package com.zconnect.zutto.zconnect.pools.models;

import android.os.Bundle;

import com.google.firebase.database.Exclude;
import com.zconnect.zutto.zconnect.itemFormats.PostedByDetails;

import java.util.Vector;

public class Order {

    //URLS
    public static final String URL_ORDER_BASIC_INFO = "shops/shopDetails/%s/orders/%s";
    //                                                                 userID
    public static final String URL_ORDER_ITEM_LIST = "shops/shopDetails/%s/orders/%s/%s/items";
    //                                                         shopID     poolID,payID

    //Node names
    public static final String ORDER_ID = "orderID";
    public static final String POOL_PUSH_ID = "poolPushID";
    public static final String STATUS = "status";
    public static final String PAYMENT_ID = "paymentID";
    public static final String TIMESTAMP_PAYMENT = "timeStampPayment";
    public static final String TOTAL_AMOUNT = "totalAmount";
    public static final String POOL_INFO = "poolInfo";
    public static final String ORDERED_BY = "orderedBy";


    private String orderID;
    private String poolPushID;
    private String status;
    private String paymentID;
    private long getTimestampPayment;
    private long totalAmount;
    private PoolInfo poolInfo;
    private PostedByDetails orderedBy;

    public Order() {
    }

    public Order(String orderID, String poolPushID, String status, String paymentID, long getTimestampPayment, long totalAmount, PoolInfo poolInfo, Vector<PoolItem> items, PostedByDetails orderedBy) {
        this.orderID = orderID;
        this.poolPushID = poolPushID;
        this.status = status;
        this.paymentID = paymentID;
        this.getTimestampPayment = getTimestampPayment;
        this.totalAmount = totalAmount;
        this.poolInfo = poolInfo;
        this.items = items;
        this.orderedBy = orderedBy;
    }

    private Vector<PoolItem> items;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
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

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public long getGetTimestampPayment() {
        return getTimestampPayment;
    }

    public void setGetTimestampPayment(long getTimestampPayment) {
        this.getTimestampPayment = getTimestampPayment;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PoolInfo getPoolInfo() {
        return poolInfo;
    }

    public void setPoolInfo(PoolInfo poolInfo) {
        this.poolInfo = poolInfo;
    }

    public Vector<PoolItem> getItems() {
        return items;
    }

    public void setItems(Vector<PoolItem> items) {
        this.items = items;
    }

    public PostedByDetails getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(PostedByDetails orderedBy) {
        this.orderedBy = orderedBy;
    }

    //    public Bundle getBundle() {
//        Bundle b = new Bundle();
//        if (this.orderID != null)
//            b.putString(SHOP_ORDER_ID, this.orderID);
//        if (this.paymentID != null)
//            b.putString(PAYMENT_ID, this.paymentID);
//        if (this.status != null)
//            b.putString(status, this.status);
//        if (this.poolID != null)
//            b.putString(POOL_ID, this.poolID);
//        if (this.poolName != null)
//            b.putString(POOL_NAME, this.poolName);
//        if(this.poolPushID != null)
//            b.putString(POOL_PUSH_ID,this.poolPushID);
//        if(this.shopID != null)
//            b.putString(SHOP_ID,this.shopID);
//        b.putInt(TOTAL_AMOUNT,this.amount);
//        return b;
//    }
//    public static Order getShopOrder(Bundle b){
//        Order o = new Order();
//        if(b.containsKey(SHOP_ORDER_ID))
//            o.setOrderID(b.getString(SHOP_ORDER_ID));
//        if(b.containsKey(PAYMENT_ID))
//            o.setPaymentID(b.getString(PAYMENT_ID));
//        if(b.containsKey(status))
//            o.setStatus(b.getString(status));
//        if(b.containsKey(POOL_ID))
//            o.setPoolID(b.getString(POOL_ID));
//        if(b.containsKey(POOL_NAME))
//            o.setPoolName(b.getString(POOL_NAME));
//        if(b.containsKey(POOL_PUSH_ID))
//            o.setPoolPushID(b.getString(POOL_PUSH_ID));
//        if(b.containsKey(SHOP_ID))
//            o.setShopID(b.getString(SHOP_ID));
//        if(b.containsKey(TOTAL_AMOUNT))
//            o.setAmount(b.getInt(TOTAL_AMOUNT));
//
//
//        return o;
//    }


}
