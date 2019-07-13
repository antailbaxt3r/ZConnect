package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;

public class InAppNotificationsRVViewHolder extends RecyclerView.ViewHolder {
    public TextView titletv;
    public TextView timetv;
    public TextView desctv;
    public ImageView seen;
    public LinearLayout notificationsLayout;
    public SimpleDraweeView simpleDraweeView;

    public InAppNotificationsRVViewHolder(View itemView) {
        super(itemView);
        notificationsLayout = (LinearLayout) itemView.findViewById(R.id.ll_notif);
        titletv = (TextView) itemView.findViewById(R.id.tv_notification_title);
        timetv = (TextView) itemView.findViewById(R.id.tv_notification_timeago);
        desctv = (TextView) itemView.findViewById(R.id.tv_notification_desc);
        simpleDraweeView = itemView.findViewById(R.id.imageView);
        seen = (ImageView) itemView.findViewById(R.id.seen);
    }
}
