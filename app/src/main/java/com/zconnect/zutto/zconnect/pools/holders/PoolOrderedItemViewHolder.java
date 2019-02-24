package com.zconnect.zutto.zconnect.pools.holders;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.OrderDetailActivity;
import com.zconnect.zutto.zconnect.pools.models.Order;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class PoolOrderedItemViewHolder extends RecyclerView.ViewHolder {


    private TextView poolName, deliveryTime, amount, orderPlaceTimeTV,paymentStatusText,orderStatusText;
    private ImageView paymentStatusIcon,orderStatusIcon;

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

        paymentStatusText = itemView.findViewById(R.id.payment_status);
        paymentStatusIcon = itemView.findViewById(R.id.payment_status_icon);

        orderStatusText = itemView.findViewById(R.id.order_status);
        orderStatusIcon = itemView.findViewById(R.id.order_status_icon);
    }

    public void populate(final Order order) {
        this.order = order;
        poolName.setText(order.getPoolInfo().getName());
        amount.setText(String.format("%s%d", itemView.getContext().getResources().getString(R.string.Rs), order.getTotalAmount()));
        long timeStamp = order.getTimestampPaymentAfter();
        DateTimeZone indianZone = DateTimeZone.forID("Asia/Kolkata");
        DateTime dateTime = new DateTime(timeStamp, indianZone);


            setOrderStatus(order.getOrderStatus());
            setPaymentStatus(order.getPaymentStatus());



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

    private void setOrderStatus(String orderStatus){

        if(orderStatus.equals(Order.KEY_ORDER_OUT_FOR_DELIVERY)){
            orderStatusText.setText("Order Confirmed");
            orderStatusText.setTextColor(Color.YELLOW);
            orderStatusIcon.setBackgroundColor(Color.YELLOW);
        }else if(orderStatus.equals(Order.KEY_ORDER_DELIVERED)){
            orderStatusText.setText("Delivered");
            orderStatusText.setTextColor(Color.GREEN);
            orderStatusIcon.setBackgroundColor(Color.GREEN);
        }

    }

    private void setPaymentStatus(String paymentStatus){

        if (paymentStatus.equals(Order.KEY_PAYMENT_PROCESSING)){

            paymentStatusText.setText("Payment Processing");
            paymentStatusText.setTextColor(Color.YELLOW);
            paymentStatusIcon.setBackgroundColor(Color.YELLOW);

        }else if(paymentStatus.equals(Order.KEY_PAYMENT_FAIL)){
            paymentStatusText.setText("Payment Failed");
            paymentStatusText.setTextColor(Color.RED);
            paymentStatusIcon.setBackgroundColor(Color.RED);

        }else if(paymentStatus.equals(Order.KEY_PAYMENT_SUCCESS)){
            paymentStatusText.setText("Payment Done");
            paymentStatusText.setTextColor(Color.GREEN);
            paymentStatusIcon.setBackgroundColor(Color.GREEN);
        }

    }

}
