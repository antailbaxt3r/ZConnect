package com.zconnect.zutto.zconnect.commonModules;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
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
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zconnect.zutto.zconnect.CabPoolAll;
import com.zconnect.zutto.zconnect.CabPoolListOfPeople;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.InfoneContactListActivity;
import com.zconnect.zutto.zconnect.Notices;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.OpenProductDetails;
import com.zconnect.zutto.zconnect.OpenStatus;
import com.zconnect.zutto.zconnect.OpenUserDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.ShortlistedPeopleList;
import com.zconnect.zutto.zconnect.VerificationPage;
import com.zconnect.zutto.zconnect.utilities.ForumUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.ProductUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class NotificationService extends FirebaseMessagingService {

    private Map data;
    private Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private NotificationManager manager;
    public static final String FORUMS_CHANNEL_ID = "forums";
    public static final String PERSONAL_CHANNEL_ID = "personal";
    public static final String COMMUNITY_CHANNEL_ID = "community";

    NotificationChannel forumChannel = null;
    NotificationChannel personalChannel = null;
    NotificationChannel communityChannel = null;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            forumChannel = new NotificationChannel(FORUMS_CHANNEL_ID, getString(R.string.noti_channel_forums), NotificationManager.IMPORTANCE_DEFAULT);
            forumChannel.setLightColor(Color.GREEN);
            forumChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(forumChannel);


            personalChannel = new NotificationChannel(PERSONAL_CHANNEL_ID, getString(R.string.noti_channel_personal), NotificationManager.IMPORTANCE_DEFAULT);
            personalChannel.setLightColor(Color.GREEN);
            personalChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(personalChannel);


            communityChannel = new NotificationChannel(COMMUNITY_CHANNEL_ID, getString(R.string.noti_channel_community), NotificationManager.IMPORTANCE_DEFAULT);
            communityChannel.setLightColor(Color.GREEN);
            communityChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(communityChannel);
        }


        data = remoteMessage.getData();


        if (data.containsKey("Type")) {
            final String type = data.get("Type").toString();

            if (data.containsKey("userKey")) {
                try {
                    final String userKey = data.get("userKey").toString();
                    if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userKey)) {
                        handleNotifications(type);
                    }

                }catch (Exception e){
                    handleNotifications(type);
                }

            } else {
                handleNotifications(type);
            }
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
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_CATEGORY_ADD: infoneCategoryAddNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_LIKE: infoneLikeNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_LOVE: infoneLoveNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_FORUM: forumChatNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_PRODUCT: productChatNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_CAB: cabChatNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_EVENT: eventChatNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_POST: postChatNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_TEXT_URL: textURLNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_IMAGE_URL: imageURLNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CACHE: cacheDeleteNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_REQUEST_CALL: requestCallNotification();
                break;
            case  NotificationIdentifierUtilities.KEY_NOTIFICATION_NEW_USER_ACCEPT: newUserAcceptNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_NEW_USER_REJECT: newUserRejectNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_STATUS_LIKED: statusLikeNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_NOTICES_ADD: noticeAddNotification();
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_ORDER_REACHED: orderReachedNotification();
                break;
        }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    private void orderReachedNotification() {

        final String communityReference = data.get("communityReference").toString();

        final String shopName = data.get("shopName").toString();
        final String poolName = data.get("itemName").toString();
        final String poolPushKey = data.get("itemKey").toString();
        final String poolImage = data.get("itemImage").toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,FORUMS_CHANNEL_ID);
        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText("Click to check more details regarding your order").setBigContentTitle(shopName + " order arrived");

        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(poolImage);
        }catch (Exception e){}


        if (bitmap!=null){
            mBuilder.setLargeIcon(bitmap);
        }


        mBuilder.setSmallIcon(R.drawable.ic_forum_white_18dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(shopName + " order arrived")
                .setContentText("Click to check more details regarding your order");

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);


        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
        intent.putExtra("ref", "https://zconnectmulticommunity.firebaseio.com/communities/"+ communityReference+"/features/forums/categories/"+poolPushKey);
        intent.putExtra("type","forums");
        intent.putExtra("name", poolName);
        intent.putExtra("tab","shopPools");
        intent.putExtra("key",poolPushKey);


        Intent[] intents = new Intent[]{intent0,intent};


        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(21, mBuilder.build());
    }

    private void noticeAddNotification() {
        final String communityName = data.get("communityName").toString();
        final String noticeName = data.get("noticeName").toString();
        final String noticeKey = data.get("noticeKey").toString();
        final String noticeImage = data.get("noticeImage").toString();
        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        Bitmap bitmapNotice = null;
        try {
            URL url = new URL(noticeImage);
            bitmapNotice = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }

        NotificationCompat.BigPictureStyle style = new android.support.v4.app.NotificationCompat.BigPictureStyle();
            style.setBigContentTitle(communityName).bigPicture(bitmapNotice).setBigContentTitle(communityName);

            Bitmap bitmap = null;

            try {
                bitmap = getRoundedBitmap(userImage);
            }catch (Exception e){}

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,COMMUNITY_CHANNEL_ID);

            if(bitmap!=null){
                mBuilder.setLargeIcon(bitmap);
            }
            mBuilder.setSmallIcon(R.drawable.baseline_insert_photo_white_36)
                    .setSound(defaultSoundUri)
                    .setStyle(style)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setContentTitle(communityName)
                    .setContentText("A new notice for " + noticeName + "is added, click to check");


            Intent intentInfoneList = new Intent(NotificationService.this, Notices.class);

            Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

            Intent[] intents = new Intent[]{intent0,intentInfoneList};


            PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_ONE_SHOT);
            mBuilder.setContentIntent(intent1);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(20, mBuilder.build());


    }

    private void statusLikeNotification() {
        final String statusKey = data.get("statusKey").toString();
        final String userName = data.get("userName").toString();
        final String communityName = data.get("communityName").toString();
        final String userImage = data.get("userImage").toString();
        final String likeCount = data.get("likeCount").toString();
        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " likes your status").setBigContentTitle(communityName);
        if(likeCount.length()>0)
            style.bigText(userName + " and " + likeCount + " others " + " like your status").setBigContentTitle(communityName);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);


        Bitmap image = null;
        image  = getRoundedBitmap(userImage);

        if(image!=null){
            mBuilder.setLargeIcon(image);
        }

        mBuilder.setSmallIcon(R.drawable.baseline_thumb_up_alt_white_24)
                .setStyle(style)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText(userName + " likes your status");
        if(likeCount.length()>0)
            mBuilder.setContentText(userName + " and " + likeCount + " others " + " like your status");

        Intent intent = new Intent(NotificationService.this, OpenStatus.class);
        intent.putExtra("key",statusKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent pendingIntent = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(19, mBuilder.build());
    }

    private void newUserRejectNotification() {

        final String communityName = data.get("communityName").toString();

        Bitmap appLogo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.setBigContentTitle(communityName).bigText("Your profile is rejected, please again add your details.");




        if (appLogo!=null){
            mBuilder.setLargeIcon(appLogo);
        }

        mBuilder.setSmallIcon(R.drawable.ic_person_white_24dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setContentText("Your profile is rejected, please again add your details.");


        Intent intent = new Intent(NotificationService.this, VerificationPage.class);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent pendingIntent = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(19, mBuilder.build());




    }


    private void newUserAcceptNotification() {

        final String communityName = data.get("communityName").toString();

        Bitmap appLogo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.setBigContentTitle(communityName).bigText("Your profile is approved, you can enjoy access to all features.");




        if (appLogo!=null){
            mBuilder.setLargeIcon(appLogo);
        }

        mBuilder.setSmallIcon(R.drawable.ic_person_white_24dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setContentText("Your profile is approved, you can enjoy access to all features.");


        Intent intent = new Intent(NotificationService.this, HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(18, mBuilder.build());
    }

    private void cacheDeleteNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
        }
    }

    private void imageURLNotification() {
        final String imageURL = data.get("imageURL").toString();
        final String URL = data.get("URL").toString();
        final String title = data.get("title").toString();
        final String message = data.get("message").toString();

        Bitmap appLogo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,COMMUNITY_CHANNEL_ID);

        NotificationCompat.BigPictureStyle style = new android.support.v4.app.NotificationCompat.BigPictureStyle();
        style.setBigContentTitle(title).setSummaryText(message);

        Bitmap bitmap = null;
        try {
            URL url = new URL(imageURL);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }

        if (appLogo!=null){
            mBuilder.setLargeIcon(appLogo);
        }

        if(bitmap!=null){
            style.bigPicture(bitmap);
        }

        mBuilder.setSmallIcon(R.drawable.ic_whatshot_white_24dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(message);

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        PendingIntent contentIntent = PendingIntent.getActivity(NotificationService.this, 0, notificationIntent, 0);
        mBuilder.setContentIntent(contentIntent);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(17, mBuilder.build());
    }

    private void textURLNotification() {

        final String URL = data.get("URL").toString();
        final String title = data.get("title").toString();
        final String message = data.get("message").toString();

        Bitmap appLogo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,COMMUNITY_CHANNEL_ID);

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.setBigContentTitle(title).bigText(message);

        if (appLogo!=null){
            mBuilder.setLargeIcon(appLogo);
        }


        mBuilder.setSmallIcon(R.drawable.ic_whatshot_white_24dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(message);

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        PendingIntent contentIntent = PendingIntent.getActivity(NotificationService.this, 0, notificationIntent, 0);
        mBuilder.setContentIntent(contentIntent);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(16, mBuilder.build());
    }

    private void requestCallNotification() {

        final String communityName = data.get("communityName").toString();

        final String userKey = data.get("userKey").toString();
        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();
        final String userMobileNumber = data.get("userMobileNumber").toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);
        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " tried contacting you, call him now! ").setBigContentTitle(communityName);

        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userMobileNumber));

        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, call, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_phone_black_18dp, "Call Now", pendingIntent);

        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(userImage);
        }catch (Exception e) {}

        if (bitmap!= null){
            mBuilder.setLargeIcon(bitmap);
        }

        mBuilder.setSmallIcon(R.drawable.ic_phone)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setContentText(userName + " tried contacting you, call him now! ");

        Intent intent = new Intent(NotificationService.this, OpenUserDetail.class);
        intent.putExtra("Uid",userKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(18, mBuilder.build());

    }


    private void postChatNotification() {


        final String communityName = data.get("communityName").toString();
        final String communityReference = data.get("communityReference").toString();

        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        final String postMessage = data.get("postMessage").toString();
        final String postKey = data.get("postKey").toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);
        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " commented on your post").setBigContentTitle(communityName);


        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(userImage);
        }catch (Exception e){}


        if (bitmap!=null){
            mBuilder.setLargeIcon(bitmap);
        }

        mBuilder.setSmallIcon(R.drawable.ic_message_white_18dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setSubText(userName + ": "+ postMessage)
                .setContentText(userName + " commented on your post");

        Intent intent = new Intent(NotificationService.this, ChatActivity.class);

        intent.putExtra("ref", "https://zconnectmulticommunity.firebaseio.com/communities/"+ communityReference + "/home/"+postKey);
        intent.putExtra("key",postKey);
        intent.putExtra("type","post");

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(14, mBuilder.build());

    }

    private void eventChatNotification() {
        final String communityName = data.get("communityName").toString();
        final String communityReference = data.get("communityReference").toString();

        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        final String eventMessage = data.get("eventMessage").toString();
        final String eventKey = data.get("eventKey").toString();
        final String eventName = data.get("eventName").toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);

        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(userImage);
        }catch (Exception e){}


        if (bitmap!=null){
            mBuilder.setLargeIcon(bitmap);
        }

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " asked about " + eventName).setBigContentTitle(communityName);

        mBuilder.setSmallIcon(R.drawable.ic_event_white_18dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setSubText(userName + ": "+ eventMessage)
                .setContentText(userName + " asked about " + eventName);

        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
        intent.putExtra("type","events");
        intent.putExtra("key",eventKey);
        intent.putExtra("name",eventName);
        intent.putExtra("ref", "https://zconnectmulticommunity.firebaseio.com/communities/" + communityReference + "/features/events/activeEvents"+ eventKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(13, mBuilder.build());

    }

    private void cabChatNotification() {

        final String communityName = data.get("communityName").toString();
        final String communityReference = data.get("communityReference").toString();

        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        final String cabMessage = data.get("cabMessage").toString();
        final String cabKey = data.get("cabKey").toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);

        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(userImage);
        }catch (Exception e){}


        if (bitmap!=null){
            mBuilder.setLargeIcon(bitmap);

        }

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " messaged in your cab pool").setBigContentTitle(communityName);

        mBuilder.setSmallIcon(R.drawable.ic_directions_car_black_24dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setSubText(userName + ": "+ cabMessage)
                .setContentText(userName + " messaged in your cab pool");

        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
        intent.putExtra("type","cabPool");
        intent.putExtra("key",cabKey);
        intent.putExtra("ref", "https://zconnectmulticommunity.firebaseio.com/communities/"+ communityReference +"/features/cabPool/allCabs/"+cabKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(12, mBuilder.build());

    }

    private void productChatNotification() {
        final String communityName = data.get("communityName").toString();
        final String communityReference = data.get("communityReference").toString();

        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        final String productMessage = data.get("productMessage").toString();
        final String productKey = data.get("productKey").toString();
        final String productName = data.get("productName").toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);

        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(userImage);
        }catch (Exception e){}


        if (bitmap!=null){
            mBuilder.setLargeIcon(bitmap);
        }

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " asked for " + productName).setBigContentTitle(communityName);

        mBuilder.setSmallIcon(R.drawable.ic_local_mall_white_18dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setSubText(userName + ": "+ productMessage)
                .setContentText(userName + " asked for " + productName);

        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
        intent.putExtra("type","storeroom");
        intent.putExtra("key",productKey);
        intent.putExtra("name",productName);
        intent.putExtra("ref", "https://zconnectmulticommunity.firebaseio.com/communities/" + communityReference +"/features/storeroom/products/"+productKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(11, mBuilder.build());
    }

    private void forumChatNotification() {

        final String communityName = data.get("communityName").toString();
        final String communityReference = data.get("communityReference").toString();

        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        final String forumMessage = data.get("forumMessage").toString();
        final String forumCategoryUID = data.get("forumCategoryUID").toString();
        final String forumKey = data.get("forumKey").toString();
        final String forumName = data.get("forumName").toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,FORUMS_CHANNEL_ID);
        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " posted in " + forumName).setBigContentTitle(communityName);

        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(userImage);
        }catch (Exception e){}


        if (bitmap!=null){
            mBuilder.setLargeIcon(bitmap);
        }


            mBuilder.setSmallIcon(R.drawable.ic_forum_white_18dp)
                    .setStyle(style)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                    .setContentTitle(communityName)
                    .setSubText(userName + ": "+ forumMessage)
                    .setContentText(userName + " posted in " + forumName);

            Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
            intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);


            Intent intent = new Intent(NotificationService.this, ChatActivity.class);
            intent.putExtra("ref", "https://zconnectmulticommunity.firebaseio.com/communities/"+ communityReference+"/features/forums/categories/"+forumKey);
            intent.putExtra("type","forums");
            intent.putExtra("name", forumName);
            intent.putExtra("tab",forumCategoryUID);
            intent.putExtra("key",forumKey);


            Intent[] intents = new Intent[]{intent0,intent};


            PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_ONE_SHOT);
            mBuilder.setContentIntent(intent1);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(10, mBuilder.build());
    }

    private void infoneLoveNotification() {

        final String communityName = data.get("communityName").toString();

        final String userKey = data.get("userKey").toString();
        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText("Hey! " + userName + " loved your profile").setBigContentTitle(communityName);

        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(userImage);
        }catch (Exception e) {}

        if (bitmap!= null){
            mBuilder.setLargeIcon(bitmap);
        }

        mBuilder.setSmallIcon(R.drawable.ic_favorite_white_24dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setContentText("Hey! " + userName + " loved your profile");

        Intent intent = new Intent(NotificationService.this, OpenUserDetail.class);
        intent.putExtra("Uid",userKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(9, mBuilder.build());

    }

    private void infoneLikeNotification() {

        final String communityName = data.get("communityName").toString();

        final String userKey = data.get("userKey").toString();
        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);
        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText("Hey! " + userName + " liked your profile").setBigContentTitle(communityName);

        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(userImage);
        }catch (Exception e) {}

        if (bitmap!= null){
            mBuilder.setLargeIcon(bitmap);
        }


        mBuilder.setSmallIcon(R.drawable.ic_thumb_up_white_24dp)
                .setStyle(style)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(defaultSoundUri)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setContentText("Hey! " + userName + " liked your profile");

        Intent intent = new Intent(NotificationService.this, OpenUserDetail.class);
        intent.putExtra("Uid",userKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(9, mBuilder.build());


    }

    private void infoneCategoryAddNotification(){
        final String communityName = data.get("communityName").toString();
        final String categoryName = data.get("categoryName").toString();
        final String categoryID = data.get("categoryID").toString();
        final String categoryImage = data.get("categoryImage").toString();
        final String categoryAdmin = data.get("categoryAdmin").toString();

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.setBigContentTitle(communityName).bigText(categoryName + " is created in Infone, add relevant contacts").setBigContentTitle(communityName);

        Bitmap bitmap = null;

        try {
            bitmap = getRoundedBitmap(categoryImage);
        }catch (Exception e){}


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,COMMUNITY_CHANNEL_ID);

        if(bitmap!=null){
            mBuilder.setLargeIcon(bitmap);
        }
        mBuilder.setSmallIcon(R.drawable.ic_phone)
                .setSound(defaultSoundUri)
                .setStyle(style)
                .setPriority(Notification.PRIORITY_MAX)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentTitle(communityName)
                .setContentText(categoryName + " is created in Infone, add relevant contacts");

        Intent intentInfoneList = new Intent(NotificationService.this, InfoneContactListActivity.class);
        intentInfoneList.putExtra("catId", categoryID);
        intentInfoneList.putExtra("catName",categoryName);
        intentInfoneList.putExtra("catImageurl",categoryName);
        intentInfoneList.putExtra("catAdmin",categoryAdmin);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intentInfoneList};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(8, mBuilder.build());



    }

    private void forumAddNotification() {

        final String communityName = data.get("communityName").toString();
        final String communityReference = data.get("communityReference").toString();

        final String forumName = data.get("forumName").toString();
        final String forumCategory = data.get("forumCategory").toString();
        final String forumCategoryUID = data.get("forumCategoryUID").toString();
        final String forumKey = data.get("forumKey").toString();

        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.setBigContentTitle(communityName).bigText(userName + " started a new forum " + forumName + " in " + forumCategory).setBigContentTitle(communityName);

        Bitmap bitmap = null;
        try {
            URL url = new URL(userImage);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,FORUMS_CHANNEL_ID);


        if (bitmap!=null){
            mBuilder.setLargeIcon(bitmap);
        }

        mBuilder.setSmallIcon(R.drawable.ic_forum_white_18dp)
                .setSound(defaultSoundUri)
                .setStyle(style)
                .setPriority(Notification.PRIORITY_MAX)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentTitle(communityName)
                .setContentText(userName + " started a new forum " + forumName + " in " + forumCategory);

        Intent intent = new Intent(NotificationService.this, ChatActivity.class);
        intent.putExtra("ref", "https://zconnectmulticommunity.firebaseio.com/communities/" +communityReference+"/features/forums/categories/"+forumKey);
        intent.putExtra("type","forums");
        intent.putExtra("name", forumName);
        intent.putExtra("tab",forumCategoryUID);
        intent.putExtra("key",forumKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(7, mBuilder.build());


    }

    private void productAddNotification() {

        final String communityName = data.get("communityName").toString();
        final String productName = data.get("productName").toString();
        final String productKey = data.get("productKey").toString();
        final String userName = data.get("userName").toString();
        final String userImage = data.get("userImage").toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,COMMUNITY_CHANNEL_ID);
        Intent intent = new Intent(NotificationService.this, OpenProductDetails.class);
//This is for ask Product exception

        try {
            final String productPrice = data.get("productPrice").toString();
            final String productImage = data.get("productImage").toString();

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
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentTitle(communityName)
                    .setContentText(userName + " is selling " + productName + " for ₹" + productPrice);

            intent.putExtra("key", productKey);
            intent.putExtra("type", ProductUtilities.TYPE_ADD_STR);


        }catch (Exception e){
            mBuilder.setSmallIcon(R.drawable.ic_local_mall_white_24dp)
                    .setSound(defaultSoundUri)
                    .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentTitle(communityName)
                    .setContentText(userName + " is asking for " + productName);

            intent.putExtra("key", productKey);
            intent.putExtra("type", ProductUtilities.TYPE_ASK_STR);
        }

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
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
        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,COMMUNITY_CHANNEL_ID);

        if(bitmap!=null) {
            NotificationCompat.BigPictureStyle style = new android.support.v4.app.NotificationCompat.BigPictureStyle();
            style.bigPicture(bitmap).setSummaryText(eventName + " will be happening at " + eventLocation).setBigContentTitle(communityName);
            mBuilder.setStyle(style);
        }

        mBuilder.setSmallIcon(R.drawable.ic_event_white_24dp)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setContentText(eventName + " will be happening at " + eventLocation);

        Intent intent = new Intent(NotificationService.this, OpenEventDetail.class);
        intent.putExtra("id", eventKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(5, mBuilder.build());

    }

    private void cabAddNotification(){

        final String communityName = data.get("communityName").toString();
        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText("Hey! People around you are using Cab Pool very often").setBigContentTitle(communityName);

        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,COMMUNITY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_directions_car_white_24dp)
                        .setStyle(style)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(NotificationService.this,R.color.colorPrimary))
                        .setContentTitle(communityName)
                        .setContentText("Hey! People around you are using Cab Pool very often");


        Intent intent = new Intent(NotificationService.this, CabPoolAll.class);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent intent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
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

        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);

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
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentTitle(communityName)
                .setContentText(userName + " shortlisted your product " + productName);

        Intent intent = new Intent(NotificationService.this, ShortlistedPeopleList.class);
        intent.putExtra("Key", productKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent pendingIntent1 = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent1);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(3, mBuilder.build());
    }

    private void cabLeaveNotification() {

        final String userName = data.get("userName").toString();
        final String cabKey = data.get("cabKey").toString();
        DatabaseReference cabpoolReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("allCabs").child(cabKey);
        final String communityName = data.get("communityName").toString();
        final String userImage = data.get("userImage").toString();

        NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.bigText(userName + " left your cab pool").setBigContentTitle(communityName);

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_directions_car_white_24dp)
                        .setStyle(style)
                        .setPriority(Notification.PRIORITY_HIGH)
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

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent pendingIntent = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
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

        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);

        Bitmap image = null;
        image  = getRoundedBitmap(userImage);

        if(image!=null){
            mBuilder.setLargeIcon(image);
        }

        mBuilder.setSmallIcon(R.drawable.ic_directions_car_white_24dp)
                .setStyle(style)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(ContextCompat.getColor(NotificationService.this, R.color.colorPrimary))
                .setContentTitle(communityName)
                .setContentText(userName + " joined your cab pool");

        Intent intent = new Intent(NotificationService.this, CabPoolListOfPeople.class);
        intent.putExtra("key", cabKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent pendingIntent = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
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

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,PERSONAL_CHANNEL_ID);

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
                .setPriority(Notification.PRIORITY_LOW)
                .setSound(defaultSoundUri)
                .setContentText(userName + " boosted your event " + eventName);


        Intent intent = new Intent(NotificationService.this, OpenEventDetail.class);
        intent.putExtra("id",eventKey);

        Intent intent0 = new Intent(NotificationService.this,HomeActivity.class);
        intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent[] intents = new Intent[]{intent0,intent};

        PendingIntent pendingIntent = PendingIntent.getActivities(NotificationService.this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
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

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

}