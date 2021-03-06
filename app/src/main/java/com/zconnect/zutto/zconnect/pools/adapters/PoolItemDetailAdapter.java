package com.zconnect.zutto.zconnect.pools.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.holders.PoolItemDetailViewHolder;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

import java.util.ArrayList;

public class PoolItemDetailAdapter extends RecyclerView.Adapter<PoolItemDetailViewHolder> {

    private ArrayList<PoolItem> poolsList = new ArrayList<>();


    @Override
    public PoolItemDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_pool_item_details, parent, false);
        return new PoolItemDetailViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(PoolItemDetailViewHolder holder, int position) {
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
}
