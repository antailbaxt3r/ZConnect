package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.ItemFormats.CabListItemFormat;

import java.util.Vector;

/**
 * Created by shubhamk on 27/7/17.
 */

public class CabPeopleRVAdapter extends RecyclerView.Adapter<CabPeopleRVAdapter.ViewHolder> {
    Context context;
    Vector<CabListItemFormat> cabListItemFormats;

    public CabPeopleRVAdapter(Context context, Vector<CabListItemFormat> cabListItemFormats) {
        this.context = context;
        this.cabListItemFormats = cabListItemFormats;
    }

    @Override
    public CabPeopleRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.cab_people_item_format, parent, false);
        return new CabPeopleRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(CabPeopleRVAdapter.ViewHolder holder, int position) {
        holder.name.setText(cabListItemFormats.get(position).getName());
        holder.number.setText(cabListItemFormats.get(position).getPhonenumber());
    }

    @Override
    public int getItemCount() {
        return cabListItemFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        ImageView call;

        public ViewHolder(View itemView) {
            super(itemView);
            call = (ImageView) itemView.findViewById(R.id.callbutton);
            name = (TextView) itemView.findViewById(R.id.cab_name);
            number = (TextView) itemView.findViewById(R.id.cab_number);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:" + Long.parseLong(number.getText().toString().trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }
    }
}
