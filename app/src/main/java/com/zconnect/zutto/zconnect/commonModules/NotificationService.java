package com.zconnect.zutto.zconnect.commonModules;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
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
import com.zconnect.zutto.zconnect.CabPoolAll;
import com.zconnect.zutto.zconnect.CabPoolListOfPeople;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.InfoneContactListActivity;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.OpenProductDetails;
import com.zconnect.zutto.zconnect.OpenUserDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.ShortlistedPeopleList;
import com.zconnect.zutto.zconnect.itemFormats.Event;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_CABPOOL;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_CABPOOL_JOIN;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_CAB_POOL_CHAT;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_EVENT;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_EVENTS_CHAT;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_EVENT_BOOST;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_FORUMS;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_FORUMS_JOIN;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_IMAGE_NOTIF;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_LIKE;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_LOVE;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_MESSAGES_CHAT;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_MESSAGES_CHAT_DELETE;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_POST_CHAT;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_PRODUCT;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_PRODUCT_CHAT;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_STOREROOM;

public class NotificationService extends FirebaseMessagingService {

    private Map data;
    private Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        SharedPreferences communitySP;
        String communityReference;

        communitySP = getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        data = remoteMessage.getData();

        if (data.containsKey("Type")) {
            final String type = data.get("Type").toString();

//            if(data.containsKey("userKey")){
//                final String userKey = data.get("userKey").toString();
//                if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userKey)){
//                    handleNotifications(type);
//                }
//            }else {
                handleNotifications(type);
//            }


            try {
                // When someone boost a event
                if (type.equals(KEY_EVENT_BOOST)) {



                } else if (type.equals(KEY_CABPOOL_JOIN)) {//Notification to all the members of cab pool if a new member joins in


                } else if (type.equals(KEY_PRODUCT)) {

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

                } else if (type.equals(KEY_EVENT)) {


                } else if (type.equals(KEY_STOREROOM)) {



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
                    final String forumName = data.get("Event").toString();
                    final String userName = data.get("Product").toString();
                    if (!temp.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();

                        style.bigText(userName + " posted in " + forumName)
                                .setBigContentTitle("Forums | ZConnect");

                        mBuilder.setSmallIcon(R.drawable.ic_chat_white_24dp)
                                .setStyle(style)
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(NotificationService.this, R.color.forums))
                                .setContentTitle("Forums | ZConnect")
                                .setContentText(userName + " posted in " + forumName);

                        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                        intent.putExtra("type", "forums");
                        intent.putExtra("name",forumName);
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
                    final String userUID = data.get("").toString();

                    if (!userUID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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
                        intent.putExtra("type", "forums");
                        intent.putExtra("name", temp);
                        intent.putExtra("key", key);
                        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent1);

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(2, mBuilder.build());
                    }

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

                        style.bigText("Messages | ZConnect").setBigContentTitle(userName + " messaged you");

                        mBuilder.setSmallIcon(R.drawable.ic_message_white_18dp)
                                .setStyle(style)
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(NotificationService.this, R.color.messages))
                                .setContentTitle("Messages | ZConnect")
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

    private void handleNotifications(String type){
        switch (type){
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_BOOST : eventBoostNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_JOIN : cabJoinNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_LEAVE : cabLeaveNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_SHORTLIST : productShortlistNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD : cabAddNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD : eventAddNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD: productAddNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD: forumAddNotification();
                break;
        }

    }

    private void infoneCategoryAddNotification(){
        final String communityName = data.get("communityName").toString();
        final String categoryName = data.get("categoryName").toString();
        final String categoryID = data.get("categoryID").toString();
        final String categoryImage = data.get("categoryImage").toString();
        final String categoryAdmin = data.get("categoryAdmin").toString();

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.setBigContentTitle(communityName).setSummaryText(categoryName + " is created in Infone, add relevant contacts").setBigContentTitle(communityName);

        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(categoryImage);
        }catch (Exception e){}


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        if(bitmap!=null){
            mBuilder.setLargeIcon(bitmap);
        }
        mBuilder.setSmallIcon(R.drawable.ic_phone)
                .setSound(defaultSoundUri)
                .setStyle(style)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentTitle(communityName)
                .setContentText(categoryName + " is created in Infone, add relevant contacts");

        Intent intentInfoneList = new Intent(NotificationService.this, InfoneContactListActivity.class);
        intentInfoneList.putExtra("catId", categoryID);
        intentInfoneList.putExtra("catName",categoryName);
        intentInfoneList.putExtra("catImageurl",categoryName);
        intentInfoneList.putExtra("catAdmin",categoryAdmin);


        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intentInfoneList, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(8, mBuilder.build());



    }

