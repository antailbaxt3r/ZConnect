package com.zconnect.zutto.zconnect.pools.holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolDishAdapter;
import com.zconnect.zutto.zconnect.pools.models.PoolDish;

public class PoolDishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private Button increment, decrement,add;
    private RelativeLayout rl_btn;
    private SimpleDraweeView dishImage;
    private TextView name, description, quantity;
    private PoolDishAdapter adapter;
    private int number = 0;
    private PoolDish dish;

    public PoolDishViewHolder(View itemView, PoolDishAdapter adapter) {
        super(itemView);
        this.adapter = adapter;

        attachID();
    }

    private void attachID() {
        name = itemView.findViewById(R.id.dish_name);
        increment = itemView.findViewById(R.id.increment_button);
        decrement = itemView.findViewById(R.id.decrement_button);
        rl_btn = itemView.findViewById(R.id.user_quantity_details);
        add = itemView.findViewById(R.id.btn_first_add);
        dishImage = itemView.findViewById(R.id.pool_dish_image);
        description = itemView.findViewById(R.id.dish_description);
        quantity = itemView.findViewById(R.id.quantity_display);
    }

    public void populate(PoolDish dish) {
        this.dish = dish;
        Log.d("PoolDishViewHolder", "populate : " + dish.getName());
        name.setText(dish.getName());
        quantity.setText(dish.getQuantity());
        description.setText(dish.getDescription());
        dishImage.setImageURI(dish.getImageURL());
        number = Integer.parseInt(dish.getQuantity());

        increment.setOnClickListener(this);
        decrement.setOnClickListener(this);
        add.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.increment_button){
            number++;
        }else if(id==R.id.decrement_button){
            number--;
            if(number < 0) number = 0;
        }else if(id==R.id.btn_first_add){
            number++;
        }
        updateUI(number);
        adapter.updateOrderDish(dish.getID(),number);
    }

    private void updateUI(int p) {
        Log.d("PoolAdapter","quantity : "+String.valueOf(p));
        if (p == 0) {
            add.setEnabled(true);
            add.setVisibility(View.VISIBLE);
            rl_btn.setVisibility(View.GONE);
        } else {
            add.setVisibility(View.GONE);
            rl_btn.setVisibility(View.VISIBLE);
            quantity.setText(String.valueOf(p));
        }
    }
}
