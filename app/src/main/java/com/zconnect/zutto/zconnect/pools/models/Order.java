package com.zconnect.zutto.zconnect.pools.models;

import com.zconnect.zutto.zconnect.itemFormats.PostedByDetails;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

public class Order implements Serializable{

    //URLS
    public static final String URL_MY_ORDERS = "communities/%s/features/shops/orders/current/%s";
    public static final String URL_MY_PARTICULAR_ORDER = "communities/%s/features/shops/orders/current/%s/%s";
    public static final String URL_ORDER_BASIC_INFO = "shops/shopDetails/%s/orders/%s";
    //                                                                 userID
    public static final String URL_MY_ORDER_ITEM_LIST = "communities/%s/features/shops/orders/current/%s/%s/items";
    //                                                          communnityID                      userID/orderID


    //Values
    public static final String KEY_ORDER_OUT_FOR_DELIVERY = "out for delivery";
    public static final String KEY_ORDER_DELIVERED = "delivered";

    public static final String KEY_PAYMENT_FAIL = "fail";
    public static final String KEY_PAYMENT_PENDING = "pending";
    public static final String KEY_PAYMENT_PROCESSING = "processing";
    public static final String KEY_PAYMENT_SUCCESS = "success";

    //Node names
    public static final String ORDER_ID = "orderID";
    public static final String POOL_PUSH_ID = "poolPushID";
    public static final String ORDER_STATUS = "orderStatus";
    public static final String PAYMENT_STATUS = "paymentStatus";
    public static final String PAYMENT_ID = "paymentID";
    public static final String TIMESTAMP_PAYMENT_BEFORE = "timestampPaymentBefore";
    public static final String TIMESTAMP_PAYMENT_AFTER= "timestampPaymentAfter";
    public static final String TOTAL_AMOUNT = "totalAmount";
    public static final String DISCOUNTED_AMOUNT = "discountedAmount";
    public static final String POOL_INFO = "poolInfo";
    public static final String ITEMS = "items";
    public static final String DELIVERY_TIME = "deliveryTime";
    public static final String DELIVERY_RCD_TIME = "deliveryRcdTime";
    public static final String ORDERED_BY = "orderedBy";
    public static final String PHONE_NUMBER = "phoneNumber";

    private String orderID;
    private String poolPushID;
    private String orderStatus;
    private String paymentStatus;
    private String paymentGatewayID;
    private long timestampPaymentBefore;
    private long timestampPaymentAfter;
    private long totalAmount;
    private long discountedAmount;
    private long deliveryTime;
    private long deliveryRcdTime;
    private PoolInfo poolInfo;
    private PostedByDetails orderedBy;
    private HashMap<String, PoolItem> items;
    private String userBillID;
    private String phoneNumber;

    public Order() {
    }

    public Order(String orderID, String poolPushID, String orderStatus, String paymentStatus, String paymentGatewayID, long timestampPaymentBefore, long timestampPaymentAfter, long totalAmount, long discountedAmount, PoolInfo poolInfo, HashMap<String, PoolItem> items, PostedByDetails orderedBy, long deliveryTime, long deliveryRcdTime, String userBillID, String phoneNumber) {
        this.orderID = orderID;
        this.poolPushID = poolPushID;
        this.orderStatus = orderStatus;
        this.paymentGatewayID = paymentGatewayID;
        this.timestampPaymentBefore = timestampPaymentBefore;
        this.timestampPaymentAfter = timestampPaymentAfter;
        this.totalAmount = totalAmount;
        this.discountedAmount = discountedAmount;
        this.poolInfo = poolInfo;
        this.items = items;
        this.orderedBy = orderedBy;
        this.deliveryTime = deliveryTime;
        this.deliveryRcdTime = deliveryRcdTime;
        this.userBillID = userBillID;
        this.paymentStatus = paymentStatus;
        this.phoneNumber = phoneNumber;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setDeliveryRcdTime(long deliveryRcdTime) {
        this.deliveryRcdTime = deliveryRcdTime;
    }

    public long getDeliveryRcdTime() {
        return deliveryRcdTime;
    }

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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentGatewayID() {
        return paymentGatewayID;
    }

    public void setPaymentGatewayID(String paymentGatewayID) {
        this.paymentGatewayID = paymentGatewayID;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDiscountedAmount(long discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public long getDiscountedAmount() {
        return discountedAmount;
    }

    public PoolInfo getPoolInfo() {
        return poolInfo;
    }

    public void setPoolInfo(PoolInfo poolInfo) {
        this.poolInfo = poolInfo;
    }

    public HashMap<String, PoolItem> getItems() {
        return items;
    }

    public void setItems(HashMap<String, PoolItem> items) {
        this.items = items;
    }

    public PostedByDetails getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(PostedByDetails orderedBy) {
        this.orderedBy = orderedBy;
    }

    public void setUserBillID(String userBillID) {
        this.userBillID = userBillID;
    }

    public String getUserBillID() {
        return userBillID;
    }

    public long getTimestampPaymentBefore() {
        return timestampPaymentBefore;
    }

    public void setTimestampPaymentBefore(long timestampPaymentBefore) {
        this.timestampPaymentBefore = timestampPaymentBefore;
    }

    public long getTimestampPaymentAfter() {
        return timestampPaymentAfter;
    }

    public void setTimestampPaymentAfter(long timestampPaymentAfter) {
        this.timestampPaymentAfter = timestampPaymentAfter;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
