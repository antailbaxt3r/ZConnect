package com.zconnect.zutto.zconnect.pools.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.PoolDetailsActivity;
import com.zconnect.zutto.zconnect.pools.models.ActivePool;

public class ActivePoolViewHolder extends RecyclerView.ViewHolder {

    private TextView poolName,deliveryTime,joinedPeoples,offers;
    private SimpleDraweeView logo;
    private ActivePool pool;


    private Context context;

    public ActivePoolViewHolder(View itemView, Context context) {
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

    public void populate(ActivePool pool){
        this.pool = pool;
        poolName.setText(pool.getName());
        logo.setImageURI(pool.getImageURL());
        offers.setText(pool.getOffer());
        joinedPeoples.setText(pool.getJoined());
        deliveryTime.setText(pool.getDeliveryTime());
        onclick();
    }

    private void onclick() {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PoolDetailsActivity.class);
                intent.putExtra("newPool",pool.getBundle());
                context.startActivity(intent);

            }
        });
    }

}
