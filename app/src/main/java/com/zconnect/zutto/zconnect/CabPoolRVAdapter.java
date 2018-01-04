package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
    String url = "https://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect";

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
        Button list_people, share;
        public ViewHolder(View itemView) {
            super(itemView);
            source =(TextView)itemView.findViewById(R.id.source);
            destination =(TextView)itemView.findViewById(R.id.destination);
            time=(TextView)itemView.findViewById(R.id.time_range);
            date=(TextView)itemView.findViewById(R.id.date);
            list_people = (Button) itemView.findViewById(R.id.list);
            share = (Button) itemView.findViewById(R.id.sharecab);
            list_people.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CabListOfPeople.class);
                    intent.putExtra("key", cabItemFormats.get(getAdapterPosition()).getKey());

                    context.startActivity(intent);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CounterManager.searchPool(cabItemFormats.get(getAdapterPosition()).getKey());
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Join my cabpool from " + cabItemFormats.get(getAdapterPosition()).getSource() +
                            " to " + cabItemFormats.get(getAdapterPosition()).getDestination() + " on " +
                            cabItemFormats.get(getAdapterPosition()).getDate() +
                            "\n Use the ZConnect app to join the pool \n"
                            + url);
                    intent.setType("text/plain");
                    context.startActivity(Intent.createChooser(intent, "Share app url via ... "));
                }
            });
            Typeface customFont = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            Typeface customFont2 = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
            source.setTypeface(customFont2);
            destination.setTypeface(customFont2);
//            details.setTypeface(customFont2);
            time.setTypeface(customFont2);
            date.setTypeface(customFont2);
            list_people.setTypeface(customFont2);
            share.setTypeface(customFont2);

            TextView source_head = (TextView)itemView.findViewById(R.id.source_head);
            TextView destination_head = (TextView)itemView.findViewById(R.id.destination_head);
            TextView date_head = (TextView)itemView.findViewById(R.id.date_head);
            TextView time_head = (TextView)itemView.findViewById(R.id.time_head);

            source_head.setTypeface(customFont);
            destination_head.setTypeface(customFont);
            date_head.setTypeface(customFont);
            time_head.setTypeface(customFont);

        }
    }
}
