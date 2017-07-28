package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;

import java.util.Vector;

/**
 * Created by shubhamk on 26/7/17.
 */

public class CabPoolRVAdapter extends RecyclerView.Adapter<CabPoolRVAdapter.ViewHolder> {
    Context context;
    Vector<CabItemFormat> cabItemFormats;

    public CabPoolRVAdapter(Context context, Vector<CabItemFormat> cabItemFormats) {
        this.context = context;
        this.cabItemFormats = cabItemFormats;
    }

    @Override
    public CabPoolRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.cabpool_item_format, parent, false);
        return new CabPoolRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(CabPoolRVAdapter.ViewHolder holder, int position) {
        holder.date.setText(cabItemFormats.get(position).getDate());
        holder.details.setText(cabItemFormats.get(position).getDetails());
        holder.destination.setText(cabItemFormats.get(position).getDestination());
        holder.source.setText(cabItemFormats.get(position).getSource());
        holder.time.setText(cabItemFormats.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return cabItemFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
       TextView source,destination,details,time,date;
        String key;
        Button list_people;
        public ViewHolder(View itemView) {
            super(itemView);
            source =(TextView)itemView.findViewById(R.id.source);
            destination =(TextView)itemView.findViewById(R.id.destination);
            details=(TextView)itemView.findViewById(R.id.details);
            time=(TextView)itemView.findViewById(R.id.time_range);
            date=(TextView)itemView.findViewById(R.id.date);
            list_people = (Button) itemView.findViewById(R.id.list);

            list_people.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CabListOfPeople.class);
                    intent.putExtra("key", cabItemFormats.get(getAdapterPosition()).getKey());

                    context.startActivity(intent);
                }
            });
        }
    }
}
