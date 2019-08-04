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
import android.support.annotation.RequiresApi;
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
import com.google.android.gms.tasks.OnCanceledListener;
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
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.commonModules.NotificationService;
import com.zconnect.zutto.zconnect.itemFormats.CommunityFeatures;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.RecentTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class LogoFlashActivity extends BaseActivity {
    /*private final String TAG = getClass().getSimpleName();*/
    //Request code permission request external storage
    private String TAG = LogoFlashActivity.class.getSimpleName();
    private final int RC_PERM_REQ_EXT_STORAGE = 7;
    private SimpleDraweeView bgImage;
    private DatabaseReference mDatabase,temp,temp2,temp3,temp4,temp5,t,t2,temporary,temporary2;
    private View bgColor;
    boolean flag = false;
    private String mReferrerUid;
            DatabaseReference communitiesInfoRef = FirebaseDatabase.getInstance().getReference().child("communitiesInfo");

    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };



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

        final SharedPreferences communitySP = getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        if (communityReference != null) {

            DatabaseReference duplicateCommunity = FirebaseDatabase.getInstance().getReference().child("communities").child("newTest");

            duplicateCommunity.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FirebaseDatabase.getInstance().getReference().child("communities").child("template").setValue(dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference duplicateCommunityInfo = FirebaseDatabase.getInstance().getReference().child("communitiesInfo").child("newTest");

            duplicateCommunityInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FirebaseDatabase.getInstance().getReference().child("communitiesInfo").child("template").setValue(dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("ui/logoFlash");

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("bgUrl")) {
                        bgImage.setImageURI(Uri.parse(dataSnapshot.child("bgUrl").getValue(String.class)));
                    } else {
//                        todo
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
                                        Log.d(TAG,"abc1 " + deepLink.getQueryParameter("eventID"));
                                        Intent i = new Intent(LogoFlashActivity.this, HomeActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);
                                        Intent intent = new Intent(LogoFlashActivity.this, OpenEventDetail.class);
                                        intent.putExtra("id", deepLink.getQueryParameter("eventID"));
                                        intent.putExtra("flag", true);
                                        startActivity(intent);
                                        flag = true;
                                    }
                                    else if(path.equals("/cabpooling/"))
                                    {
                                        Log.d(TAG,"abc1 " + deepLink.getQueryParameter("key"));
                                        Intent i = new Intent(LogoFlashActivity.this, HomeActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);

                                        Intent intent = new Intent(LogoFlashActivity.this, CabPoolListOfPeople.class);
                                        intent.putExtra("key", deepLink.getQueryParameter("key"));
                                        startActivity(intent);
                                        flag = true;
                                    }
                                    else if(path.equals("/openproduct/"))
                                    {
                                        Intent i = new Intent(LogoFlashActivity.this, HomeActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);

                                        Log.d(TAG,"abc1 " + deepLink.getQueryParameter("key"));
                                        Intent intent = new Intent(LogoFlashActivity.this, OpenProductDetails.class);
                                        intent.putExtra("key", deepLink.getQueryParameter("key"));
                                        intent.putExtra("type", deepLink.getQueryParameter("type"));
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
                            Log.d(TAG,"getDynamicLink:onFailure " + e);
                        }
                    });
        } else {

            Log.d(TAG, "no communityref");
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
                            Log.d(TAG, "inside dynamic link receiver");
                            //
                            // If the user isn't signed in and the pending Dynamic Link is
                            // an invitation, sign in the user anonymously, and record the
                            // referrer's UID.
                            //
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user == null && deepLink!=null && deepLink.getBooleanQueryParameter("referredBy", false))
                            {
                                mReferrerUid = deepLink.getQueryParameter("referredBy");
                                Log.d(TAG, "deep link not null");
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


        FirebaseDatabase.getInstance().getReference().child("communities").child("bitsGoa").child("Users1").child("yo").setValue("lo").addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Lokesh",e.toString());
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.d("Lokesh","cancel");
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Lokesh","Success");
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (arePermissionsEnabled()) {
                        // Do not wait so that user doesn't realise this is a new launch.
                        Log.d(TAG, " goint to home act 1");
                        Intent intent = new Intent(LogoFlashActivity.this, HomeActivity.class);
                        intent.putExtra("isReferred", mReferrerUid!=null);
                        intent.putExtra("referredBy", mReferrerUid);
                        Log.d(TAG,"goint to home act 1 " + mReferrerUid);
                        if(!flag)
                            startActivity(intent);
                        finish();
                    }else {
                        requestMultiplePermissions();
                    }
                }else {
                    Intent intent = new Intent(LogoFlashActivity.this, HomeActivity.class);
                    intent.putExtra("isReferred", mReferrerUid!=null);
                    intent.putExtra("referredBy", mReferrerUid);
                    Log.d(TAG,"goint to home act 1 " + mReferrerUid);
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
//        temp2 = FirebaseDatabase.getInstance().getReference().child("communitiesInfo").child("gim");
//
//        temp2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                FirebaseDatabase.getInstance().getReference().child("communitiesInfo").child("comm_code").setValue(dataSnapshot.getValue());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        temp3 = FirebaseDatabase.getInstance().getReference().child("communities").child("gim");
//
//        temp3.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                FirebaseDatabase.getInstance().getReference().child("communities").child("comm_code").setValue(dataSnapshot.getValue());
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
        Log.d(TAG, "inside create anonymous");
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
                                Log.d(TAG,"anonymoous user adding");
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference userRecord =
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("referredUsers")
                                                .child(user.getUid());
                                userRecord.child("referredBy").setValue(referrerUid);
                                Log.d(TAG,"anonymoous user added");

                            }
                            else {
                                Log.d(TAG,"anonymoous user failed to added");
                            }
                        }
                    });
        }
    }

