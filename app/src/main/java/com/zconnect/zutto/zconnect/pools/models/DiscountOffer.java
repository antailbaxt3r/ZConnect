package com.zconnect.zutto.zconnect.pools.models;

import android.content.Intent;

public class DiscountOffer {

    public static final String DISCOUNT_PERCENTAGE = "discountPercentage";
    public static final String MAX_DISCOUNT = "maxDiscount";
    public static final String MIN_QUANTITY = "minQuantity";

    private Integer maxDiscount,minQuantity,discountPercentage;

    public DiscountOffer(Integer maxDiscount, Integer minQuantity, Integer discountPercentage) {
        this.maxDiscount = maxDiscount;
        this.minQuantity = minQuantity;
        this.discountPercentage = discountPercentage;
    }

    public DiscountOffer() {
    }

    public Integer getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(Integer maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public Integer getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

}
