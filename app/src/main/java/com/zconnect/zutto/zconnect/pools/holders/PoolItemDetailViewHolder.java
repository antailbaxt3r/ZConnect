package com.zconnect.zutto.zconnect.pools.holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

public class PoolItemDetailViewHolder extends RecyclerView.ViewHolder{


    private RelativeLayout rl_btn;
    private SimpleDraweeView dishImage;
    private TextView name, description;

    private PoolItem item;

    public PoolItemDetailViewHolder(View itemView) {
        super(itemView);


        attachID();
    }

    private void attachID() {
        name = itemView.findViewById(R.id.dish_name);
        dishImage = itemView.findViewById(R.id.pool_dish_image);
        description = itemView.findViewById(R.id.dish_description);
    }

    public void populate(PoolItem dish) {
        this.item = dish;
        Log.d(this.getClass().getName(), "populate : " + dish.getName());
        name.setText(dish.getName());

        description.setText(dish.getDescription());
        dishImage.setImageURI(dish.getImageURL());

    }
}
