package com.zconnect.zutto.zconnect;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

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
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import static com.zconnect.zutto.zconnect.KeyHelper.KEY_CABPOOL;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_CABPOOL_JOIN;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_CAB_POOL_CHAT;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_EVENT;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_EVENTS_CHAT;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_EVENT_BOOST;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_FORUMS;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_FORUMS_JOIN;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_IMAGE_NOTIF;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_LIKE;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_LOVE;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_MESSAGES_CHAT;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_MESSAGES_CHAT_DELETE;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_POST_CHAT;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_PRODUCT;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_PRODUCT_CHAT;
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

            final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // When someone boost a event
            try {
                if (type.equals(KEY_EVENT_BOOST)) {
                    final String key = data.get("Key").toString();
                    final String name = data.get("PersonEmail").toString();
                    final String event = data.get("Event").toString();
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    style.bigText(name + " boosted your event " + event)
                            .setBigContentTitle("Event Boosted | ZConnect");

                    mBuilder.setSmallIcon(R.drawable.ic_whatshot_white_24dp)
                            .setStyle(style)
                            .setColor(ContextCompat.getColor(NotificationService.this, R.color.orange))
                            .setContentTitle("Event Boosted | ZConnect")
                            .setSound(defaultSoundUri)
                            .setContentText(name + " boosted your event " + event);

                    Intent intent = new Intent(NotificationService.this, OpenEventDetail.class);
                    intent.putExtra("id", key);
                    PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(intent1);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1, mBuilder.build());

                } else if (type.equals(KEY_CABPOOL_JOIN)) {//Notification to all the members of cab pool if a new member joins in
                    final String key = data.get("Key").toString();
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                    style.bigText(data.get("Product").toString() + " joined your cab pool")
                            .setBigContentTitle("Cab Pool | ZConnect");

                    android.support.v4.app.NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(NotificationService.this)
                                    .setSmallIcon(R.drawable.ic_directions_car_white_24dp)
                                    .setStyle(style)
                                    .setSound(defaultSoundUri)
                                    .setColor(ContextCompat.getColor(NotificationService.this, R.color.cabpool))
                                    .setContentTitle("Cab Pool | ZConnect")
                                    .setContentText(data.get("Product").toString() + " joined your cab pool");

                    Intent intent = new Intent(NotificationService.this, CabPoolListOfPeople.class);
                    intent.putExtra("key", (String) data.get("Key"));

                    PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(intent1);


                    // Gets an instance of the NotificationMa

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(key.compareTo(data.get("Product").toString()), mBuilder.build());

                } else if (type.equals(KEY_PRODUCT)) {
                    final String personEmail = data.get("PersonEmail").toString();
                    FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").orderByChild("userUID").equalTo(personEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String personName = data.get("Person").toString();
                            android.support.v4.app.NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(NotificationService.this);

                            if (dataSnapshot.getChildrenCount() != 0) {
                                UserItemFormat item = new UserItemFormat();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                    item = snapshot.getValue(UserItemFormat.class);

                                personName = item.getUsername();

                                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.getMobileNumber()));
                                PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, call, PendingIntent.FLAG_UPDATE_CURRENT);
                                mBuilder.addAction(R.drawable.ic_phone_black_18dp, "Call", pendingIntent);
                            }

                            String product = data.get("Product").toString();
                            NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                            style.bigText(personName + " shortlisted your product " + product)
                                    .setBigContentTitle("Product Shortlisted | ZConnect");

                            mBuilder.setSmallIcon(R.drawable.ic_local_mall_white_18dp)
                                    .setColor(ContextCompat.getColor(NotificationService.this, R.color.storeroom))
                                    .setStyle(style)
                                    .setSound(defaultSoundUri)
                                    .setContentTitle("Product Shortlisted | ZConnect")
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
                            .setSound(defaultSoundUri)
                            .setContentTitle(data.get("title").toString())
                            .setContentText(data.get("message").toString())
                            .setContentIntent(pi);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(new Random().nextInt(), mBuilder.build());

                } else if (type.equals(KEY_CABPOOL)) {

                    String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                    style.bigText("Hey! " + name + ", People around you are using Cab Pool very often")
                            .setBigContentTitle("Cab Pool | ZConnect");
                    android.support.v4.app.NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(NotificationService.this)
                                    .setSmallIcon(R.drawable.ic_directions_car_white_24dp)
                                    .setStyle(style)
                                    .setSound(defaultSoundUri)
                                    .setColor(ContextCompat.getColor(NotificationService.this, R.color.cabpool))
                                    .setContentTitle("Cab Pool | ZConnect")
                                    .setContentText("Hey! " + name + ", People around you are using Cab Pool very often");


                    Intent intent = new Intent(NotificationService.this, HomeActivity.class);
                    PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(intent1);


                    //Gets an instance of the NotificationMa

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1, mBuilder.build());

                } else if (type.equals(KEY_EVENT)) {
                    final String key = data.get("Key").toString();
                    FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("events").child("activeEvents").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Event format = dataSnapshot.getValue(Event.class);
                            Log.d("Entered ", format.getKey());
                            android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NotificationService.this);

                            String event = data.get("Event").toString();
                            NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                            style.bigText(event + " is going to happen near you.")
                                    .setBigContentTitle(format.getEventDescription());

                            mBuilder.setSmallIcon(R.drawable.ic_event_white_24dp)
                                    .setSound(defaultSoundUri)
                                    .setColor(ContextCompat.getColor(NotificationService.this, R.color.events))
                                    .setStyle(style)
                                    .setContentTitle("New Event | ZConnect")
                                    .setContentText(event + " is going to happen near you.");

                            Intent intent = new Intent(NotificationService.this, HomeActivity.class);
                            PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(intent1);

