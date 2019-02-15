package com.zconnect.zutto.zconnect.pools.holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

public class PoolItemCartViewHolder extends RecyclerView.ViewHolder {

    private TextView name, amount;

    public PoolItemCartViewHolder(View itemView) {
        super(itemView);
        attachID();
    }

    private void attachID() {
        name = itemView.findViewById(R.id.item_name_cum_quantity);
        amount = itemView.findViewById(R.id.item_row_amount);
    }

    public void populate(PoolItem dish) {
        Log.d(this.getClass().getName(), "populate : " + dish.getName());
        String name_cum_quantity = dish.getName() + " X " + dish.getQuantity();
        name.setText(name_cum_quantity);
        amount.setText(String.format("%s%d", itemView.getContext().getResources().getString(R.string.Rs), dish.getPrice() * dish.getQuantity()));
    }
}
