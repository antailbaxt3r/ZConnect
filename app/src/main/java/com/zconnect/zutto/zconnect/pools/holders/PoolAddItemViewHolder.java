package com.zconnect.zutto.zconnect.pools.holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolAddItemAdapter;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

public class PoolAddItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Button increment, decrement, add;
    private RelativeLayout rl_btn;
    private SimpleDraweeView dishImage;
    private TextView name, description, quantity, price;
    private PoolAddItemAdapter adapter;
    private int number = 0;
    private PoolItem item;

    public PoolAddItemViewHolder(View itemView, PoolAddItemAdapter adapter) {
        super(itemView);
        this.adapter = adapter;

        attachID();
    }

    private void attachID() {
        name = itemView.findViewById(R.id.dish_name);
        increment = itemView.findViewById(R.id.increment_button);
        decrement = itemView.findViewById(R.id.decrement_button);
        price = itemView.findViewById(R.id.item_price);
        rl_btn = itemView.findViewById(R.id.user_quantity_details);
        add = itemView.findViewById(R.id.btn_first_add);
        dishImage = itemView.findViewById(R.id.pool_dish_image);
        description = itemView.findViewById(R.id.dish_description);
        quantity = itemView.findViewById(R.id.quantity_display);
    }

    public void populate(PoolItem item) {
        this.item = item;
        Log.d("PoolDishViewHolder", "populate : " + item.getName());
        name.setText(item.getName());
        quantity.setText(String.valueOf(item.getQuantity()));
        description.setText(item.getDescription());
        dishImage.setImageURI(item.getImageURL());
        number = item.getQuantity();
        price.setText(itemView.getContext().getResources().getString(R.string.Rs) + item.getPrice());
        increment.setOnClickListener(this);
        decrement.setOnClickListener(this);
        add.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.increment_button) {
            number++;
        } else if (id == R.id.decrement_button) {
            number--;
            if (number < 0) number = 0;
        } else if (id == R.id.btn_first_add) {
            number++;
        }
        updateUI(number);
        adapter.updateOrderDish(item, number);
    }

    private void updateUI(int p) {
        Log.d("PoolAdapter", "quantity : " + String.valueOf(p));
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
