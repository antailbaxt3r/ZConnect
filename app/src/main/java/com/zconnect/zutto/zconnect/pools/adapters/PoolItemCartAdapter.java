package com.zconnect.zutto.zconnect.pools.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.holders.PoolItemCartViewHolder;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

import java.util.ArrayList;

public class PoolItemCartAdapter extends RecyclerView.Adapter<PoolItemCartViewHolder> {

    private ArrayList<PoolItem> poolsList = new ArrayList<>();


    @Override
    public PoolItemCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_pool_item_cart, parent, false);
        return new PoolItemCartViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(PoolItemCartViewHolder holder, int position) {
        holder.populate(poolsList.get(position));
    }

    @Override
    public int getItemCount() {
        return poolsList.size();
    }

    public void insertAtEnd(PoolItem p) {
        poolsList.add(p);
        notifyItemInserted(poolsList.size() - 1);
    }

    public void clearDataset() {
        poolsList.clear();
        notifyDataSetChanged();
    }

    public ArrayList<PoolItem> getPoolsList() {
        return poolsList;
    }
}
