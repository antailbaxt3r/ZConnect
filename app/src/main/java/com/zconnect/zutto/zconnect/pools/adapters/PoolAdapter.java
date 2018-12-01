package com.zconnect.zutto.zconnect.pools.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.holders.PoolViewHolder;
import com.zconnect.zutto.zconnect.pools.models.Pool;

import java.util.ArrayList;

public class PoolAdapter extends RecyclerView.Adapter<PoolViewHolder> {

    private ArrayList<Pool> poolsList = new ArrayList<>();


    @Override
    public PoolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_pool, parent, false);
        return new PoolViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(PoolViewHolder holder, int position) {
        holder.populate(poolsList.get(position));
    }

    @Override
    public int getItemCount() {
        return poolsList.size();
    }

    public void insertAtEnd(Pool p) {
        poolsList.add(p);
        notifyItemInserted(poolsList.size() - 1);
    }

    public void clearDataset() {
        poolsList.clear();
        notifyDataSetChanged();
    }

    public void removePool(Pool pool) {
        for (int i = 0; i < poolsList.size(); i++) {
            if (poolsList.get(i).getID().compareTo(pool.getID()) == 0) {
                poolsList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void updatePool(Pool pool) {
        for (int i = 0; i < poolsList.size(); i++) {
            if (poolsList.get(i).getID().compareTo(pool.getID()) == 0) {
                poolsList.set(i, pool);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public Pool getPool(String id) {
        for (int i = 0; i < poolsList.size(); i++) {
            if (poolsList.get(i).getID().compareTo(id) == 0) {
                return poolsList.get(i);
            }
        }
        return null;
    }
}
