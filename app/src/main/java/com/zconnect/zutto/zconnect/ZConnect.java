package com.zconnect.zutto.zconnect;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by shubhamk on 8/2/17.
 */

public class ZConnect extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //Mutlidex is used when the number of methods or method calls including android's extends
        // to more than 65536. It is often required in large projects
        //You don't need to bother about the usage, it handles everything on its own
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //initialise Fresco when app starts
        //this will speed download process and is required by this library
        //more info http://frescolib.org/
        Fresco.initialize(this);

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("ZCM");
    }

    private String community= new String("Yo");

    public String getData(){
        return this.community;
    }

    public void setData(String d){
        this.community=d;
    }
}
