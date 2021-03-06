package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.itemFormats.Event;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.EventsViewHolder;

import java.util.Vector;

/**
 * Created by Lokesh Garg on 23-04-2018.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsViewHolder> {

    private Context ctx;
    private Vector<Event> eventsVector;
    private String type;
    public EventsAdapter(Context ctx, Vector<Event> eventsVector,String type){
        this.ctx = ctx;
        this.eventsVector = eventsVector;
        this.type = type;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = null;
        if(type.equals("timeline")){
            view = layoutInflater.inflate(R.layout.timeline_event_row, parent, false);
        }else if (type.equals("trending")){
            view = layoutInflater.inflate(R.layout.trending_events_row, parent, false);
            ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.trending_event_image_progress_circle);
            SimpleDraweeView image = (SimpleDraweeView) view.findViewById(R.id.er_postImg);
            progressBar.setVisibility(View.VISIBLE);
            image.setVisibility(View.INVISIBLE);

        }
        return new EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventsViewHolder holder, int position) {


        if(type.equals("trending")) {
            holder.openEvent(eventsVector.get(position).getKey(),"fromTrending");
            holder.setEventName(eventsVector.get(position).getEventName());
            holder.setEventImage(ctx, eventsVector.get(position).getEventImage());
            holder.setEventTimestamp(eventsVector.get(position).getPostTimeMillis());
            holder.setBoost(eventsVector.get(position).getKey(), eventsVector.get(position).getEventName());
        }else if(type.equals("timeline")){
            holder.openEvent(eventsVector.get(position).getKey(),"fromTimeline");
            holder.setEventName(eventsVector.get(position).getEventName());
            holder.setEventTimestamp(eventsVector.get(position).getPostTimeMillis());
            holder.setBoost(eventsVector.get(position).getKey(), eventsVector.get(position).getEventName());
            holder.setEventDate(eventsVector.get(position).getEventDate());
            holder.setEventDesc(eventsVector.get(position).getEventDescription(), eventsVector.get(position).getKey());
        }
    }

    @Override
    public int getItemCount() {
        return eventsVector.size();
    }
}
