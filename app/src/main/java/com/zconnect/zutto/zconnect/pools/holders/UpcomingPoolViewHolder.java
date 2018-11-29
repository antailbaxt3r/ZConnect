package com.zconnect.zutto.zconnect.pools.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.AddPoolItemActivity;
import com.zconnect.zutto.zconnect.pools.PoolItemDetailActivity;
import com.zconnect.zutto.zconnect.pools.models.UpcomingPool;

public class UpcomingPoolViewHolder extends RecyclerView.ViewHolder {

    private TextView poolName,deliveryTime,joinedPeoples,offers;
    private SimpleDraweeView logo;
    private Context context;
    private UpcomingPool pool;

    public UpcomingPoolViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;

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
        this.pool = pool;
        poolName.setText(pool.getName());
        logo.setImageURI(pool.getImageURL());
        offers.setText(pool.getOffer());
        joinedPeoples.setText(pool.getUpVote());
        deliveryTime.setText(pool.getDeliveryTime());
        onclick();


    }
    private void onclick() {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PoolItemDetailActivity.class);
                intent.putExtra("newPool",pool.getBundle());
                context.startActivity(intent);

            }
        });
    }

}
