package com.zconnect.zutto.zconnect.pools.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.models.UpcomingPool;

public class UpcomingPoolViewHolder extends RecyclerView.ViewHolder {

    private TextView poolName,deliveryTime,joinedPeoples,offers;
    private SimpleDraweeView logo;

    public UpcomingPoolViewHolder(View itemView) {
        super(itemView);

        attachID();
    }

    private void attachID() {
        poolName = itemView.findViewById(R.id.pool_name);
        deliveryTime = itemView.findViewById(R.id.delivery_time);
        joinedPeoples= itemView.findViewById(R.id.joined_peoples);
        offers = itemView.findViewById(R.id.pool_offers);
        logo = itemView.findViewById(R.id.pool_logo);
    }
    public void populate(UpcomingPool pool){
        poolName.setText(pool.getName());
        logo.setImageURI(pool.getImageURL());
        offers.setText(pool.getOffer());
        joinedPeoples.setText(pool.getJoined());
        deliveryTime.setText(pool.getDeliveryTime());
    }

}
