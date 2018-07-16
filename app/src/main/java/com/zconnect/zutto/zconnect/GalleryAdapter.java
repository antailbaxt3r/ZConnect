package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.itemFormats.GalleryFormat;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Lokesh Garg on 31-03-2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    Context context;
    Vector<GalleryFormat> galleryItem;

    public GalleryAdapter(Context context, Vector<GalleryFormat> galleryItem) {
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
        if(galleryItem.get(position).getImageurl()!=null){
      holder.image.setImageURI(Uri.parse(galleryItem.get(position).getImageurl()));
        }}

    @Override
    public int getItemCount() {
        return galleryItem.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView image;
        ArrayList<String>arrayList=new ArrayList<>();
        public ViewHolder(View itemView) {
            super(itemView);
            image = (SimpleDraweeView) itemView.findViewById(R.id.galleryImage);
            itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               arrayList.clear();
               Intent intent = new Intent(context,GalleryActivity.class);
               for (int i=0;i<galleryItem.size();i++){
                   arrayList.add(galleryItem.get(i).getImageurl());
               }
               intent.putStringArrayListExtra(GalleryActivity.EXTRA_NAME,arrayList);
               context.startActivity(intent);
           }
       });
        }
    }

}
