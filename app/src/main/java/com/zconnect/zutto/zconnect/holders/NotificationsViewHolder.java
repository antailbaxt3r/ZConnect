package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.R;

public class NotificationsViewHolder extends RecyclerView.ViewHolder {
    public TextView titletv;
    public TextView desctv;
    public TextView datetv;
    public LinearLayout notificationsLayout;

    public NotificationsViewHolder(View itemView) {
        super(itemView);

        notificationsLayout = (LinearLayout) itemView.findViewById(R.id.ll_notif);
        titletv = (TextView) itemView.findViewById(R.id.tv_notification_title);
        desctv = (TextView) itemView.findViewById(R.id.tv_notification_desc);
        datetv = (TextView) itemView.findViewById(R.id.tv_notification_date);
    }
}