//                        Intent intent = new Intent(NotificationService.this, OpenEventDetail.class);
//                        intent.putExtra("currentEvent", format);
//                        intent.putExtra("Eventtag","1");

//                        PendingIntent pIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//                        mBuilder.setContentIntent(pIntent);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else if (type.equals(KEY_STOREROOM)) {

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
                    String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    style.bigText("Hey! " + name + ", products have been added. Check what your friend's are selling!")
                            .setBigContentTitle("StoreRoom | ZConnect");

                    mBuilder.setSmallIcon(R.drawable.ic_local_mall_white_24dp)
                            .setSound(defaultSoundUri)
                            .setColor(ContextCompat.getColor(NotificationService.this, R.color.storeroom))
                            .setStyle(style)
                            .setContentTitle("StoreRoom | ZConnect")
                            .setContentText("Hey! " + name + ", products have been added. Check what your friend's are selling!");

                    Intent intent = new Intent(NotificationService.this, HomeActivity.class);
                    PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(intent1);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1, mBuilder.build());

                } else if (type.equals(KEY_LIKE)) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    style.bigText("Hey! Somebody liked you, in your community")
                            .setBigContentTitle("Like ALert | ZConnect");

                    mBuilder.setSmallIcon(R.drawable.ic_thumb_up_white_24dp)
                            .setStyle(style)
                            .setSound(defaultSoundUri)
                            .setColor(ContextCompat.getColor(NotificationService.this, R.color.blue500))
                            .setContentTitle("Like Alert")
                            .setContentText("Hey! Somebody liked you, in your community");

                    Intent intent = new Intent(NotificationService.this, OpenUserDetail.class);
                    intent.putExtra("Uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(intent1);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1, mBuilder.build());

                } else if (type.equals(KEY_LOVE)) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    style.bigText("Hey! Somebody loves you, in your community")
                            .setBigContentTitle("Love Alert");

                    mBuilder.setSmallIcon(R.drawable.ic_favorite_white_24dp)
                            .setStyle(style)
                            .setSound(defaultSoundUri)
                            .setColor(ContextCompat.getColor(NotificationService.this, R.color.red500))
                            .setContentTitle("Love Alert | ZConnect")
                            .setContentText("Hey! Somebody loves you, in your community");

                    Intent intent = new Intent(NotificationService.this, OpenUserDetail.class);
                    intent.putExtra("Uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(intent1);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1, mBuilder.build());

                } else if (type.equals(KEY_FORUMS)) {

                    final String temp = data.get("Temp").toString();
                    final String key = data.get("Key").toString();
                    final String userName = data.get("Product").toString();
                    if (!temp.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                        style.bigText(userName + " posted in " + temp)
                                .setBigContentTitle("Forums | ZConnect");

                        mBuilder.setSmallIcon(R.drawable.ic_chat_white_24dp)
                                .setStyle(style)
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(NotificationService.this, R.color.forums))
                                .setContentTitle("Forums | ZConnect")
                                .setContentText(userName + " posted in " + temp);

                        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                        intent.putExtra("type", "forums");
                        intent.putExtra("key", key);
                        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent1);

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(1, mBuilder.build());
                    }
                } else if (type.equals(KEY_FORUMS_JOIN)) {

                    final String temp = data.get("Temp").toString();
                    final String key = data.get("Key").toString();
                    final String userName = data.get("Product").toString();

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    style.bigText(userName + " joined " + temp).setBigContentTitle("Forums Join | ZConnect");

                    mBuilder.setSmallIcon(R.drawable.ic_chat_white_24dp)
                            .setStyle(style)
                            .setSound(defaultSoundUri)
                            .setColor(ContextCompat.getColor(NotificationService.this, R.color.forums))
                            .setContentTitle("Forums Join | ZConnect")
                            .setContentText(userName + " joined " + temp);

                    Intent intent = new Intent(NotificationService.this, ChatActivity.class);
                    intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                    intent.putExtra("key", key);
                    PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(intent1);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(2, mBuilder.build());

                } else if (type.equals(KEY_IMAGE_NOTIF)) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigPictureStyle style = new android.support.v4.app.NotificationCompat.BigPictureStyle();

                    //final String title = data.get("Temp").toString();
                    final String imageUrl = data.get("Key").toString();
                    // final String text = data.get("Product").toString();


                    URL url = null;
                    try {
                        url = new URL(imageUrl);

                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        style.setBigContentTitle("Forums Alert").bigPicture(bitmap).bigLargeIcon(bitmap);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mBuilder.setSmallIcon(R.drawable.ic_thumb_up_white_24dp)
                            .setStyle(style)
                            .setContentTitle("Forums Alert")
                            .setContentText("test");

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(2, mBuilder.build());

                } else if (type.equals(KEY_PRODUCT_CHAT)) {

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    final String key = data.get("Key").toString();
                    final String userName = data.get("Product").toString();
                    final String temp = data.get("Temp").toString();


                    if (!temp.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        style.bigText("Storeroom Chat | ZConnect").setBigContentTitle(userName + " is discussing in the product posted by you");

                        mBuilder.setSmallIcon(R.drawable.ic_local_mall_white_24dp)
                                .setStyle(style)
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(NotificationService.this, R.color.storeroom))
                                .setContentTitle("Storeroom Chat | ZConnect")
                                .setContentText(userName + " is discussing in the product posted by you");


                        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(key).toString());
                        intent.putExtra("type", "storeroom");
                        intent.putExtra("key", key);
                        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent1);

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(2, mBuilder.build());
                    }

                } else if (type.equals(KEY_POST_CHAT)) {

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    final String key = data.get("Key").toString();
                    final String userName = data.get("Product").toString();
                    final String temp = data.get("Temp").toString();


                    if (!temp.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        style.bigText("Post | ZConnect").setBigContentTitle(userName + " is discussing in the post, posted by you");

                        mBuilder.setSmallIcon(R.drawable.ic_message_white_24dp)
                                .setStyle(style)
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(NotificationService.this, R.color.messages))
                                .setContentTitle("Post | ZConnect")
                                .setContentText(userName + " is discussing in the post, posted by you");

                        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(key).toString());
                        intent.putExtra("key", key);
                        intent.putExtra("type", "post");
                        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent1);

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(2, mBuilder.build());
                    }

                } else if (type.equals(KEY_MESSAGES_CHAT)) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    final String key = data.get("Key").toString();
                    final String userName = data.get("Product").toString();
                    final String temp = data.get("Temp").toString();

                    if (!temp.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        style.bigText("Anonymous Messages | ZConnect").setBigContentTitle(userName + " messaged you");

                        mBuilder.setSmallIcon(R.drawable.ic_message_white_18dp)
                                .setStyle(style)
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(NotificationService.this, R.color.messages))
                                .setContentTitle("Anonymous Messages | ZConnect")
                                .setContentText(userName + " messaged you");

                        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("messages").child("chats").child(key).toString());
                        intent.putExtra("key", key);
                        intent.putExtra("type", "messages");
                        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent1);

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(2, mBuilder.build());
                    }

                } else if (type.equals(KEY_MESSAGES_CHAT_DELETE)) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    final String key = data.get("Key").toString();
                    final String userName = data.get("Product").toString();
                    final String temp = data.get("Temp").toString();


                    if (!temp.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        style.bigText("Anonymous Messages | ZConnect").setBigContentTitle(userName + " deleted your message, talk to other people");

                        mBuilder.setSmallIcon(R.drawable.ic_message_white_24dp)
                                .setStyle(style)
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(NotificationService.this, R.color.messages))
                                .setContentTitle("Anonymous Messages | ZConnect")
                                .setContentText(userName + " deleted your message, talk to other people");

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(2, mBuilder.build());

                    }
                } else if (type.equals(KEY_EVENTS_CHAT)) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    final String key = data.get("Key").toString();
                    final String userName = data.get("Product").toString();
                    final String temp = data.get("Temp").toString();


                    if (!temp.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        style.bigText("Events | ZConnect").setBigContentTitle(userName + " posted in your event");

                        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("events").child("activeEvents").child(key).toString());
                        intent.putExtra("key", key);
                        intent.putExtra("type", "events");
                        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent1);

                        mBuilder.setSmallIcon(R.drawable.ic_event_white_24dp)
                                .setStyle(style)
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(NotificationService.this, R.color.events))
                                .setContentTitle("Events | ZConnect")
                                .setContentText(userName + " posted in your event");

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(2, mBuilder.build());
                    }
                } else if (type.equals(KEY_CAB_POOL_CHAT)) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                    final String key = data.get("Key").toString();
                    final String userName = data.get("Product").toString();
                    final String temp = data.get("Temp").toString();


                    if (!temp.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        style.bigText("Cab Pool Chat | ZConnect").setBigContentTitle(userName + " posted in your cab pool");

                        mBuilder.setSmallIcon(R.drawable.ic_directions_car_white_24dp)
                                .setStyle(style)
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(NotificationService.this, R.color.messages))
                                .setContentTitle("Cab Pool Chat | ZConnect")
                                .setContentText(userName + " posted in your cab pool");

                        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("allCabs").child(key).toString());
                        intent.putExtra("key", key);
                        intent.putExtra("type", "cabPool");
                        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent1);


                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(2, mBuilder.build());
                    }
                }
            }catch (Exception e){}
        }
    }

}