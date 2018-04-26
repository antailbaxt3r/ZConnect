package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.SharedPreferences;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by aayush on 12/2/18.
 */

public class NotificationSender extends AsyncTask<Void,Void,Void> {
    private RemoteMessage.Builder creator;
    private String key;
    private String type,temp;
    private boolean checkFrequency;
    private long current_frequency;
    private long normal_frequency;
    private boolean sendToKey;
    private boolean result;
    SharedPreferences communitySP;
    String communityReference;


    public NotificationSender(String key,String temp,String number,String event,String timeInMilli,String PersonEmail,
                              String Product,String type,boolean checkFrequency,boolean sendToKey,Context ctx){
        String pName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(sendToKey)
            creator = new RemoteMessage.Builder(key);
        else
            creator = new RemoteMessage.Builder(type);

        creator.addData("Type", type);
        creator.addData("Person", pName);//pName == null ? name : pName);
        creator.addData("Contact", number);
        creator.addData("Key", key);
        creator.addData("PersonEmail", PersonEmail);
        creator.addData("Event", event);
        creator.addData("TimeInMilli",timeInMilli);
        creator.addData("Product",Product);
        creator.addData("Temp",temp);

        this.key=key;
        this.type=type;
        this.sendToKey=sendToKey;
        this.checkFrequency=checkFrequency;

        communitySP = ctx.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.e("noti","do in background");
                if(checkFrequency){
                    CompareFrequency();
                }else{
                    sendNotification();
                    Log.e("noti","Notification sent");

                }

        return null;
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


    private void sendNotification(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        try {

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "key=AIzaSyC-OEClz9DTH1GDb9R0YGcqGsYqUXy50GI");
            connection.setDoOutput(true);
            connection.connect();


            DataOutputStream os = new DataOutputStream(connection.getOutputStream());


            Map<String, Object> data = new HashMap<String, Object>();
            if(sendToKey)
                data.put("to", "/topics/" + key);
            else
                data.put("to","/topics/"+type);

            data.put("data", creator.build().getData());

            JSONObject object = new JSONObject(data);
            Log.e("noti","o:"+object.toString());
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

            Log.e("noti","r:"+response.toString());


        } catch (Exception e) {
            Log.e("msg","nahi ho raha");
            e.printStackTrace();
        }




    }

}
