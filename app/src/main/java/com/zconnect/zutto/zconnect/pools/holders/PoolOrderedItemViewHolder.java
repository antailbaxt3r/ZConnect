package com.zconnect.zutto.zconnect.pools.holders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.OrderDetailActivity;
import com.zconnect.zutto.zconnect.pools.models.Order;

public class PoolOrderedItemViewHolder extends RecyclerView.ViewHolder {


    private TextView poolName, deliveryTime, amount;

    private String communityID;

    private Order order;

    public PoolOrderedItemViewHolder(View itemView) {
        super(itemView);


        attachID();
    }

    private void attachID() {

        //TODO load community id from preference
        communityID = "testCollege";

        poolName = itemView.findViewById(R.id.pool_name);
        deliveryTime = itemView.findViewById(R.id.delivery_time);
        amount = itemView.findViewById(R.id.ordered_amount);
    }

    public void populate(final Order order) {
        this.order = order;
        poolName.setText(order.getPoolName());
        amount.setText(String.format("%s%d", itemView.getContext().getResources().getString(R.string.Rs), order.getAmount()));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(),OrderDetailActivity.class);
                intent.putExtra("order",order.getBundle());
                itemView.getContext().startActivity(intent);
            }
        });
    }

}
