package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.NoticesRVViewHolder;
import com.zconnect.zutto.zconnect.holders.ProductsViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.NoticeItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.Product;

import java.util.Vector;

public class NoticeRVAdapter extends RecyclerView.Adapter<NoticesRVViewHolder>{

    Vector<NoticeItemFormat> photos;
    Context context;

    public NoticeRVAdapter(Vector<NoticeItemFormat> photos, Context context) {
        this.photos=photos;
        this.context = context;
    }


    @Override
    public NoticesRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.view_notices_item,parent,false);
        return new NoticesRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoticesRVViewHolder holder, int position) {
        holder.setImage(photos.get(position).getImageurl());
        holder.setText(photos.get(position).getName());
    }


    @Override
    public int getItemCount() {
        return photos.size();
    }
}
