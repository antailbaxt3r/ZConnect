package com.zconnect.zutto.zconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zconnect.zutto.zconnect.adapters.NotificationRVAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class NotificationAdmin extends AppCompatActivity {

    ArrayList notification_type=new ArrayList<>(Arrays.asList("Notification with image","Notification without image"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_admin);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.menu_notifications_add);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new NotificationRVAdapter(notification_type,getBaseContext()));



    }
}
