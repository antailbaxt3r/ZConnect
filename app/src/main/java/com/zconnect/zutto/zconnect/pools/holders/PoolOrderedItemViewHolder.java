package com.zconnect.zutto.zconnect.pools.holders;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.pools.OrderDetailActivity;
import com.zconnect.zutto.zconnect.pools.models.Order;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.HashMap;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class PoolOrderedItemViewHolder extends RecyclerView.ViewHolder {


    private TextView poolName, deliveryTime, amount, orderPlaceTimeTV,paymentStatusText,orderStatusText;

    private Order order;

    public PoolOrderedItemViewHolder(View itemView) {
        super(itemView);
        attachID();
    }

    private void attachID() {
        poolName = itemView.findViewById(R.id.pool_name);
        deliveryTime = itemView.findViewById(R.id.order_delivery_time);
        amount = itemView.findViewById(R.id.ordered_amount);
        orderPlaceTimeTV = itemView.findViewById(R.id.order_place_time);

        paymentStatusText = itemView.findViewById(R.id.payment_status);

        orderStatusText = itemView.findViewById(R.id.order_status);
    }

    public void populate(final Order order) {
        this.order = order;
        poolName.setText(order.getPoolInfo().getName());
        amount.setText(String.format("%s%s", itemView.getContext().getResources().getString(R.string.Rs), String.valueOf(order.getTotalAmount())));
        long timeStamp = order.getTimestampPaymentAfter()==0?order.getTimestampPaymentBefore():order.getTimestampPaymentAfter();
        TimeUtilities tu = new TimeUtilities(timeStamp);
        String dateTimeText = itemView.getContext().getResources().getString(R.string.order_placed_on) + " " + tu.getTimeInHHMMAPM() + ", " + tu.getDateTime().getDayOfMonth() + " " + tu.getMonthName("SHORT") + " " + tu.getDateTime().getYearOfEra();
        timeStamp = order.getDeliveryTime();
        orderPlaceTimeTV.setText(dateTimeText);
        tu = new TimeUtilities(timeStamp);
        dateTimeText = itemView.getContext().getResources().getString(R.string.order_delivery_on) + " " + tu.getTimeInHHMMAPM() + ", " + tu.getDateTime().getDayOfMonth() + " " + tu.getMonthName("SHORT") + " " + tu.getDateTime().getYearOfEra();
        deliveryTime.setText(dateTimeText);
        setOrderStatus(order.getOrderStatus());
        setPaymentStatus(order.getPaymentStatus(), order.getOrderStatus(), order.getPaymentGatewayID());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_SHOPS_ORDER_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                Intent intent = new Intent(itemView.getContext(),OrderDetailActivity.class);
                intent.putExtra("order",order);
                itemView.getContext().startActivity(intent);
            }
        });
    }

    private void setOrderStatus(String orderStatus){

        if(orderStatus==null)
        {
            orderStatusText.setVisibility(View.GONE);
            paymentStatusText.setText("Payment Failed");
            paymentStatusText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
        else
        {
            orderStatusText.setVisibility(View.VISIBLE);
            if(orderStatus.equals(Order.KEY_ORDER_OUT_FOR_DELIVERY)){
                orderStatusText.setText("Order Confirmed");
                orderStatusText.getBackground().setColorFilter(itemView.getResources().getColor(R.color.blue500), PorterDuff.Mode.SRC_ATOP);
            }else if(orderStatus.equals(Order.KEY_ORDER_DELIVERED)){
                orderStatusText.setText("Delivered");
                paymentStatusText.getBackground().setColorFilter(itemView.getResources().getColor(R.color.deeporange500), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    private void setPaymentStatus(String paymentStatus, String orderStatus, String paymentgatewayID){

        if (paymentStatus.equals(Order.KEY_PAYMENT_PROCESSING)){
            paymentStatusText.setText("Payment Processing...");
            paymentStatusText.getBackground().setColorFilter(itemView.getResources().getColor(R.color.grey700), PorterDuff.Mode.SRC_ATOP);
        }else if(paymentStatus.equals(Order.KEY_PAYMENT_FAIL) || paymentStatus.equals(Order.KEY_PAYMENT_PENDING)){
            paymentStatusText.setText("Payment Failed");
            paymentStatusText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }else if(paymentStatus.equals(Order.KEY_PAYMENT_SUCCESS)){
            paymentStatusText.setText("Payment Done");
            paymentStatusText.getBackground().setColorFilter(itemView.getResources().getColor(R.color.green500), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
