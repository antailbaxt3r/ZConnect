package com.zconnect.zutto.zconnect;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.Event;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.util.Map;
import java.util.Random;

import static com.zconnect.zutto.zconnect.KeyHelper.KEY_CABPOOL;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_CABPOOL_JOIN;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_EVENT;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_EVENT_BOOST;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_FORUMS;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_LIKE;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_LOVE;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_PRODUCT;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_STOREROOM;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        SharedPreferences communitySP;
        String communityReference;

        communitySP = getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        final Map data = remoteMessage.getData();
        if (data.containsKey("Type")) {
            final String type = data.get("Type").toString();
            if (type.equals(KEY_EVENT_BOOST)) {
                final String key = data.get("Key").toString();
                // use this to notify users who have boosted if event details have been changed
                FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("events").child("activeEvents").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event format = dataSnapshot.getValue(Event.class);
                        Log.d("Entered ", format.getKey());
                        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NotificationService.this);

                        String event = data.get("Event").toString();
                        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                        style.bigText("Congrats! You just subscribed to an event - " + event)
                                .setBigContentTitle("Event Subscription");
                        mBuilder.setSmallIcon(R.mipmap.ic_alarm_black_24dp)
                                .setStyle(style)
                                .setContentTitle("NB Event Subscription")
                                .setContentText("NB Congrats! You just subscribed to an event - " + event);

//                        Intent intent = new Intent(NotificationService.this, OpenEventDetail.class);
//                        intent.putExtra("currentEvent", format);
//                        intent.putExtra("Eventtag","1");

//                        PendingIntent pIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//                        mBuilder.setContentIntent(pIntent);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                        notificationIntent.addCategory("android.intent.category.DEFAULT");
                        notificationIntent.putExtra("Eventtag", "1");
                        notificationIntent.putExtra("currentEvent", format);
                        notificationIntent.putExtra("EventName", event);

                        PendingIntent broadcast = PendingIntent.getBroadcast(NotificationService.this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, (Long.parseLong(format.getEventTimeMillis().toString()) - Long.parseLong(data.get("TimeInMilli").toString()) - (5 * 60 * 1000)), broadcast);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            } else if (type.equals(KEY_CABPOOL_JOIN)) {
                final String key = data.get("Key").toString();
                FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("allCabs").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
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

                        Intent intent = new Intent(NotificationService.this, CabPoolListOfPeople.class);
                        intent.putExtra("key", (String) data.get("Key"));

                        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent1);


// Gets an instance of the NotificationMa

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(key.compareTo(data.get("Person").toString()), mBuilder.build());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else if (type.equals(KEY_PRODUCT)) {
                final String personEmail = data.get("PersonEmail").toString();
                FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Phonebook").orderByChild("uid").equalTo(personEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String personName = data.get("Person").toString();
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

                        Intent intent = new Intent(NotificationService.this, Shortlisted.class);
                        intent.putExtra("Key", (String) data.get("Key"));

                        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent1);

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify((product.length()), mBuilder.build());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else if (type.equals("crash")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
                }
            } else if (type.equals("url")) {

                String url = data.get("url").toString();
                Log.d("data", "url fired");
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;

                Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                notificationIntent.setData(Uri.parse(url));

                PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                style.bigText(data.get("Bigmessage").toString())
                        .setBigContentTitle(data.get("Bigtitle").toString());
                android.support.v4.app.NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(NotificationService.this);

                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(style)
                        .setContentTitle(data.get("title").toString())
                        .setContentText(data.get("message").toString())
                        .setContentIntent(pi);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(new Random().nextInt(), mBuilder.build());

            }else if (type.equals(KEY_CABPOOL)) {

                String name= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                        style.bigText("Hey! "+name +",People around you are using Cabpool very often.just check where they are going.")
                                .setBigContentTitle("Cab pool");
                        android.support.v4.app.NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(NotificationService.this)
                                        .setSmallIcon(R.drawable.ic_directions_car_black_24dp)
                                        .setStyle(style)
                                        .setContentTitle("Cabpool")
                                        .setContentText("Cabpool's around you");


                        Intent intent = new Intent(NotificationService.this, HomeActivity.class);
                        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent1);


                      //Gets an instance of the NotificationMa

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(1, mBuilder.build());

            } else if (type.equals(KEY_EVENT)) {
               final String key = data.get("Key").toString();
               // use this to notify users who have boosted if event details have been changed
               FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("events").child("activeEvents").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       Event format = dataSnapshot.getValue(Event.class);
                       Log.d("Entered ", format.getKey());
                       android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NotificationService.this);

                       String event = data.get("Event").toString();
                       NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                       style.bigText(event+" is going to happen near you.")
                               .setBigContentTitle(format.getEventDescription());
                       mBuilder.setSmallIcon(R.mipmap.ic_alarm_black_24dp)
                               .setStyle(style)
                               .setContentTitle("New Event")
                               .setContentText(event+" is going to happen near you.");

