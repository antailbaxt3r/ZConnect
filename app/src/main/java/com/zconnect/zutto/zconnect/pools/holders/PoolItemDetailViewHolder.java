package com.zconnect.zutto.zconnect.pools.holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

public class PoolItemDetailViewHolder extends RecyclerView.ViewHolder {


    private SimpleDraweeView dishImage;
    private TextView name, description, price;

    private PoolItem item;

    public PoolItemDetailViewHolder(View itemView) {
        super(itemView);


        attachID();
    }

    private void attachID() {
        name = itemView.findViewById(R.id.item_name);
        dishImage = itemView.findViewById(R.id.item_image);
        description = itemView.findViewById(R.id.item_description);
        price = itemView.findViewById(R.id.item_price);
    }

    public void populate(PoolItem dish) {
        this.item = dish;
        Log.d(this.getClass().getName(), "populate : " + dish.getName());
        name.setText(dish.getName());
        description.setText(dish.getDescription());
        dishImage.setImageURI(dish.getImageURL());
        price.setText(String.format("Price %s%d", itemView.getContext().getResources().getString(R.string.Rs), dish.getPrice()));
    }
}
