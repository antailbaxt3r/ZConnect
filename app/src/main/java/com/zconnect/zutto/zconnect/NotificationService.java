package com.zconnect.zutto.zconnect;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        final Map data = remoteMessage.getData();
        Log.d("data", data.toString());
        if (data.containsKey("Type")) {
            final String type = data.get("Type").toString();
            final String key = data.get("key").toString();

            if (type.equals("CabPool")) {
                FirebaseDatabase.getInstance().getReference("Cab").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CabItemFormat format = dataSnapshot.getValue(CabItemFormat.class);

                        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                        style.bigText(data.get("Person").toString() + " joined your cab pool for " + format.getDestination() + " from " + format.getSource() + " on " + format.getDate() + " at " + format.getTime())
                                .setBigContentTitle("New member: Cab pool");
                        android.support.v4.app.NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(NotificationService.this)
                                        .setSmallIcon(R.drawable.ic_directions_car_black_24dp)
                                        .setStyle(style)
                                        .setContentTitle("Cab pool")
                                        .setContentText(data.get("Person").toString() + " joined your cab pool");

                        if (data.containsKey("Contact") && data.get("Contact").toString() != null) {
                            final String contact = data.get("Contact").toString();
                            Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact));
                            PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, call, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.addAction(R.drawable.ic_phone_black_24dp, "Call", pendingIntent);
                        }

// Gets an instance of the NotificationMa

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(key.compareTo(data.get("Person").toString()), mBuilder.build());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else if (type.equals("Shortlist")) {
                final String personEmail = data.get("PersonEmail").toString();
                FirebaseDatabase.getInstance().getReference("Phonebook").orderByChild("email").equalTo(personEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String personName = data.get("PersonName").toString();
                        android.support.v4.app.NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(NotificationService.this);

                        if (dataSnapshot.getChildrenCount() != 0) {
                            PhonebookDisplayItem item = new PhonebookDisplayItem();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                item = snapshot.getValue(PhonebookDisplayItem.class);

                            if (item == null)
                                return;

                            if (personName == null)
                                personName = item.getName();

                            Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.getNumber()));
                            PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, call, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.addAction(R.drawable.ic_phone_black_24dp, "Call", pendingIntent);
                        }

                        String product = data.get("Product").toString();
                        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                        style.bigText(personName + " shortlisted your product " + product)
                                .setBigContentTitle("Your product shortlisted");
                        mBuilder.setSmallIcon(R.mipmap.ic_shopping_basket_black_36dp)
                                .setStyle(style)
                                .setContentTitle("Your product shortlisted")
                                .setContentText(personName + " shortlisted your product " + product);

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify((product.compareTo(personEmail)), mBuilder.build());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
    }
}
