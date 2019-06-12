package com.zconnect.zutto.zconnect.pools.models;

import android.os.Bundle;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
 * name :
 * type :
 * description :
 * imageURL :
 */
public class PoolInfo implements Serializable {

    // URLS
    public static final String URL_POOL_INFO = "shops/shopDetails/%s/pools/poolInfo";
    //                                                         shopID
    public static final String URL_POOL_OFFER = "shops/shopDetails/%s/poolTemplates/poolInfo/%s/offer";
    //                                                          shopID                      poolID
    public static final String URL_POOL_TEMPLATE_INFO = "shops/shopDetails/%s/poolTemplates/poolInfo/%s";

    //Node name
    public static final String NAME = "name";
    public static final String OFFER_TYPE = "offerType";
    public static final String DESCRIPTION = "description";
    public static final String EXTRAS = "extras";
    public static final String IMAGE_URL = "imageURL";
    public static final String IMAGE_THUMB = "imageThumb";
    public static final String OFFER = "offer";
    public static final String POOL_ID = "poolID";
    public static final String SHOP_ID = "shopID";
    public static final String CONVENIENCE_PERCENTAGE = "conveniencePercentage";
    public static final String CONVENIENCE_UPTO = "convenienceUpto";
    public static final String CONVENIENCE_MIN = "convenienceMin";

    // nodes
    private String name;
    private String description;
    private String extras;
    private String imageURL;
    private String imageThumb;
    private String offerType;
    private DiscountOffer offer;
    private String poolID;
    private String shopID;
    private int conveniencePercentage;
    private int convenienceUpto;
    private int convenienceMin;

    public PoolInfo() {

    }

    public PoolInfo(String name, String description, String extras, String imageURL, String imageThumb, String offerType, DiscountOffer offer, String poolID, String shopID, int conveniencePercentage, int convenienceUpto, int convenienceMin) {
        this.name = name;
        this.description = description;
        this.extras = extras;
        this.imageURL = imageURL;
        this.imageThumb = imageThumb;
        this.offerType = offerType;
        this.offer = offer;
        this.poolID = poolID;
        this.shopID = shopID;
        this.conveniencePercentage = conveniencePercentage;
        this.convenienceUpto = convenienceUpto;
        this.convenienceMin = convenienceMin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public DiscountOffer getOffer() {
        return offer;
    }

    public void setOffer(DiscountOffer offer) {
        this.offer = offer;
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

    public int getConveniencePercentage() {
        return conveniencePercentage;
    }

    public void setConveniencePercentage(int conveniencePercentage) {
        this.conveniencePercentage = conveniencePercentage;
    }

    public int getConvenienceUpto() {
        return convenienceUpto;
    }

    public void setConvenienceUpto(int convenienceUpto) {
        this.convenienceUpto = convenienceUpto;
    }

    public int getConvenienceMin() {
        return convenienceMin;
    }

    public void setConvenienceMin(int convenienceMin) {
        this.convenienceMin = convenienceMin;
    }
    //
//    public static PoolInfo getPoolInfo(Bundle b) {
//        PoolInfo poolInfo = new PoolInfo();
//        if (b.containsKey("poolInfoID"))
//            poolInfo.setOrderID(b.getString("poolInfoID"));
//        if (b.containsKey(NAME))
//            poolInfo.setName(b.getString(NAME));
//        if (b.containsKey(DESCRIPTION))
//            poolInfo.setDescription(b.getString(DESCRIPTION));
//        if (b.containsKey(OFFER_TYPE))
//            poolInfo.setType(b.getString(OFFER_TYPE));
//        if (b.containsKey(IMAGE_URL))
//            poolInfo.setImageURL(b.getString(IMAGE_URL));
//        Map<String, Integer> offer = new HashMap<>();
//        if (b.containsKey(DISCOUNT_PERCENTAGE))
//            offer.put(DISCOUNT_PERCENTAGE, b.getInt(DISCOUNT_PERCENTAGE));
//        if (b.containsKey(MAX_DISCOUNT))
//            offer.put(MAX_DISCOUNT, b.getInt(MAX_DISCOUNT));
//        if (b.containsKey(DISCOUNT_PERCENTAGE))
//            offer.put(MIN_QUANTITY, b.getInt(MIN_QUANTITY));
//        poolInfo.setOffer(offer);
//        return poolInfo;
//    }
//
//
//    public Bundle getBundle() {
//        Bundle b = new Bundle();
//        if (this.getOrderID() != null) {
//            b.putString("poolInfoID", this.getOrderID());
//        }
//        if (this.getName() != null) {
//            b.putString(NAME, this.getName());
//        }
//        if (this.getDescription() != null) {
//            b.putString(DESCRIPTION, this.getDescription());
//        }
//        if (this.getType() != null) {
//            b.putString(OFFER_TYPE, this.getType());
//        }
//        if (this.getImageURL() != null) {
//            b.putString(IMAGE_URL, this.getImageURL());
//        }
//
//        b.putInt(DISCOUNT_PERCENTAGE, this.getDiscountPercentage());
//        b.putInt(MAX_DISCOUNT, this.getMaxDiscount());
//        b.putInt(MIN_QUANTITY, this.getMinQunatity());
//
//        return b;
//    }

}
