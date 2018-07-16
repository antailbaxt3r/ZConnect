package com.zconnect.zutto.zconnect;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.zconnect.zutto.zconnect.itemFormats.Event;

/**
 * Created by akhiller on 30/10/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationIntent = new Intent(context, OpenEventDetail.class);
        notificationIntent.putExtra("currentEvent", (Event) intent.getExtras().get("currentEvent"));
        notificationIntent.putExtra("Eventtag", "1");

        String eventName = intent.getStringExtra("EventName");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(OpenEventDetail.class);
        stackBuilder.addNextIntent(notificationIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = builder.setContentTitle(eventName)
                .setContentText("Starting in 5 minutes. Hurry up!")
                .setTicker("ZConnect Event Alert!")
                .setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(eventName, 1, notification);
    }
}
