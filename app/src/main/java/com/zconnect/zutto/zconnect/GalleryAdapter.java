package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Vector;

/**
 * Created by Lokesh Garg on 31-03-2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    Context context;
    Vector<String> galleryItem;

    public GalleryAdapter(Context context, Vector<String> galleryItem) {
        this.context = context;
        this.galleryItem = galleryItem;
    }


    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.gallery_row, parent, false);

        // Return a new holder instance
        return new GalleryAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position) {
        Picasso.with(context).load(galleryItem.get(position).toString()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return galleryItem.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.galleryRecycler);
        }
    }

}
