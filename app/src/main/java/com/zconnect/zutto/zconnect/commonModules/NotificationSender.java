package com.zconnect.zutto.zconnect.commonModules;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;


public class NotificationSender extends AsyncTask<NotificationItemFormat,Void,Void> {
    private RemoteMessage.Builder creator;
    private String key;
    private String type,temp;
    private boolean checkFrequency;
    private long current_frequency;
    private long normal_frequency;
    private boolean sendToKey;
    private boolean result;
    private URL url;
    private HttpURLConnection connection;
    Context ctx;
    private String userKey;

    public NotificationSender(Context ctx,String userKey) {
        this.ctx = ctx;
        this.userKey = userKey;

        try {
            url = new URL("https://fcm.googleapis.com/fcm/send");

           connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "key=AIzaSyC-OEClz9DTH1GDb9R0YGcqGsYqUXy50GI");
            connection.setDoOutput(true);
            connection.connect();
        }catch (Exception e){

        }
    }

    public NotificationSender(String key,String temp,String number,String event,String timeInMilli,String PersonEmail,
                              String Product,String type,boolean checkFrequency,boolean sendToKey,Context ctx){

        try {
            url = new URL("https://fcm.googleapis.com/fcm/send");

            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "key=AIzaSyC-OEClz9DTH1GDb9R0YGcqGsYqUXy50GI");
            connection.setDoOutput(true);
            connection.connect();
        }catch (Exception e){

        }
        try {
            String pName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            if (sendToKey)
                creator = new RemoteMessage.Builder(key);
            else
                creator = new RemoteMessage.Builder(type);

            creator.addData("Type", type);
            creator.addData("Person", pName);//pName == null ? name : pName);
            creator.addData("Contact", number);
            creator.addData("Key", key);
            creator.addData("PersonEmail", PersonEmail);
            creator.addData("Event", event);
            creator.addData("TimeInMilli", timeInMilli);
            creator.addData("Product", Product);
            creator.addData("Temp", temp);

            this.key = key;
            this.type = type;
            this.sendToKey = sendToKey;
            this.checkFrequency = checkFrequency;

        }catch (Exception e){

        }
    }

    @Override
    protected Void doInBackground(NotificationItemFormat... notificationDetails) {

        NotificationItemFormat ND = notificationDetails[0];

        String notificationIdentifier = ND.getNotificationIdentifier();

        switch (notificationIdentifier) {
            //Done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_BOOST:
                eventBoostNotification(ND.getItemKey(), ND.getItemName(), ND.getUserName(), ND.getCommunityName(), ND.getUserImage(),ND.getRecieverKey());
                break;
            //Done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_JOIN:
                cabJoinNotification(ND.getItemKey(), ND.getUserName(), ND.getCommunityName(), ND.getUserImage(),ND.getRecieverKey());
                break;
            //Done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_LEAVE:
                cabLeaveNotification(ND.getItemKey(), ND.getUserName(), ND.getCommunityName(), ND.getUserImage());
                break;
            //done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_SHORTLIST:
                productShortlistNotification(ND.getItemKey(), ND.getUserName(), ND.getUserMobileNumber(), ND.getItemName(), ND.getCommunityName(), ND.getUserImage(),ND.getRecieverKey());
                break;
                //done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD:
                cabAddNotification(ND.getCommunityName());
                break;
                //done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD:
                eventAddNotification(ND.getCommunityName(),ND.getItemName(),ND.getItemLocation(),ND.getItemKey(),ND.getItemImage());
                break;
                //done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD:
                productAddNotification(ND.getCommunityName(),ND.getItemName(),ND.getItemPrice(),ND.getItemKey(),ND.getItemImage(),ND.getUserName(),ND.getUserImage(), ND.getItemType());
                break;
                //done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD:
                forumAddNotification(ND.getCommunityName(), ND.getItemName(),ND.getItemCategory(),ND.getItemCategoryUID(),ND.getItemKey(),ND.getUserName(),ND.getUserImage());
                break;
                //Done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_CATEGORY_ADD:
                infoneCategoryAddNotification(ND.getCommunityName(),ND.getItemName(),ND.getItemKey(),ND.getItemImage(),ND.getItemCategoryAdmin());
                break;

                //Done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_LOVE :
                infoneLoveNotification(ND.getCommunityName(),ND.getUserName(),ND.getUserImage(),ND.getItemKey());
                break;
                //Done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_LIKE:
                infoneLikeNotification(ND.getCommunityName(),ND.getUserName(),ND.getUserImage(),ND.getItemKey());
                break;

                //Done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_FORUM:
                forumChatNotification(ND.getCommunityName(),ND.getUserName(),ND.getUserImage(),ND.getItemMessage(),ND.getItemName(),ND.getItemCategoryUID(),ND.getItemKey());
                break;

                //Done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_PRODUCT:
                productChatNotification(ND.getUserName(), ND.getCommunityName(), ND.getUserImage(), ND.getItemMessage(), ND.getItemKey(), ND.getItemName());
                break;
                //DOne
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_CAB:
                cabChatNotification(ND.getCommunityName(),ND.getUserName(), ND.getUserImage(), ND.getItemMessage(), ND.getItemKey());
                break;
                //done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_EVENT:
                eventChatNotification(ND.getCommunityName(),ND.getUserName(), ND.getUserImage(),ND.getItemMessage(), ND.getItemKey(), ND.getItemName());
                break;
                //done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_POST:
                postChatNotification(ND.getCommunityName(), ND.getUserName(), ND.getUserImage(), ND.getItemMessage(), ND.getItemKey());
                break;
                //done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_REQUEST_CALL:
                requestCallNotification(ND.getCommunityName(),ND.getUserName(),ND.getUserImage(),ND.getUserMobileNumber(),ND.getItemKey());
                break;
                //done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_NEW_USER_ACCEPT:
                newUserAcceptNotification(ND.getCommunityName(),ND.getItemKey(),ND.getUserName(),ND.getUserImage());
                break;
                //done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_NEW_USER_REJECT:
                newUserRejectNotification(ND.getCommunityName(),ND.getItemKey(),ND.getUserName(),ND.getUserImage());
                break;
                //Done
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_STATUS_LIKED:
                statusLikeNotification(ND.getItemKey(), ND.getCommunityName(), ND.getUserName(), ND.getUserImage(), ND.getItemLikeCount());
                Log.d("LIKESSSS", "2");
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_IMAGE_URL:
                notificationWithImage(ND.getItemImage(), ND.getItemURL(), ND.getItemTitle(), ND.getItemMessage());
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_TEXT_URL:
                notificationWithoutImage(ND.getItemURL(), ND.getItemTitle(), ND.getItemMessage());
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_NOTICES_ADD:
                noticeAddNotification( ND.getCommunityName(),ND.getItemName(),ND.getItemKey(),ND.getItemImage(), ND.getUserName(), ND.getUserImage());
                break;
        }

        return null;
    }

    private void noticeAddNotification(String communityName, String noticeName,String noticeKey,String noticeImage,String userName,String userImage) {

        creator = new RemoteMessage.Builder("data");

        creator.addData("communityName",communityName);
        creator.addData("noticeName",noticeName);
        creator.addData("noticeKey",noticeKey);
        creator.addData("noticeImage",noticeImage);

        creator.addData("userName", userName);
        creator.addData("userImage", userImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_NOTICES_ADD);
        creator.addData("userKey",userKey);

        compareFrequency(NotificationIdentifierUtilities.KEY_NOTIFICATION_NOTICES_ADD,NotificationIdentifierUtilities.KEY_NOTIFICATION_NOTICES_ADD_FREQUENCY_STR,null,null,null,null,null);
    }

    private void newUserRejectNotification(String communityName,String receiverKey,String username,String userimg) {
        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_NEW_USER_REJECT);
        creator.addData("userKey",userKey);

        UserItemFormat userItemFormat=new UserItemFormat();
        userItemFormat.setUserUID(userKey);
        userItemFormat.setUsername(username);
        userItemFormat.setImageURL(userimg);
        GlobalFunctions.inAppNotifications("rejected your request to join the community","please add your details and try again",userItemFormat,false,"verification",null,receiverKey);
        sendNotification(true,receiverKey);
    }

    private void newUserAcceptNotification(String communityName,String receiverKey,String username,String userimg) {
        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);
        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_NEW_USER_ACCEPT);
        creator.addData("userKey",userKey);

        UserItemFormat userItemFormat=new UserItemFormat();
        userItemFormat.setUserUID(userKey);
        userItemFormat.setUsername(username);
        userItemFormat.setImageURL(userimg);
        GlobalFunctions.inAppNotifications("accepted your request to join the community","congratulations your part of this community",userItemFormat,false,"verification",null,receiverKey);
        sendNotification(true,receiverKey);
    }

    private void statusLikeNotification(String statusKey, String communityName, String userName, String userImage, long likeCount)
    {
        creator = new RemoteMessage.Builder("data");
        if(likeCount%3==0 && likeCount!=0)
            creator.addData("likeCount", String.valueOf(likeCount - 1));
        else
            creator.addData("likeCount", "");
        creator.addData("statusKey", statusKey);
        creator.addData("communityName", communityName);
        creator.addData("userName", userName);
        creator.addData("userImage", userImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_STATUS_LIKED);
        creator.addData("userKey",userKey);

        //subscribe to topic first
        sendNotification(true, statusKey);
        Log.d("LIKESSSS", "3");

    }

    private void requestCallNotification(String communityName,String userName,String userImage,String userMobileNumber,String receiverKey) {

        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);
        creator.addData("userName",userName);
        creator.addData("userImage",userImage);
        creator.addData("userMobileNumber",userMobileNumber);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_REQUEST_CALL);
        creator.addData("userKey",userKey);

        sendNotification(true,receiverKey);
    }

    private void postChatNotification(String communityName, String userName, String userImage, String postMessage, String postKey) {

        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);
        creator.addData("communityReference",communityReference);

        creator.addData("userName",userName);
        creator.addData("userImage",userImage);

        creator.addData("postMessage",postMessage);
        creator.addData("postKey",postKey);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_POST);
        creator.addData("userKey",userKey);

        sendNotification(true, postKey);
    }

    private void eventChatNotification(String communityName, String userName, String userImage, String eventMessage, String eventKey, String eventName) {

        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);
        creator.addData("communityReference",communityReference);

        creator.addData("userName",userName);
        creator.addData("userImage",userImage);

        creator.addData("eventMessage",eventMessage);
        creator.addData("eventKey",eventKey);
        creator.addData("eventName",eventName);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_EVENT);
        creator.addData("userKey",userKey);

        sendNotification(true, eventKey);

    }

    private void cabChatNotification(String communityName, String userName, String userImage, String cabMessage, String cabKey) {



        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);
        creator.addData("communityReference",communityReference);

        creator.addData("userName",userName);
        creator.addData("userImage",userImage);

        creator.addData("cabMessage",cabMessage);
        creator.addData("cabKey",cabKey);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_CAB);
        creator.addData("userKey",userKey);

        sendNotification(true, cabKey);

    }

    private void productChatNotification(String userName, String communityName, String userImage, String productMessage, String productKey, String productName) {


        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);
        creator.addData("communityReference",communityReference);

        creator.addData("userName",userName);
        creator.addData("userImage",userImage);

        creator.addData("productMessage",productMessage);
        creator.addData("productKey",productKey);
        creator.addData("productName",productName);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_PRODUCT);
        creator.addData("userKey",userKey);

        sendNotification(true, productKey);

    }

    private void forumChatNotification(String communityName, String userName, String userImage,String forumMessage,String forumName,String forumCategoryUID,String forumKey) {

        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);
        creator.addData("communityReference",communityReference);

        creator.addData("userName",userName);
        creator.addData("userImage",userImage);

        creator.addData("forumMessage",forumMessage);
        creator.addData("forumName",forumName);
        creator.addData("forumCategoryUID",forumCategoryUID);
        creator.addData("forumKey",forumKey);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_FORUM);
        creator.addData("userKey",userKey);

        sendNotification(true, forumKey);
    }


    private void infoneLikeNotification(String communityName, String userName, String userImage, String infoneUserKey) {
        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);
        creator.addData("userName",userName);
        creator.addData("userImage",userImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_LIKE);
        creator.addData("userKey",userKey);

        sendNotification(true, infoneUserKey);
    }

    private void infoneLoveNotification(String communityName, String userName, String userImage, String infoneUserKey) {

        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);
        creator.addData("userName",userName);
        creator.addData("userImage",userImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_LOVE);
        creator.addData("userKey",userKey);

        sendNotification(true, infoneUserKey);

    }


    private void infoneCategoryAddNotification(String communityName,String categoryName,String categoryID,String categoryImage,String categoryAdmin) {

        creator = new RemoteMessage.Builder("data");
        creator.addData("communityName",communityName);
        creator.addData("categoryName",categoryName);
        creator.addData("categoryID",categoryID);
        creator.addData("categoryImage",categoryImage);
        creator.addData("categoryAdmin",categoryAdmin);


        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_CATEGORY_ADD);
        creator.addData("userKey",userKey);

        sendNotification(true,NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_CATEGORY_ADD + communityReference);

    }

    private void forumAddNotification(String communityName, String forumName, String forumCategory, String forumCategoryUID, String forumKey, String userName,String userImage) {


        creator = new RemoteMessage.Builder("data");

        creator.addData("communityName",communityName);
        creator.addData("communityReference",communityReference);

        creator.addData("forumName",forumName);
        creator.addData("forumCategory",forumCategory);
        creator.addData("forumCategoryUID",forumCategoryUID);
        creator.addData("forumKey",forumKey);

        creator.addData("userName", userName);
        creator.addData("userImage", userImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD);
        creator.addData("userKey",userKey);

        compareFrequency(NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD,NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD_FREQUENCY_STR,null,null,null,null,null);
    }

    private void productAddNotification(String communityName, String productName,String productPrice,String productKey,String productImage,String userName,String userImage, String productType) {


        creator = new RemoteMessage.Builder("data");
        HashMap<String,Object> metadata = new HashMap<>();

        creator.addData("communityName",communityName);
        creator.addData("productName",productName);
        creator.addData("productPrice",productPrice);
        creator.addData("productKey",productKey);
        creator.addData("productImage",productImage);
        creator.addData("productType", productType);

        creator.addData("userName", userName);
        creator.addData("userImage", userImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD);
        creator.addData("userKey",userKey);

        UserItemFormat userItemFormat=new UserItemFormat();
        userItemFormat.setUserUID(UserUtilities.currentUser.getUserUID());
        userItemFormat.setUsername(UserUtilities.currentUser.getUsername());
        userItemFormat.setImageURL(UserUtilities.currentUser.getImageURL());
        metadata.put("key",productKey);
        metadata.put("type",productType);
        compareFrequency(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD,NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD_FREQUENCY_STR," is selling "+productName,"price: "+productPrice,userItemFormat,"productAdd",metadata);
    }

    private void eventAddNotification(String communityName,String eventName,String eventLocation,String eventKey,String eventImage) {
        creator = new RemoteMessage.Builder("data");
     HashMap<String,Object> metadata = new HashMap<>();
        creator.addData("communityName",communityName);
        creator.addData("communityReference",communityReference);

        creator.addData("eventName",eventName);
        creator.addData("eventLocation",eventLocation);
        creator.addData("eventKey",eventKey);
        creator.addData("eventImage",eventImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD);
        creator.addData("userKey",userKey);

        UserItemFormat userItemFormat=new UserItemFormat();
        userItemFormat.setUserUID(UserUtilities.currentUser.getUserUID());
        userItemFormat.setUsername("");
        userItemFormat.setImageURL("");
        metadata.put("id",eventKey);
        compareFrequency(NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD,NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD_FREQUENCY_STR,eventName+" will be happening at ","location: "+eventLocation,userItemFormat,"eventAdd",metadata);
    }

    private void cabAddNotification(String communityName) {

        creator = new RemoteMessage.Builder("data");
        HashMap<String,Object> metadata = new HashMap<>();
        creator.addData("communityName",communityName);
        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD);
        creator.addData("userKey",userKey);
        metadata.put("key",userKey);
        UserItemFormat userItemFormat=new UserItemFormat();
        userItemFormat.setUsername("");
        userItemFormat.setImageURL("");
        userItemFormat.setUserUID(UserUtilities.currentUser.getUserUID());
        compareFrequency(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD,NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD_FREQUENCY_STR,"Hey! People around you are using Cab Pool very often","",userItemFormat,"cabAdd",metadata);
    }

    private void productShortlistNotification(String productKey,String userName,String userMobileNumber,String productName, String communityName,String userImage,String recieverKey){

        creator = new RemoteMessage.Builder("data");
        HashMap<String,Object> metadata = new HashMap<>();
        creator.addData("userName",userName);
        creator.addData("userMobileNumber",userMobileNumber);
        creator.addData("productKey",productKey);
        creator.addData("productName",productName);
        creator.addData("communityName",communityName);
        creator.addData("userImage",userImage);
        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_SHORTLIST);
        creator.addData("userKey",userKey);
        metadata.put("key",productKey);
        UserItemFormat userItemFormat=new UserItemFormat();
        userItemFormat.setUserUID(userKey);
        userItemFormat.setUsername(userName);
        userItemFormat.setImageURL(userImage);
        GlobalFunctions.inAppNotifications("shortlisted your product","",userItemFormat,false,"productShortlist",metadata,recieverKey);
        sendNotification(true, productKey);
    }

    private void cabLeaveNotification(String cabKey,String userName,String communityName,String userImage) {
        creator = new RemoteMessage.Builder("data");

        creator.addData("userName",userName);
        creator.addData("cabKey",cabKey);
        creator.addData("communityName",communityName);
        creator.addData("userImage",userImage);


        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_LEAVE);
        creator.addData("userKey",userKey);

        sendNotification(true, cabKey);
    }

    private void cabJoinNotification(String cabKey,String userName,String communityName,String userImage,String recieverKey) {
        creator = new RemoteMessage.Builder("data");

        creator.addData("userName",userName);
        creator.addData("cabKey",cabKey);
        creator.addData("communityName",communityName);
        creator.addData("userImage",userImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_JOIN);
        creator.addData("userKey",userKey);
        UserItemFormat userItemFormat=new UserItemFormat();
        userItemFormat.setUserUID(userKey);
        userItemFormat.setUsername(userName);
        userItemFormat.setImageURL(userImage);
        GlobalFunctions.inAppNotifications("joined your cabpool","",userItemFormat,false,"cabpoolJoin",null,recieverKey);
        sendNotification(true, cabKey);
    }

    public void eventBoostNotification(String eventKey, String eventName, String userName,String communityName,String userImage,String recieverKey) {
        creator = new RemoteMessage.Builder("data");
        HashMap<String,Object> metadata = new HashMap<>();
        creator.addData("eventKey",eventKey);
        creator.addData("eventName",eventName);
        creator.addData("userName",userName);
        creator.addData("userImage",userImage);

        creator.addData("communityName",communityName);
        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_BOOST);
        creator.addData("userKey",userKey);
        metadata.put("key",eventKey);
        UserItemFormat userItemFormat=new UserItemFormat();
        userItemFormat.setUserUID(userKey);
        userItemFormat.setUsername(userName);
        userItemFormat.setImageURL(userImage);
        GlobalFunctions.inAppNotifications("boosted your event","",userItemFormat,false,"eventBoost",metadata,recieverKey);
        sendNotification(true,eventKey);
    }


    public void sendNotification(Boolean isTopic, String condition){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            url = new URL("https://fcm.googleapis.com/fcm/send");

            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "key=AIzaSyC-OEClz9DTH1GDb9R0YGcqGsYqUXy50GI");
            connection.setDoOutput(true);
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());

            Map<String, Object> data = new HashMap<String, Object>();

            if(isTopic) {
                data.put("to","/topics/" + condition);
            }
            data.put("data", creator.build().getData());

            JSONObject object = new JSONObject(data);

            String s2 = object.toString().replace("\\", "");
            os.writeBytes(s2);
            os.close();

                        //recieving response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();

        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void notificationWithImage(String notificationImage, String notificationURL, String notificationTitle, String notificationMessage){
        creator=new RemoteMessage.Builder("data");
        creator.addData("imageURL",notificationImage);
        creator.addData("URL",notificationURL);
        creator.addData("title",notificationTitle);
        creator.addData("message",notificationMessage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_IMAGE_URL);
        creator.addData("userKey",userKey);
        sendNotification(true,communityReference);
    }

    private void notificationWithoutImage(String noticationURL, String notificationTitle, String notificationMessage){
        creator=new RemoteMessage.Builder("data");
        creator.addData("URL",noticationURL);
        creator.addData("title",notificationTitle);
        creator.addData("message",notificationMessage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_TEXT_URL);
        creator.addData("userKey",userKey);

        sendNotification(true,communityReference);
    }


    private void compareFrequency(final String notificationIdentifier, final String notificationType, final String title, final String desc, final UserItemFormat userItemFormat,final String type,final HashMap<String,Object> metadata){

        DatabaseReference DB_NORMAL = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Notifications").child("frequency").child(notificationType);
        final DatabaseReference DB_CURRENT = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Notifications").child("current").child(notificationType);

        DB_NORMAL.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    normal_frequency = dataSnapshot.getValue(Long.class);

                    DB_CURRENT.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                current_frequency = dataSnapshot.getValue(Long.class);

                                if(current_frequency == normal_frequency){

                                    DB_CURRENT.setValue(Long.valueOf(0));
                                    if(title!=null)
                                    GlobalFunctions.inAppNotifications(title,desc,userItemFormat,true,type,metadata,null);
                                    sendNotification(true,notificationIdentifier + communityReference);

                                }else{
                                    DB_CURRENT.setValue(current_frequency + 1);
                                }
                            }catch (Exception e){}
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
