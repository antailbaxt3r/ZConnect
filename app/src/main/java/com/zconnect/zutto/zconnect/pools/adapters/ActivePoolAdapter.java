package com.zconnect.zutto.zconnect.pools.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.holders.ActivePoolViewHolder;
import com.zconnect.zutto.zconnect.pools.models.ActivePool;

import java.util.ArrayList;

public class ActivePoolAdapter extends RecyclerView.Adapter<ActivePoolViewHolder> {

    private ArrayList<ActivePool> poolsList = new ArrayList<>();
    private Context context;

    public ActivePoolAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ActivePoolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_active_pool, parent, false);
        return new ActivePoolViewHolder(newView, context);
    }

    @Override
    public void onBindViewHolder(ActivePoolViewHolder holder, int position) {
        holder.populate(poolsList.get(position));
    }

    @Override
    public int getItemCount() {
        return poolsList.size();
    }

    public void insertAtEnd(ActivePool p) {
        poolsList.add(p);
        notifyItemInserted(poolsList.size() - 1);
    }
}
