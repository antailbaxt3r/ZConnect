package com.zconnect.zutto.zconnect.pools.holders;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.AddPoolItemActivity;
import com.zconnect.zutto.zconnect.pools.PoolItemDetailActivity;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.pools.models.ShopOrder;

import org.w3c.dom.Text;

import java.util.Date;

public class PoolOrderedItemViewHolder extends RecyclerView.ViewHolder {


    private TextView poolName,deliveryTime,amount;

    private String communityID;

    private ShopOrder order;

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

    public void populate(ShopOrder order) {
        this.order = order;
        poolName.setText(order.getPoolName());
        amount.setText(String.format("%s%d",itemView.getContext().getResources().getString(R.string.Rs),order.getAmount()));
    }

}