//                        Intent intent = new Intent(NotificationService.this, OpenEventDetail.class);
//                        intent.putExtra("currentEvent", format);
//                        intent.putExtra("Eventtag","1");

//                        PendingIntent pIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//                        mBuilder.setContentIntent(pIntent);

                       AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                       Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                       notificationIntent.addCategory("android.intent.category.DEFAULT");
                       notificationIntent.putExtra("Eventtag", "1");
                       notificationIntent.putExtra("currentEvent", format);
                       notificationIntent.putExtra("EventName", event);

                       PendingIntent broadcast = PendingIntent.getBroadcast(NotificationService.this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                       alarmManager.setExact(AlarmManager.RTC_WAKEUP, (Long.parseLong(format.getEventTimeMillis().toString()) - Long.parseLong(data.get("TimeInMilli").toString()) - (5 * 60 * 1000)), broadcast);
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });


           }else if(type.equals(KEY_STOREROOM)){

                NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this);
                NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                String name=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                style.bigText("Hey! "+name+" lot of products have been added since, you last visited. Check what your friend's are selling!")
                        .setBigContentTitle("New Products");
                mBuilder.setSmallIcon(R.mipmap.ic_shopping_basket_black_36dp)
                        .setStyle(style)
                        .setContentTitle("StoreRoom")
                        .setContentText("New Products are added in the Storeroom");

                Intent intent = new Intent(NotificationService.this, HomeActivity.class);
                PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(intent1);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());

            }else if(type.equals(KEY_LIKE)){
                NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this);
                NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                style.bigText("Hey! Somebody liked you")
                        .setBigContentTitle("Like ALert");

                mBuilder.setSmallIcon(R.drawable.ic_thumb_up_white_24dp)
                        .setStyle(style)
                        .setColor(ContextCompat.getColor(NotificationService.this, R.color.blue500))
                        .setContentTitle("Like Alert")
                        .setContentText("Hey! Somebody liked you, in your community");

                Intent intent = new Intent(NotificationService.this, OpenUserDetail.class);
                intent.putExtra("Uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(intent1);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());

            }else if(type.equals(KEY_LOVE)){
                NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this);
                NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                style.bigText("Hey! Somebody loves you")
                        .setBigContentTitle("Love Alert");

                mBuilder.setSmallIcon(R.drawable.ic_favorite_white_24dp)
                        .setStyle(style)
                        .setColor(ContextCompat.getColor(NotificationService.this, R.color.red500))
                        .setContentTitle("Love Alert")
                        .setContentText("Hey! Somebody loves you, in your community");

                Intent intent = new Intent(NotificationService.this, OpenUserDetail.class);
                intent.putExtra("Uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(intent1);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
            }else if(type.equals(KEY_FORUMS)){

                final String temp = data.get("Temp").toString();
                final String key = data.get("Key").toString();
                if(!temp.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    style.bigText("Your subscribed forum is active")
                            .setBigContentTitle("Forums Alert");

                    mBuilder.setSmallIcon(R.drawable.ic_thumb_up_white_24dp)
                            .setStyle(style)
                            .setContentTitle("Forums Alert")
                            .setContentText("Your subscribed forum is active");

                    Intent intent = new Intent(NotificationService.this, ChatActivity.class);
                    intent.putExtra("ref",FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                    intent.putExtra("key",key);
                    PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(intent1);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1, mBuilder.build());
                }
            }
        }
    }

}