//    private void userCommunitiesScript() {
//        temporary = FirebaseDatabase.getInstance().getReference().child("userCommunities");
//        communitiesInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot communitiesInfoSnapshot) {
//                for(DataSnapshot communitySnapshot : communitiesInfoSnapshot.getChildren())
//                {
//                    String communityID = communitySnapshot.getKey();
//                    Log.d("COMMMMMMM", communityID);
//                    temporary2 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityID).child("Users1");
//                    temporary2.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for (DataSnapshot shot: dataSnapshot.getChildren())
//                            {
//                                if (shot.hasChild("userType"))
//                                {
//                                    if ((shot.child("userType").getValue().toString().equals(UsersTypeUtilities.KEY_VERIFIED)) || (shot.child("userType").getValue().toString().equals(UsersTypeUtilities.KEY_ADMIN))) {
//                                        try {
//                                            temporary.child(shot.getKey()).child("communitiesJoined").child(communityID).setValue(communityID);
//
//                                        }
//                                        catch (Exception e){
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    else
//                                        continue;
//                                }
//                                else
//                                    continue;
//                            }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void createUserForumsForOldForums() {
//        communitiesInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot communitiesInfoSS) {
//                for(DataSnapshot communitySS : communitiesInfoSS.getChildren())
//                {
//                    String communityID = communitySS.getKey();
//                    DatabaseReference forumTabCatRef = communitiesInfoRef.getRoot().child("communities").child(communityID).child("features/forums/tabsCategories");
//                    DatabaseReference userForumsRef = forumTabCatRef.getParent().child("userForums");
//                    userForumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot userForumSS) {
//                            forumTabCatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot forumTabsSnapshot) {
//                                    for(DataSnapshot tabSnapshot : forumTabsSnapshot.getChildren())
//                                    {
//                                        for(DataSnapshot forumSnapshot : tabSnapshot.getChildren())
//                                        {
////                                            HashMap<String, Object> forumObj = forumSnapshot.getValue(HashMap.class);
////                                            Log.d("UUUUUUUUU", forumObj.get("users").toString()+"");
////                                            forumObj.remove("users");
////                                            Log.d("UUUUUUUUU", forumObj.get("users").toString()+"");
//                                            for(DataSnapshot userSS : forumSnapshot.child("users").getChildren())
//                                            {
//                                                Log.d("UUUUUUUUU", userSS.getKey());
//                                                Log.d("UUUUUUUUU", userForumSS.child(userSS.getKey()).child("joinedForums").hasChild(forumSnapshot.getKey())+"");
//                                                if(!userForumSS.child(userSS.getKey()).child("joinedForums").hasChild(forumSnapshot.getKey()))
//                                                {
//                                                    Log.d("UUUUUUUUU", userForumSS.getKey() + "");
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    //script to transfers users from 'Users1' with type 'admin' to admin node
//    private void createadminnode() {
//        communitiesInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot communitiesInfoSS) {
//                for(DataSnapshot communitySS : communitiesInfoSS.getChildren())
//                {
//                    String communityID = communitySS.getKey();
//                    Log.d("COMMMMMMM", communityID);
//                    if(communityID!=null) {
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityID).child("Users1");
//                        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityID).child("admins");
//                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                for (DataSnapshot childsnap : dataSnapshot.getChildren()) {
//                                    try{
//                                        if (childsnap.hasChild("userType") && childsnap.child("userType").getValue().toString().equals("admin")) {
//                                            databaseReference1.child(childsnap.getKey()).child("UID").setValue(childsnap.getKey());
//                                            databaseReference1.child(childsnap.getKey()).child("Username").setValue(childsnap.child("username").getValue());
//                                            databaseReference1.child(childsnap.getKey()).child("ImageThumb").setValue(childsnap.child("imageURL").getValue());
//                                        }
//                                    }
//                                    catch (Exception e)
//                                    {
//                                        Log.d("COMMMMMM", "ERROR : "+childsnap.getKey() + " " + communityID);
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
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







    //script to copy userPoints to userPointsNum
