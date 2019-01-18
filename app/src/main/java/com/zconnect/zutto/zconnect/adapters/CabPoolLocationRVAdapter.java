package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.itemFormats.CabPoolLocationFormat;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.CabPoolLocationRVViewHolder;

import java.util.Vector;

public class CabPoolLocationRVAdapter extends RecyclerView.Adapter<CabPoolLocationRVViewHolder> {
    Vector<CabPoolLocationFormat> cabPoolLocationFormat;
    Context ctx;

    public CabPoolLocationRVAdapter(Context ctx, Vector<CabPoolLocationFormat> cabPoolLocationFormat){
        this.ctx= ctx;
        this.cabPoolLocationFormat = cabPoolLocationFormat;
    }


    @Override
    public CabPoolLocationRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cab_pool_locations_row, parent, false);
        return new CabPoolLocationRVViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CabPoolLocationRVViewHolder holder, int position) {
        try {
            holder.setLocationName(cabPoolLocationFormat.get(position).getLocationName());
            holder.setDeleteButton(cabPoolLocationFormat.get(position).getLocationUID(),cabPoolLocationFormat.get(position).getLocationName());
        }catch (Exception e){}

    }

    @Override
    public int getItemCount() {
        return cabPoolLocationFormat.size();
    }
}
