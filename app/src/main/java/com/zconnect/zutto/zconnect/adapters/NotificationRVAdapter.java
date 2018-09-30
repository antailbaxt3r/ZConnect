package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.NotificationImage;
import com.zconnect.zutto.zconnect.NotificationNoImage;
import com.zconnect.zutto.zconnect.R;

import java.util.ArrayList;

public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ProgrammingViewHolder>{

    private ArrayList notif_type;
    Context context;

    public NotificationRVAdapter(ArrayList notif_type, Context context)
    {
        this.notif_type=notif_type;
        this.context=context;
    }


    @Override
    public ProgrammingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.notification_list_item_layout,parent,false);
        return new ProgrammingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProgrammingViewHolder holder, int position) {
        String notif_title=(String) notif_type.get(position);
        holder.notif.setText(notif_title);
        holder.notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.notif.getText()=="Text Notification"){
                    Intent nottif_no_image;
                    nottif_no_image = new Intent(context,NotificationNoImage.class);
                    nottif_no_image.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(nottif_no_image);
                }
                if (holder.notif.getText()=="Image Notification"){
                    Intent notif_image = new Intent(context,NotificationImage.class);
                    notif_image.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(notif_image);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notif_type.size();
    }

    public class ProgrammingViewHolder extends RecyclerView.ViewHolder{
        TextView notif;
        public ProgrammingViewHolder(View itemView) {
            super(itemView);
            notif=(TextView)itemView.findViewById(R.id.notif_textview);
        }
    }
}
