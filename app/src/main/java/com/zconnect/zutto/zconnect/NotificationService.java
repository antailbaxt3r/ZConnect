package com.zconnect.zutto.zconnect;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map data = remoteMessage.getData();

        Log.d("sdsa", data.toString());
        if (data.containsKey("Type")) {
            if (data.get("Type").equals("CabPool")) {
                android.support.v4.app.NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_action_action_search)
                                .setContentTitle("Cab pool")
                                .setContentText(data.get("Person").toString() + "joined your cab pool");


// Gets an instance of the NotificationManager service//

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify((new Random()).nextInt(), mBuilder.build());
            }
//            creator.addData("Type","CabPool");
//            creator.addData("Person",name);
//            creator.addData("Contact",number);
//            creator.addData("Pool",key);
        }
    }
}