    private void forumAddNotification() {

        final String communityName = data.get("communityName").toString();
        final String forumName = data.get("forumName").toString();
        final String forumCategory = data.get("forumCategory").toString();
        final String forumCategoryUID = data.get("forumCategoryUID").toString();
        final String forumKey = data.get("forumKey").toString();

        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.setBigContentTitle(communityName).setSummaryText(userName + " started a new forum " + forumName + " in " + forumCategory).setBigContentTitle(communityName);

        Bitmap bitmap = null;
        try {
            URL url = new URL(userImage);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);


        if (bitmap!=null){
            mBuilder.setLargeIcon(bitmap);
        }

        mBuilder.setSmallIcon(R.drawable.ic_forum_white_18dp)
                .setSound(defaultSoundUri)
                .setStyle(style)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentTitle(communityName)
                .setContentText(userName + " started a new forum " + forumName + " in " + forumCategory);

        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(forumKey).toString());
        intent.putExtra("type","forums");
        intent.putExtra("name", forumName);
        intent.putExtra("tab",forumCategoryUID);
        intent.putExtra("key",forumKey);

        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(7, mBuilder.build());


    }

    private void productAddNotification() {

        final String communityName = data.get("communityName").toString();
        final String productName = data.get("productName").toString();
        final String productPrice = data.get("productPrice").toString();
        final String productKey = data.get("productKey").toString();
        final String productImage = data.get("productImage").toString();

        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        Bitmap bitmap = null;
        try {
            URL url = new URL(productImage);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }

        Bitmap bitmap2 = null;
        try {
            URL url = new URL(userImage);
            bitmap2 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        if(bitmap!=null) {
            NotificationCompat.BigPictureStyle style = new android.support.v4.app.NotificationCompat.BigPictureStyle();
            style.bigPicture(bitmap).setSummaryText(userName + " is selling " + productName + " for ₹" + productPrice).setBigContentTitle(communityName);
            mBuilder.setStyle(style);
        }

        if (bitmap2!=null){
            mBuilder.setLargeIcon(bitmap2);
        }

        mBuilder.setSmallIcon(R.drawable.ic_local_mall_white_24dp)
                .setSound(defaultSoundUri)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentTitle(communityName)
                .setContentText(userName + " is selling " + productName + " for ₹" + productPrice);

        Intent intent = new Intent(NotificationService.this, OpenProductDetails.class);
        intent.putExtra("key", productKey);

        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(6, mBuilder.build());

    }

    private void eventAddNotification(){

        final String communityName = data.get("communityName").toString();
        final String eventName = data.get("eventName").toString();
        final String eventLocation = data.get("eventLocation").toString();
        final String eventKey = data.get("eventKey").toString();
        final String eventImage = data.get("eventImage").toString();

        Bitmap bitmap = null;
        try {
            URL url = new URL(eventImage);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }
        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NotificationService.this);

        if(bitmap!=null) {
            NotificationCompat.BigPictureStyle style = new android.support.v4.app.NotificationCompat.BigPictureStyle();
            style.bigPicture(bitmap).setSummaryText(eventName + " will be happening at " + eventLocation).setBigContentTitle(communityName);
            mBuilder.setStyle(style);
        }

        mBuilder.setSmallIcon(R.drawable.ic_event_white_24dp)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setContentText(eventName + " will be happening at " + eventLocation);

        Intent intent = new Intent(NotificationService.this, OpenEventDetail.class);
        intent.putExtra("id", eventKey);

        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(5, mBuilder.build());

    }

    private void cabAddNotification(){

        final String communityName = data.get("communityName").toString();

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText("Hey! People around you are using Cab Pool very often").setBigContentTitle(communityName);

        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NotificationService.this)
                        .setSmallIcon(R.drawable.ic_directions_car_white_24dp)
                        .setStyle(style)
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(NotificationService.this,R.color.colorPrimary))
                        .setContentTitle(communityName)
                        .setContentText("Hey! People around you are using Cab Pool very often");


        Intent intent = new Intent(NotificationService.this, CabPoolAll.class);
        PendingIntent intent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(4, mBuilder.build());
    }



    private void productShortlistNotification(){

        final String userName = data.get("userName").toString();
        final String userMobileNumber = data.get("userMobileNumber").toString();
        final String productKey = data.get("productKey").toString();
        final String productName = data.get("productName").toString();
        final String communityName = data.get("communityName").toString();
        final String userImage = data.get("userImage").toString();

        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NotificationService.this);

        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userMobileNumber));

        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, call, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_phone_black_18dp, "Call", pendingIntent);

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " shortlisted your product " + productName).setBigContentTitle(communityName);

        Bitmap image = null;
        image  = getRoundedBitmap(userImage);

        if(image!=null){
            mBuilder.setLargeIcon(image);
        }

        mBuilder.setSmallIcon(R.drawable.ic_local_mall_white_18dp)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentTitle(communityName)
                .setContentText(userName + " shortlisted your product " + productName);

        Intent intent = new Intent(NotificationService.this, ShortlistedPeopleList.class);
        intent.putExtra("Key", productKey);

        PendingIntent pendingIntent1 = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(3, mBuilder.build());
    }

    private void cabLeaveNotification() {

        final String userName = data.get("userName").toString();
        final String cabKey = data.get("cabKey").toString();
        final String communityName = data.get("communityName").toString();
        final String userImage = data.get("userImage").toString();

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " left your cab pool").setBigContentTitle(communityName);

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(NotificationService.this)
                        .setSmallIcon(R.drawable.ic_directions_car_white_24dp)
                        .setStyle(style)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                        .setContentTitle(communityName)
                        .setContentText(userName + " left your cab pool");

        Bitmap image = null;
        image  = getRoundedBitmap(userImage);

        if(image!=null){
            mBuilder.setLargeIcon(image);
        }

        Intent intent = new Intent(NotificationService.this, CabPoolListOfPeople.class);
        intent.putExtra("key", cabKey);

        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2, mBuilder.build());
    }

    private void cabJoinNotification() {

        final String userName = data.get("userName").toString();
        final String cabKey = data.get("cabKey").toString();
        final String communityName = data.get("communityName").toString();
        final String userImage = data.get("userImage").toString();

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " joined your cab pool").setBigContentTitle(communityName);

        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Bitmap image = null;
        image  = getRoundedBitmap(userImage);

        if(image!=null){
            mBuilder.setLargeIcon(image);
        }

        mBuilder.setSmallIcon(R.drawable.ic_directions_car_white_24dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setContentText(userName + " joined your cab pool");

        Intent intent = new Intent(NotificationService.this, CabPoolListOfPeople.class);
        intent.putExtra("key", cabKey);

        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    public void eventBoostNotification(){

        final String eventKey = data.get("eventKey").toString();
        final String userName = data.get("userName").toString();
        final String eventName = data.get("eventName").toString();
        final String communityName = data.get("communityName").toString();
        final String userImage = data.get("userImage").toString();

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " boosted your event " + eventName).setBigContentTitle(communityName);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Bitmap image = null;
        image  = getRoundedBitmap(userImage);

        if(image!=null){
            mBuilder.setLargeIcon(image);
        }

        mBuilder.setSmallIcon(R.drawable.ic_whatshot_white_24dp)
                .setStyle(style)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText(userName + " boosted your event " + eventName);

        Intent intent = new Intent(NotificationService.this, OpenEventDetail.class);
        intent.putExtra("id",eventKey);

        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public static Bitmap getRoundedBitmap(String userImage) {

        Bitmap bitmap= null;
        int cornerRadius;

        try {
            URL url = new URL(userImage);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }


        if (bitmap == null) {
            return null;
        }else {
            cornerRadius = bitmap.getHeight()/2;
        }
        if (cornerRadius < 0) {
            cornerRadius = 0;
        }
        // Create plain bitmap
        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawARGB(0,0,0,0);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return canvasBitmap;
    }

}