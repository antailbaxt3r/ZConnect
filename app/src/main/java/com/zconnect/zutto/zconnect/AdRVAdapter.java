package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.ItemFormats.AdItemFormat;

import java.util.Vector;

/**
 * Created by shubhamk on 10/2/17.
 */

public class AdRVAdapter extends RecyclerView.Adapter<AdRVAdapter.ViewHolder> {
    Context context;
    Vector<AdItemFormat> adItemFormats;

    public AdRVAdapter(Context context, Vector<AdItemFormat> adItemFormats) {
        this.context = context;
        this.adItemFormats = adItemFormats;
    }

    @Override
    public AdRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.ad_item_format, parent, false);

        // Return a new holder instance
        return new AdRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(AdRVAdapter.ViewHolder holder, int position) {

        holder.simpleDraweeView.setImageURI(Uri.parse(adItemFormats.get(position).getImageurl()));
    }

    @Override
    public int getItemCount() {
        return adItemFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView simpleDraweeView;

        public ViewHolder(View itemView) {
            super(itemView);

            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.ad_item_format_image);

        }
    }
}
