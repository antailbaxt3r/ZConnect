package com.zconnect.zutto.zconnect.commonModules;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

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
import static android.content.Context.MODE_PRIVATE;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_EVENT_BOOST;


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

    public NotificationSender(Context ctx) {
        this.ctx = ctx;
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

        switch (notificationIdentifier){
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_BOOST:
                eventBoostNotification(ND.getItemKey(),ND.getItemName(),ND.getUserName(),ND.getCommunityName(),ND.getUserImage());
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_JOIN:
                cabJoinNotification(ND.getItemKey(),ND.getUserName(),ND.getCommunityName(),ND.getUserImage());
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_LEAVE:
                cabLeaveNotification(ND.getItemKey(),ND.getUserName(),ND.getCommunityName(),ND.getUserImage());
                break;
            case NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_SHORTLIST:
                productShortlistNotification(ND.getItemKey(),ND.getUserName(),ND.getUserMobileNumber(),ND.getItemName(),ND.getCommunityName(),ND.getUserImage());

        }
//        if(checkFrequency){
//            CompareFrequency();
//        }else{
//            sendNotification();
//            Log.e("noti","Notification sent");
//        }

        return null;
    }

    private void productShortlistNotification(String productKey,String userName,String userMobileNumber,String productName, String communityName,String userImage){

        creator = new RemoteMessage.Builder("data");
        creator.addData("userName",userName);
        creator.addData("userMobileNumber",userMobileNumber);
        creator.addData("productKey",productKey);
        creator.addData("productName",productName);
        creator.addData("communityName",communityName);
        creator.addData("userImage",userImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_SHORTLIST);

        sendNotification(true, productKey);
    }

    private void cabLeaveNotification(String cabKey,String userName,String communityName,String userImage) {
        creator = new RemoteMessage.Builder("data");

        creator.addData("userName",userName);
        creator.addData("cabKey",cabKey);
        creator.addData("communityName",communityName);
        creator.addData("userImage",userImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_LEAVE);

        sendNotification(true, cabKey);
    }

    private void cabJoinNotification(String cabKey,String userName,String communityName,String userImage) {
        creator = new RemoteMessage.Builder("data");

        creator.addData("userName",userName);
        creator.addData("cabKey",cabKey);
        creator.addData("communityName",communityName);
        creator.addData("userImage",userImage);

        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_JOIN);

        sendNotification(true, cabKey);
    }


    private void CompareFrequency(){

        DatabaseReference DB_NORMAL = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Notifications").child("frequency").child(type);
        DB_NORMAL.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                normal_frequency = dataSnapshot.getValue(Long.class);
                Log.e("noti:value of current F", String.valueOf( normal_frequency));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final DatabaseReference DB_CURRENT = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Notifications").child("current").child(type);
        DB_CURRENT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current_frequency = dataSnapshot.getValue(Long.class);
                Log.e("noti:value of current F", String.valueOf( current_frequency));


                if(String.valueOf(current_frequency).equals(String.valueOf(normal_frequency))){
                    DB_CURRENT.setValue(Long.valueOf(0));
                    sendNotification();
                    Log.e("noti","Notification sent");
                    result=true;
                    Log.e("noti",String.valueOf(result));

                }else{

                    DB_CURRENT.setValue(current_frequency+1);
                    result=false;
                    Log.e("noti","Notification not sent");
                    Log.e("noti",String.valueOf(result));

                }}

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void eventBoostNotification(String eventKey, String eventName, String userName,String communityName,String userImage) {
        creator = new RemoteMessage.Builder("data");

        creator.addData("eventKey",eventKey);
        creator.addData("eventName",eventName);
        creator.addData("userName",userName);
        creator.addData("userImage",userImage);

        creator.addData("communityName",communityName);
        creator.addData("Type",NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_BOOST);

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


    private void sendNotification(){

            try {
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());

                Map<String, Object> data = new HashMap<String, Object>();
                if (sendToKey)
                    data.put("to", "/topics/" + key);
                else
                    data.put("to", "/topics/" + type);

                data.put("data", creator.build().getData());

                JSONObject object = new JSONObject(data);
                Log.e("noti", "o:" + object.toString());
                String s2 = object.toString().replace("\\", "");
                os.writeBytes(s2);
                os.close();
            }catch (IOException e) {
                e.printStackTrace();
            }

//            //recieving response
//            InputStream is = connection.getInputStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
//            String line;
//            while ((line = rd.readLine()) != null) {
//                response.append(line);
//            }
//            rd.close();



    }

}
