package com.zconnect.zutto.zconnect.pools.models;

import android.os.Bundle;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/*
 * name :
 * type :
 * description :
 * imageURL :
 */
public class PoolInfo {

    // URLS
    public static final String URL_POOL_INFO = "communities/%s/shopOwner/%s/pools/poolInfo";
    //                                                    communityID ,   shopID
    public static final String URL_POOL_OFFER = "communities/%s/shopOwner/%s/pools/poolInfo/%s/offer";
    //                                                    communityID ,   shopID,          poolID
    //Node name
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE_URL = "imageURL";
    public static final String OFFER = "offer";
    public static final String DISCOUNT_PERCENTAGE = "discountPercentage";
    public static final String MAX_DISCOUNT = "maxDiscount";
    public static final String MIN_QUANTITY = "minQuantity";


    @Exclude
    private String ID;
    private String name;
    private String type;
    private String description;
    private String imageURL;
    private Map<String, Integer> offer;

    public PoolInfo() {
    }

    public static PoolInfo getDummy() {
        PoolInfo p = new PoolInfo();
        p.setID("adaasd");
        p.setDescription("30% off");
        p.setImageURL("https://dummyimage.com/120x120/736573/fff.png&text=Logo");
        p.setName("dummyName");
        p.setType("dish");
        return p;
    }

    public static PoolInfo getPoolInfo(Bundle b) {
        PoolInfo poolInfo = new PoolInfo();
        if (b.containsKey("poolInfoID"))
            poolInfo.setID(b.getString("poolInfoID"));
        if (b.containsKey(NAME))
            poolInfo.setName(b.getString(NAME));
        if (b.containsKey(DESCRIPTION))
            poolInfo.setDescription(b.getString(DESCRIPTION));
        if (b.containsKey(TYPE))
            poolInfo.setType(b.getString(TYPE));
        if (b.containsKey(IMAGE_URL))
            poolInfo.setImageURL(b.getString(IMAGE_URL));
        Map<String, Integer> offer = new HashMap<>();
        if (b.containsKey(DISCOUNT_PERCENTAGE))
            offer.put(DISCOUNT_PERCENTAGE, b.getInt(DISCOUNT_PERCENTAGE));
        if (b.containsKey(MAX_DISCOUNT))
            offer.put(MAX_DISCOUNT, b.getInt(MAX_DISCOUNT));
        if (b.containsKey(DISCOUNT_PERCENTAGE))
            offer.put(MIN_QUANTITY, b.getInt(MIN_QUANTITY));
        poolInfo.setOffer(offer);
        return poolInfo;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public Map<String, Integer> getOffer() {
        return offer;
    }

    public void setOffer(Map<String, Integer> offer) {
        this.offer = offer;
    }

    @Exclude
    public int getDiscountPercentage() {
        if (offer == null) return 0;
        if (offer.containsKey(DISCOUNT_PERCENTAGE)) return offer.get(DISCOUNT_PERCENTAGE);
        else return 0;
    }

    @Exclude
    public int getMaxDiscount() {
        if (offer == null) return 0;
        if (offer.containsKey(MAX_DISCOUNT)) return offer.get(MAX_DISCOUNT);
        else return 0;
    }

    @Exclude
    public int getMinQunatity() {
        if (offer == null) return Integer.MAX_VALUE;
        if (offer.containsKey(MIN_QUANTITY)) return offer.get(MIN_QUANTITY);
        else return Integer.MAX_VALUE;
    }

    public Bundle getBundle() {
        Bundle b = new Bundle();
        if (this.getID() != null) {
            b.putString("poolInfoID", this.getID());
        }
        if (this.getName() != null) {
            b.putString(NAME, this.getName());
        }
        if (this.getDescription() != null) {
            b.putString(DESCRIPTION, this.getDescription());
        }
        if (this.getType() != null) {
            b.putString(TYPE, this.getType());
        }
        if (this.getImageURL() != null) {
            b.putString(IMAGE_URL, this.getImageURL());
        }

        b.putInt(DISCOUNT_PERCENTAGE, this.getDiscountPercentage());
        b.putInt(MAX_DISCOUNT, this.getMaxDiscount());
        b.putInt(MIN_QUANTITY, this.getMinQunatity());

        return b;
    }

}
