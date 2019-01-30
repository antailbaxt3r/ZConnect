package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    Vector<NoticeItemFormat> notices;
    String userType;
    Context context;

    public NoticeRVAdapter(Vector<NoticeItemFormat> notices, Context context, String userType) {
        this.notices=notices;
        this.context = context;
        this.userType = userType;
    }


    @Override
    public NoticesRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.view_notices_item,parent,false);
        return new NoticesRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoticesRVViewHolder holder, int position) {
        holder.setImage(notices.get(position).getImageThumbURL(),notices.get(position).getImageURL(),notices.get(position).getTitle());
        holder.setText(notices.get(position).getTitle());
        holder.setDeleteButton(userType,notices.get(position).getKey());
        if(notices.get(position).getExpiryDate()!=null)
        {
            holder.setExpiryDate(notices.get(position).getExpiryDate());
        }
        else
        {
            holder.hideExpiryDateLayout();
        }
    }


    @Override
    public int getItemCount() {
        return notices.size();
    }
}
