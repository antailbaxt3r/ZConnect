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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.NotificationService;
import com.zconnect.zutto.zconnect.itemFormats.CommunityFeatures;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class LogoFlashActivity extends BaseActivity {
    /*private final String TAG = getClass().getSimpleName();*/
    //Request code permission request external storage
    private final int RC_PERM_REQ_EXT_STORAGE = 7;
    private SimpleDraweeView bgImage;
    private DatabaseReference mDatabase,temp,temp2,temp3,temp4,temp5,t,t2;
    private View bgColor;
    boolean flag = false;
    private String mReferrerUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception ignore) {
        }
        Fresco.initialize(this);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_logo_flash);
        bgImage =  findViewById(R.id.bgImage);
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

                            // Handle the deep link. For example, open the linked
                            // content, or apply promotional credit to the user's
                            // account.
                            // ...

                            // ...
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
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("AAAAAA", "getDynamicLink:onFailure " + e);
                        }
                    });
        } else {

            Log.d("RRRR", "no communityref");
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
                            }
                            Log.d("RRRR", "inside dynamic link receiver");
                            //
                            // If the user isn't signed in and the pending Dynamic Link is
                            // an invitation, sign in the user anonymously, and record the
                            // referrer's UID.
                            //
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user == null && deepLink!=null && deepLink.getBooleanQueryParameter("referredBy", false))
                            {
                                mReferrerUid = deepLink.getQueryParameter("referredBy");
                                Log.d("RRRR", "deep link not null");
                                createAnonymousAccountWithReferrerInfo(mReferrerUid);
                            }
                        }
                    });
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
                    Log.d("RRRR goint to home act", "1");
                    Intent intent = new Intent(LogoFlashActivity.this, HomeActivity.class);
                    intent.putExtra("isReferred", mReferrerUid!=null);
                    intent.putExtra("referredBy", mReferrerUid);
                    Log.d("RRRR goint to home act", "1 " + mReferrerUid);
                    if(!flag)
                        startActivity(intent);
                    finish();
                }

            }
        }, 2000);



//        temp = FirebaseDatabase.getInstance().getReference().child("communities").child("testCollege").child("Users1");
//
//
//        temp.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot shot: dataSnapshot.getChildren()){
//                    try {
//                        temp.child(shot.getKey()).child("points").setValue("0");
//                    }catch (Exception e){}
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//
//
//        temp.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                FirebaseDatabase.getInstance().getReference().child("communities").child("bitsPilani").setValue(dataSnapshot.getValue());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        temp2 = FirebaseDatabase.getInstance().getReference().child("communitiesInfo").child("gmc");
//
//        temp2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                FirebaseDatabase.getInstance().getReference().child("communitiesInfo").child("gim").setValue(dataSnapshot.getValue());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//



    }

    private void createAnonymousAccountWithReferrerInfo(final String referrerUid) {
        Log.d("RRRR", "inside create anonymous");
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            FirebaseAuth.getInstance()
                    .signInAnonymously()
//                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        // Keep track of the referrer in the RTDB. Database calls
//                        // will depend on the structure of your app's RTDB.
//                        Log.d("RRRR", "anonymoous user adding");
//                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                        DatabaseReference userRecord =
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("referredUsers")
//                                        .child(user.getUid());
//                        userRecord.child("referredBy").setValue(referrerUid);
//                        Log.d("RRRR", "anonymoous user added");
//                    }
//                });
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                AuthResult authResult = task.getResult();
                                Log.d("RRRR", "anonymoous user adding");
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference userRecord =
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("referredUsers")
                                                .child(user.getUid());
                                userRecord.child("referredBy").setValue(referrerUid);
                                Log.d("RRRR", "anonymoous user added");

                            }
                            else {
                                Log.d("RRRR", "anonymoous user failed to added");
                            }
                        }
                    });
        }
    }

    ///////////script of adding forumAdmin node/////////
//    public void addForumAdminNode() {
//        Log.d("FORUUUUUM", "YEAAA");
//        final DatabaseReference tabsCategoriesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories");
//
//        tabsCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d("FORUUUUUM DS", dataSnapshot.getKey());
//                for(DataSnapshot snapshot: dataSnapshot.getChildren())
//                {
//                    Log.d("FORUUUUUM SS", snapshot.getKey());
//                    for (DataSnapshot snapshot1: snapshot.getChildren())
//                    {
//                        Log.d("FORUUUUUM SS1", snapshot1.getKey());
//                        Map<String, Object> forumAdminMap = new HashMap<String, Object>();
//                        for(DataSnapshot snapshot2: snapshot1.child("users").getChildren())
//                        {
//                            Log.d("FORUUUUUM SS2", snapshot2.getKey());
//                            if(!snapshot2.hasChild("userType"))
//                                continue;
//                            if(snapshot2.child("userType").getValue().equals(ForumsUserTypeUtilities.KEY_ADMIN))
//                            {
//                                Map<String, Object> childMap = new HashMap<String, Object>();
//                                childMap.put("userUID", snapshot2.getKey());
//                                forumAdminMap.put(snapshot2.getKey(), childMap);
//                            }
//                        }
//                        Log.d("FORUUUUUM SS1", "Before posting");
//                        snapshot1.getRef().child("forumAdmins").setValue(forumAdminMap);
//                        Log.d("FORUUUUUM SS1", "After posting");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

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
                Intent intent = new Intent(LogoFlashActivity.this, HomeActivity.class);
                Log.d("RRRR goint to home act", "2");
                intent.putExtra("isReferred", mReferrerUid!=null);
                intent.putExtra("referredBy", mReferrerUid);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Permission Denied !, Retrying.", Toast.LENGTH_SHORT).show();
                checkPermission();
            }
        }
    }
}

