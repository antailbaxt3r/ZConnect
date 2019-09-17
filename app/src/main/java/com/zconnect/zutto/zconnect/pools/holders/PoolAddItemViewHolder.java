package com.zconnect.zutto.zconnect.pools.holders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.viewImage;
import com.zconnect.zutto.zconnect.pools.adapters.PoolAddItemAdapter;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

public class PoolAddItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Button increment, decrement, add;
    private RelativeLayout rl_btn, part2;
    private SimpleDraweeView dishImage;
    private TextView name, description, quantity, price;
    private PoolAddItemAdapter adapter;
    private int number = 0;
    private PoolItem item;
    private LinearLayout mask;

    public PoolAddItemViewHolder(View itemView, PoolAddItemAdapter adapter) {
        super(itemView);
        this.adapter = adapter;

        attachID();
    }

    private void attachID() {
        name = itemView.findViewById(R.id.item_name);
        increment = itemView.findViewById(R.id.increment_button);
        decrement = itemView.findViewById(R.id.decrement_button);
        price = itemView.findViewById(R.id.item_price);
        rl_btn = itemView.findViewById(R.id.user_quantity_details);
        add = itemView.findViewById(R.id.btn_first_add);
        dishImage = itemView.findViewById(R.id.item_image);
        description = itemView.findViewById(R.id.item_description);
        quantity = itemView.findViewById(R.id.quantity_display);
        mask = itemView.findViewById(R.id.mask_layer);
        part2 = itemView.findViewById(R.id.part2);
    }

    public void populate(final PoolItem item) {
        this.item = item;
        Log.d("PoolDishViewHolder", "populate : " + item.getName());
        name.setText(item.getName());
        quantity.setText(String.valueOf(item.getQuantity()));
        description.setText(item.getDescription());
        dishImage.setImageURI(item.getImageThumb());
        number = item.getQuantity();
        price.setText(itemView.getContext().getResources().getString(R.string.Rs) + item.getPrice());
        increment.setOnClickListener(this);
        decrement.setOnClickListener(this);
        add.setOnClickListener(this);
        if (!item.isVisible()){
            mask.setVisibility(View.VISIBLE);
            part2.setVisibility(View.INVISIBLE);
            add.setClickable(false);
            name.setTextColor(itemView.getResources().getColor(R.color.grey300));
            itemView.setClickable(false);

        }else{
            mask.setVisibility(View.GONE);
            part2.setVisibility(View.VISIBLE);
        }


        dishImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressDialog mProgress = new ProgressDialog(itemView.getContext());
                mProgress.setMessage("Loading...");
                mProgress.show();
                animate((Activity)itemView.getContext(), item.getName(), item.getImageURL(), dishImage);
                mProgress.dismiss();

            }
        });

    }

    public void animate(final Activity activity, final String name, String url, ImageView productImage) {
        final Intent i = new Intent(itemView.getContext(), viewImage.class);
        i.putExtra("currentEvent", name);
        i.putExtra("eventImage", url);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, productImage, itemView.getContext().getResources().getString(R.string.transition_string));

        itemView.getContext().startActivity(i, optionsCompat.toBundle());
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
