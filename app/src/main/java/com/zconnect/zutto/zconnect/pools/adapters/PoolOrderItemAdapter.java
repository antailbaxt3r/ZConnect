package com.zconnect.zutto.zconnect.pools.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.holders.PoolOrderedItemViewHolder;
import com.zconnect.zutto.zconnect.pools.models.Order;

import java.util.ArrayList;

public class PoolOrderItemAdapter extends RecyclerView.Adapter<PoolOrderedItemViewHolder> {

    private ArrayList<Order> poolsList = new ArrayList<>();


    @Override
    public PoolOrderedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_pool_shop_ordered, parent, false);
        return new PoolOrderedItemViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(PoolOrderedItemViewHolder holder, int position) {
        holder.populate(poolsList.get(position));
    }

    @Override
    public int getItemCount() {
        return poolsList.size();
    }

    public void insertAtEnd(Order p) {
        poolsList.add(p);
        notifyItemInserted(poolsList.size() - 1);
    }

    public void clearDataset() {
        poolsList.clear();
        notifyDataSetChanged();
    }


    public void addAll(ArrayList<Order> arrayList) {
        this.poolsList = arrayList;
        notifyDataSetChanged();
    }
}
