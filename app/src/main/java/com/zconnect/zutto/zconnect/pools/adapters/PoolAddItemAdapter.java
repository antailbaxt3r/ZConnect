package com.zconnect.zutto.zconnect.pools.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.holders.PoolAddItemViewHolder;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

import java.util.ArrayList;
import java.util.HashMap;

public class PoolAddItemAdapter extends RecyclerView.Adapter<PoolAddItemViewHolder> {

    private ArrayList<PoolItem> poolsList = new ArrayList<>();
    private HashMap<String, Integer> mp = new HashMap<>();


    @Override
    public PoolAddItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_pool_add_item, parent, false);
        return new PoolAddItemViewHolder(newView, this);
    }

    @Override
    public void onBindViewHolder(PoolAddItemViewHolder holder, int position) {
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

    public void updateOrderDish(String id, int number) {
        if (number == 0) {
            if (mp.containsKey(id)) mp.remove(id);
        } else {
            mp.put(id, number);
        }
    }

    public ArrayList<PoolItem> getPoolsList() {
        return poolsList;
    }

    public HashMap<String, Integer> getMp() {
        return mp;
    }
}