//    public static void copyUserPointsToUserPointsNum() {
//        Log.i("PSYCHO", "started");
//        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
//        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot shot : dataSnapshot.getChildren())
//                {
//                    if(shot.hasChild("userPoints"))
//                    {
//                        usersRef.child(shot.getKey()).child("userPointsNum").setValue(Integer.parseInt(shot.child("userPoints").getValue().toString()));
//                    }
//                    else
//                    {
//                        Log.i("PSYCHO", "no user points " + shot.getKey());
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


    //script to remove just joined community notificatin
//    public static void removeJustJoinedNotifFromHome() {
//        Log.d("QQQQ ENTERED ", "FUNC");
//        final DatabaseReference homeRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");
//        homeRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot shot : dataSnapshot.getChildren())
//                {
//                    if(shot.hasChild("feature") && shot.child("feature").getValue().toString().equals("Users"))
//                    {
//                        shot.getRef().removeValue();
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

    //script to fix count of infone categories
//    public static void countInfoneCatMembers() {
//        Log.d("QQQQQ STARTED", "1");
//        final DatabaseReference infoneRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("infone").child("categories");
//        final DatabaseReference infoneCatInfoRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("infone").child("categoriesInfo");
//        infoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot shot : dataSnapshot.getChildren())
//                {
//                    Log.d("QQQQQ INFONE" + shot.getKey(), String.valueOf(shot.getChildrenCount()));
//                    infoneCatInfoRef.child(shot.getKey()).child("totalContacts").setValue(shot.getChildrenCount());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
    //script to fix count of community members
//    public static void countCommunityMembers() {
//        Log.d("QQQQQ STARTED", "2");
//        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
//        final DatabaseReference communityInfoRef = FirebaseDatabase.getInstance().getReference().child("communitiesInfo").child(communityReference).child("size");
//        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d("QQQQQ COMM INFO" + dataSnapshot.getKey(), String.valueOf(dataSnapshot.getChildrenCount()));
//                communityInfoRef.setValue(dataSnapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

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

//    public void setUserPointsInExistingUsers() {
//        Log.d(TAG, "setUserPointsInExistingUsers started");
//        if(communityReference!=null)
//        {
//            DatabaseReference users1Ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
//            users1Ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
//                    {
//                        if(!(snapshot.hasChild("userPoints") && Long.parseLong(snapshot.child("userPoints").getValue().toString()) >=0))
//                        {
//                            Log.d(TAG, "this");
//                            snapshot.getRef().child("userPoints").setValue("0");
//                        }
//                        else
//                            Log.d(TAG, "USER POINTS" + snapshot.child("userPoints").getValue().toString());
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled(){
        for(String permission : PERMISSIONS){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions(){
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        new AlertDialog.Builder(this)
                                .setMessage("The application needs all the permission")
                                .setPositiveButton("Allow", (dialog, which) -> requestMultiplePermissions())
                                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                    }
                    return;
                }
            }
            Intent intent = new Intent(LogoFlashActivity.this, HomeActivity.class);
            intent.putExtra("isReferred", mReferrerUid!=null);
            intent.putExtra("referredBy", mReferrerUid);
            startActivity(intent);
            finish();
        }
    }
//
//
//    public boolean checkPermission() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
//                || ContextCompat.checkSelfPermission(LogoFlashActivity.this, PERMISSIONS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (ActivityCompat.shouldShowRequestPermissionRationale(LogoFlashActivity.this, PERMISSIONS)) {
//            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LogoFlashActivity.this);
//            alertBuilder.setCancelable(true);
//            alertBuilder.setTitle("Permission necessary");
//            alertBuilder.setMessage("Permission to read storage is required .");
//            alertBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
//                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                public void onClick(DialogInterface dialog, int which) {
//                    ActivityCompat.requestPermissions(LogoFlashActivity.this, PERMISSIONS, 7);
//                }
//            });
//            AlertDialog alert = alertBuilder.create();
//            alert.show();
//        } else {
//            ActivityCompat.requestPermissions(LogoFlashActivity.this, PERMISSIONS, RC_PERM_REQ_EXT_STORAGE);
//        }
//        return false;
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == RC_PERM_REQ_EXT_STORAGE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Intent intent = new Intent(LogoFlashActivity.this, HomeActivity.class);
//                Log.d(TAG,"goint to home act 2");
//                intent.putExtra("isReferred", mReferrerUid!=null);
//                intent.putExtra("referredBy", mReferrerUid);
//                startActivity(intent);
//                finish();
//            } else {
//                Toast.makeText(this, "Permission Denied !, Retrying.", Toast.LENGTH_SHORT).show();
//                checkPermission();
//            }
//        }
//    }
}

