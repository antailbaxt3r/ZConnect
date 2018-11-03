package com.zconnect.zutto.zconnect.pools.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.holders.PoolDishViewHolder;
import com.zconnect.zutto.zconnect.pools.models.PoolDish;

import java.util.ArrayList;

public class PoolDishAdapter extends RecyclerView.Adapter<PoolDishViewHolder> {

    private ArrayList<PoolDish> poolsList = new ArrayList<>();


    @Override
    public PoolDishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_pool_dish, parent, false);
        return new PoolDishViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(PoolDishViewHolder holder, int position) {
        holder.populate(poolsList.get(position));
    }

    @Override
    public int getItemCount() {
        return poolsList.size();
    }

    public void insertAtEnd(PoolDish p) {
        poolsList.add(p);
        notifyItemInserted(poolsList.size() - 1);
    }
}
