package com.zconnect.zutto.zconnect.pools.holders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.OrderDetailActivity;
import com.zconnect.zutto.zconnect.pools.models.Order;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class PoolOrderedItemViewHolder extends RecyclerView.ViewHolder {


    private TextView poolName, deliveryTime, amount, orderPlaceTimeTV;

    private Order order;

    public PoolOrderedItemViewHolder(View itemView) {
        super(itemView);
        attachID();
    }

    private void attachID() {
        poolName = itemView.findViewById(R.id.pool_name);
        deliveryTime = itemView.findViewById(R.id.delivery_time);
        amount = itemView.findViewById(R.id.ordered_amount);
        orderPlaceTimeTV = itemView.findViewById(R.id.order_place_time);
    }

    public void populate(final Order order) {
        this.order = order;
        poolName.setText(order.getPoolInfo().getName());
        amount.setText(String.format("%s%d", itemView.getContext().getResources().getString(R.string.Rs), order.getTotalAmount()));
        long timeStamp = order.getTimestampPaymentAfter();
        DateTimeZone indianZone = DateTimeZone.forID("Asia/Kolkata");
        DateTime dateTime = new DateTime(timeStamp, indianZone);
        String dateTimeText = itemView.getContext().getResources().getString(R.string.order_placed_on)
                + " " + dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour() + ", " + dateTime.getDayOfMonth() + " " + dateTime.toString("MMM") + " " + dateTime.getYearOfEra();
        orderPlaceTimeTV.setText(dateTimeText);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(),OrderDetailActivity.class);
                intent.putExtra("order",order);
                itemView.getContext().startActivity(intent);
            }
        });
    }

}
