package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.ItemFormats.RecentsItemFormat;

import java.util.Vector;

/**
 * Created by shubhamk on 20/3/17.
 */

public class RecentsRVAdapter extends RecyclerView.Adapter<RecentsRVAdapter.ViewHolder> {

    Context context;
    Vector<RecentsItemFormat> recentsItemFormats;

    public RecentsRVAdapter(Context context, Vector<RecentsItemFormat> recentsItemFormats) {
        this.context = context;
        this.recentsItemFormats = recentsItemFormats;
    }

    @Override
    public RecentsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recents_item_format, parent, false);
        return new RecentsRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(RecentsRVAdapter.ViewHolder holder, int position) {
        holder.simpleDraweeView.setImageURI(Uri.parse(recentsItemFormats.get(position).getImageurl()));
        holder.feature.setText(recentsItemFormats.get(position).getFeature());
        holder.name.setText(recentsItemFormats.get(position).getName());
        holder.desc.setText(recentsItemFormats.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return recentsItemFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView feature, name, desc;
        SimpleDraweeView simpleDraweeView;

        public ViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.recents_image);
            name = (TextView) itemView.findViewById(R.id.recentname);
            feature = (TextView) itemView.findViewById(R.id.featurename);
            desc = (TextView) itemView.findViewById(R.id.recentdesc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Event")) {
                        Intent i = new Intent(context, OpenEventDetail.class);
                        i.putExtra("name", recentsItemFormats.get(getAdapterPosition()).getName());
                        i.putExtra("desc", recentsItemFormats.get(getAdapterPosition()).getDesc());
                        i.putExtra("date", recentsItemFormats.get(getAdapterPosition()).getDesc2());
                        i.putExtra("image", recentsItemFormats.get(getAdapterPosition()).getImageurl());
                        context.startActivity(i);
                    }
                }
            });
        }
    }
}
