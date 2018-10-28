package com.zconnect.zutto.zconnect.pools.holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.models.PoolDish;

public class PoolDishViewHolder extends RecyclerView.ViewHolder {

    private Button increment, decrement;
    private SimpleDraweeView dishImage;
    private TextView name, description, quantity;

    public PoolDishViewHolder(View itemView) {
        super(itemView);

        attachID();
    }

    private void attachID() {
        name = itemView.findViewById(R.id.dish_name);
        increment = itemView.findViewById(R.id.increment_button);
        decrement = itemView.findViewById(R.id.decrement_button);
        dishImage = itemView.findViewById(R.id.pool_dish_image);
        description = itemView.findViewById(R.id.dish_description);
        quantity = itemView.findViewById(R.id.quantity_display);
    }

    public void populate(PoolDish dish) {
        Log.d("PoolDishViewHolder", "populate : " + dish.getName());
        name.setText(dish.getName());
        quantity.setText(dish.getQuantity());
        description.setText(dish.getDescription());
        dishImage.setImageURI(dish.getImageURL());

    }


}
