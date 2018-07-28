package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.NotificationService;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;


public class LogoFlashActivity extends BaseActivity {
    /*private final String TAG = getClass().getSimpleName();*/
    //Request code permission request external storage
    private final int RC_PERM_REQ_EXT_STORAGE = 7;
    private ImageView bgImage;
    private DatabaseReference mDatabase,temp,temp2,t,t2;
    private View bgColor;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception ignore) {
        }
        setContentView(R.layout.activity_logo_flash);
        bgImage = (ImageView) findViewById(R.id.bgImage);
        bgColor = findViewById(R.id.bgColor);

        SharedPreferences communitySP = getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        if (communityReference != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("ui/logoFlash");

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("bgUrl")) {
                        bgImage.setImageURI(Uri.parse(dataSnapshot.child("bgUrl").getValue(String.class)));
                    } else {
                        bgColor.setVisibility(View.GONE);
                        bgImage.setBackground(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
                            }

                            if(deepLink!=null)
                            {
                                String path = deepLink.getPath();
                                if(!deepLink.getQueryParameter("communityRef").equals(communityReference))
                                {
                                    Snackbar snackbar = Snackbar.make(bgImage, "Can't open link. Sent from another community", BaseTransientBottomBar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                    snackbar.show();
                                }
                                else
                                {
                                    if(path.equals("/openevent/"))
                                    {
                                        Log.d("AAAAAAAA", "abc1 " + deepLink.getQueryParameter("eventID"));
                                        Intent intent = new Intent(LogoFlashActivity.this, OpenEventDetail.class);
                                        intent.putExtra("id", deepLink.getQueryParameter("eventID"));
                                        intent.putExtra("flag", true);
                                        startActivity(intent);
                                        flag = true;
                                    }
                                    else if(path.equals("/cabpooling/"))
                                    {
                                        Log.d("AAAAAAAA", "abc1 " + deepLink.getQueryParameter("key"));
                                        Intent intent = new Intent(LogoFlashActivity.this, CabPoolAll.class);
                                        intent.putExtra("key", deepLink.getQueryParameter("key"));
                                        startActivity(intent);
                                        flag = true;
                                    }
                                    else if(path.equals("/openproduct/"))
                                    {
                                        Log.d("AAAAAAAA", "abc1 " + deepLink.getQueryParameter("key"));
                                        Intent intent = new Intent(LogoFlashActivity.this, OpenProductDetails.class);
                                        intent.putExtra("key", deepLink.getQueryParameter("key"));
                                        startActivity(intent);
                                        flag = true;
                                    }
                                }
                            }
                            // Handle the deep link. For example, open the linked
                            // content, or apply promotional credit to the user's
                            // account.
                            // ...

                            // ...
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("AAAAAA", "getDynamicLink:onFailure " + e);
                        }
                    });
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("ui/logoFlash");

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("bgUrl")) {
                        bgImage.setImageURI(Uri.parse(dataSnapshot.child("bgUrl").getValue(String.class)));
                    } else {
                        bgColor.setVisibility(View.GONE);
                        bgImage.setBackground(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

//        showDebugDBAddressLogToast(this);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                if (checkPermission()) {
                    // Do not wait so that user doesn't realise this is a new launch.
                    if(!flag)
                        startActivity(new Intent(LogoFlashActivity.this, HomeActivity.class));
                    finish();
                }

            }
        }, 2000);

//                   temp = FirebaseDatabase.getInstance().getReference().child("communities").child("testCollege").child("Users1");
//            t = FirebaseDatabase.getInstance().getReference().child("communities").child("bitsGoa").child("Users1");
//
//            t.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for(DataSnapshot shot: dataSnapshot.getChildren()) {
//                        t.child(shot.getKey()).child("memberType").setValue(null);
//                        t.child(shot.getKey()).child("userType").setValue(UsersTypeUtilities.KEY_VERIFIED);
////                        temp2 = temp.child(shot.getKey());
////                        temp2.setValue(UsersTypeUtilities.KEY_VERIFIED);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

    }

//    public static void showDebugDBAddressLogToast(Context context) {
//        if (BuildConfig.DEBUG) {
//            try {
//                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
//                Method getAddressLog = debugDB.getMethod("getAddressLog");
//                Object value = getAddressLog.invoke(null);
//                Toast.makeText(context, (String) value, Toast.LENGTH_LONG).show();
//            } catch (Exception ignore) {
//
//            }
//        }
//    }


//    void link(){
//
//        final DatabaseReference mData, mPhone;
//        mData= FirebaseDatabase.getInstance().getReference().child("Users");
//        mPhone= FirebaseDatabase.getInstance().getReference().child("Phonebook");
//
//                mPhone.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot2) {
//
//                        int s=0,t=0,a=0;
//                            for (DataSnapshot Phone: dataSnapshot2.getChildren()) {
////                                t++;
////
////                                if(Phone.child("category").getValue().equals("A")){
////                                    a++;
////                                }
//                                try{
//                                    if (!Phone.hasChild("Uid")) {
//
//                                        DatabaseReference addUID = mPhone.child(Phone.getKey().toString());
//                                        Map<String, Object> taskMap = new HashMap<>();
//                                        taskMap.put("Uid", "null");
//                                        addUID.updateChildren(taskMap);
//                                    }
//                                }catch (Exception e){
//
//                                }
//                            }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }

//
////        mData.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////
//////                mPhone.addValueEventListener(new ValueEventListener() {
//////                    @Override
//////                    public void onDataChange(DataSnapshot dataSnapshot2) {
//////                        for (DataSnapshot User: dataSnapshot.getChildren()) {
//////                            for (DataSnapshot Phone: dataSnapshot2.getChildren()){
//////                                if (User.child("Email").getValue().equals(Phone.child("uid").getValue()) && User.child("Email").getValue().equals("f2015418@goa.bits-pilani.ac.in")){
////////                                    DatabaseReference addUID = mPhone.child(Phone.getKey().toString());
////////                                    Map<String, Object> taskMap = new HashMap<>();
////////                                    taskMap.put("Uid", User.getKey().toString());
////////                                    addUID.updateChildren(taskMap);
//////
//////                                }
//////                                Log.i("Uid",Phone.child("uid").getValue().toString());
//////                            }
//////                        }
//////                    }
//////
//////                    @Override
//////                    public void onCancelled(DatabaseError databaseError) {
//////
//////                    }
//////                });
////
////
////
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
//

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || ContextCompat.checkSelfPermission(LogoFlashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(LogoFlashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LogoFlashActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Permission to read storage is required .");
            alertBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(LogoFlashActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 7);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(LogoFlashActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERM_REQ_EXT_STORAGE);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERM_REQ_EXT_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(LogoFlashActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Permission Denied !, Retrying.", Toast.LENGTH_SHORT).show();
                checkPermission();
            }
        }
    }
}

