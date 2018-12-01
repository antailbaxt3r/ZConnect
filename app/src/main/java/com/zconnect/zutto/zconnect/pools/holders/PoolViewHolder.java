package com.zconnect.zutto.zconnect.pools.holders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.AddPoolItemActivity;
import com.zconnect.zutto.zconnect.pools.PoolItemDetailActivity;
import com.zconnect.zutto.zconnect.pools.models.Pool;

import java.util.Date;

public class PoolViewHolder extends RecyclerView.ViewHolder {


    private SimpleDraweeView poolImage;
    private TextView name, description, count, deliveryTime;

    private Pool pool;

    public PoolViewHolder(View itemView) {
        super(itemView);


        attachID();
    }

    private void attachID() {
        name = itemView.findViewById(R.id.pool_name);
        poolImage = itemView.findViewById(R.id.pool_logo);
        description = itemView.findViewById(R.id.pool_description);
        count = itemView.findViewById(R.id.pool_count);
        deliveryTime = itemView.findViewById(R.id.delivery_time);
    }

    public void populate(final Pool pool) {
        this.pool = pool;
        Log.d(this.getClass().getName(), "populate : " + pool.getName());
        name.setText(pool.getName());
        description.setText(pool.getDescription());
        poolImage.setImageURI(pool.getImageURL());
        if (pool.getStatus().compareTo(Pool.STATUS_UPCOMING) == 0) {
            count.setText(String.valueOf(pool.getUpVote()));
        } else {
            count.setText(String.valueOf(pool.getTotalOrder()));
        }
        Date d = new Date();
        d.setTime(pool.getDeliveryTime());
        deliveryTime.setText(d.toString());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pool.isUpcoming()) {
                    Intent intent = new Intent(itemView.getContext(), PoolItemDetailActivity.class);
                    intent.putExtra("newPool", pool.getBundle());
                    itemView.getContext().startActivity(intent);
                } else if (pool.isActive()) {
                    Intent intent = new Intent(itemView.getContext(), AddPoolItemActivity.class);
                    intent.putExtra("newPool", pool.getBundle());
                    itemView.getContext().startActivity(intent);
                }
            }
        });
    }
}
