package com.zconnect.zutto.zconnect.pools.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.holders.UpcomingPoolViewHolder;
import com.zconnect.zutto.zconnect.pools.models.UpcomingPool;

import java.util.ArrayList;

public class UpcomingPoolAdapter extends RecyclerView.Adapter<UpcomingPoolViewHolder> {

    private ArrayList<UpcomingPool> poolsList = new ArrayList<>();
    private Context context;

    public UpcomingPoolAdapter(Context context) {
        this.context = context;
    }

    @Override
    public UpcomingPoolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_active_pool, parent, false);
        return new UpcomingPoolViewHolder(newView,context);
    }

    @Override
    public void onBindViewHolder(UpcomingPoolViewHolder holder, int position) {
        holder.populate(poolsList.get(position));
    }

    @Override
    public int getItemCount() {
        return poolsList.size();
    }

    public void insertAtEnd(UpcomingPool p) {
        poolsList.add(p);
        notifyItemInserted(poolsList.size() - 1);
    }

    public void updatePool(UpcomingPool newPool) {
        for(int i = 0 ; i < poolsList.size() ;i++){
            if(poolsList.get(i).getID().compareTo(newPool.getID())==0){
                poolsList.set(i,newPool);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void removePool(UpcomingPool pool) {
        for(int i = 0 ; i < poolsList.size() ;i++){
            if(poolsList.get(i).getID().compareTo(pool.getID())==0){
                poolsList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }
}
