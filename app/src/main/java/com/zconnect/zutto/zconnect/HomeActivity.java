package com.zconnect.zutto.zconnect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.commonModules.newUserVerificationAlert;
import com.zconnect.zutto.zconnect.fragments.JoinedForums;
import com.zconnect.zutto.zconnect.fragments.MyProfileFragment;
import com.zconnect.zutto.zconnect.fragments.NotificationsFragment;
import com.zconnect.zutto.zconnect.itemFormats.CommunityFeatures;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.pools.MyOrdersActivity;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.RequestCodes;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;
import com.zconnect.zutto.zconnect.fragments.HomeBottomSheet;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, Recents.OnHomeIconListener {

    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar_app_bar_home)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    TabLayout tabs;
    String url = "https://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect";
    private boolean doubleBackToExitPressedOnce = false;
    private ValueEventListener editProfileValueEventListener;
    private ValueEventListener popupsListener;
    private ValueEventListener uiDbListener;
    private TextView navHeaderUserNameTv;
    private SimpleDraweeView navHeaderBackground;
    private MenuItem editProfileItem;
    private ActionBarDrawerToggle toggle;
    private String userEmail;
    private String username;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference currentUserReference;
    private DatabaseReference mDatabasePopUps;
    private DatabaseReference communityInfoRef;

    private DatabaseReference communityFeaturesRef;
    private Menu nav_Menu;

    private Boolean isFabOpen;
    private FloatingActionButton fab, fab1, fab2, fab3;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    private DatabaseReference uiDbRef;
    private FirebaseUser mUser;
    private boolean guestMode;
    private SharedPreferences defaultPrefs;
    private SharedPreferences guestPrefs;
    private AlertDialog addContactDialog;
    private Fragment recent, forums, shop, myProfile, infone, notifications;
    public Boolean flag = false;
    public Boolean setTitleFlag = true;

    private DatabaseReference mDatabaseStats;
    private DatabaseReference mDatabaseUserStats;
    private String navHeaderBackGroundImageUrl = null;
    private String Title;
    Context context;

    TextView[] tabTitle = new TextView[6];
    SimpleDraweeView[] tabImage = new SimpleDraweeView[6];
    ImageView[] tabNotificationCircle = new ImageView[6];

    public TabLayout.Tab recentsT, forumsT, addT, infoneT, profileT, notificationsT;
    HomeBottomSheet bottomSheetFragment;
    private LinearLayoutManager recentsLinearLayoutManager;

    public HomeActivity() {

        isFabOpen = false;
    }


    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        TODO check for loopholes in referral system

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        mAuth = FirebaseAuth.getInstance();

        bottomSheetFragment = new HomeBottomSheet();


        View navHeader = navigationView.getHeaderView(0);

        // Navigation Drawer initialization
        navHeaderUserNameTv = (TextView) navHeader.findViewById(R.id.tv_name_nav_header);
        navHeaderBackground = (SimpleDraweeView) navHeader.findViewById(R.id.iv_nav_header_background);


        navigationView.setItemIconTintList(null);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) tabs.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        navigationView.setNavigationItemSelectedListener(this);
        editProfileItem = navigationView.getMenu().findItem(R.id.edit_profile);



        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("ZCM");
//        fixFirebase();
//        testTheFix();
        initListeners();

        tabs();
        setForumNotificationDot();


    }

//    private void testTheFix() {
//        final DatabaseReference userForums = FirebaseDatabase.getInstance().getReference().child("communities").child("testCollege").child("features").child("forums").child("newUserForums");
//        userForums.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot users: dataSnapshot.getChildren()){
//                    String tot = users.c
//                    for(DataSnapshot forums: users.getChildren()){
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

////fixFirebase to add respective forum details under each user. change "testCollege" to desired community/use loops
    void fixFirebase() {

        final DatabaseReference userForums = FirebaseDatabase.getInstance().getReference().child("communities").child("testCollege").child("features").child("forums").child("userForums");
        Log.d("Fix", "Starting fixFirebase");
        DatabaseReference tabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child("testCollege").child("features").child("forums").child("tabsCategories");
        tabsCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot tablist : dataSnapshot.getChildren()) {
                    Log.d("Fix", tablist.toString());
                    String tabName = tablist.getKey();
                    for (DataSnapshot forumlist : tablist.getChildren()) {
                        Log.d("Fix", forumlist.toString());
                        for (DataSnapshot user : forumlist.child("users").getChildren()) {
                            Log.d("Fix:IsUser?", user.toString());
                            DatabaseReference newUserForm = userForums.child(user.getKey()).child(forumlist.getKey());
                            copyData(forumlist.getRef(), newUserForm);


                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//copyData to copy a set of data from one node to another. Copies data and then deletes user node from the new copy
    private void copyData(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Log.d("Try:Error", firebaseError.toString());
                        } else {
                            System.out.println("Success");
                            toPath.child("users").removeValue();


                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Circular notification in the bottom navigation
    void setNotificationCircle() {
//        if(TotalEvents>UsersTotalEvents){
//            tabNotificationCircle[4].setVisibility(View.VISIBLE);
//        } else {
//            tabNotificationCircle[4].setVisibility(View.GONE);
//        }
//        if (TotalNumbers > UsersTotalNumbers) {
//            tabNotificationCircle[1].setVisibility(View.VISIBLE);
//        } else {
//            tabNotificationCircle[1].setVisibility(View.GONE);
//        }
//        if (TotalOffers > UsersTotalOffers) {
//            tabNotificationCircle[5].setVisibility(View.VISIBLE);
//        } else {
//            tabNotificationCircle[5].setVisibility(View.GONE);
//        }
//        if (TotalProducts > UsersTotalProducts) {
//            tabNotificationCircle[2].setVisibility(View.VISIBLE);
//        } else {
//            tabNotificationCircle[2].setVisibility(View.GONE);
//        }
//        if (TotalCabpools > UsersTotalCabpools) {
//            tabNotificationCircle[3].setVisibility(View.VISIBLE);
//        } else {
//            tabNotificationCircle[3].setVisibility(View.GONE);
//        }
    }

    public void setForumNotificationDot(){
        mUser = mAuth.getCurrentUser();
        DBHelper mydb = new DBHelper(getApplicationContext());
        final Map<String,Integer> allForumsSeenMessages = mydb.getAllForums();
//        forumsCategoriesRef.addValueEventListener(joinedForumsListener);
        Log.d("Forum",mUser.getUid());
        DatabaseReference userForum = FirebaseDatabase.getInstance().getReference().child("communities").child("testCollege").child("features").child("forums").child("userForums").child(mUser.getUid());
        userForum.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer totalUnreadMessages = 0;
                for(DataSnapshot forum: dataSnapshot.getChildren()){
                    try {
                        Integer totalMessages = Integer.getInteger(forum.child("totalMessages").toString());
                        String catUID = forum.child("catUID").toString();
                        Integer readMessages = allForumsSeenMessages.get(catUID);
                        if(totalMessages == null){
                            Log.d("totalMessages","null");


                        }
                    if(readMessages == null){
                        Log.d("readlMessages","null");

                    }

                        totalUnreadMessages = totalUnreadMessages+ totalMessages - readMessages;
                        if(totalUnreadMessages>0){
                            tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    catch(Exception e){
                        Log.d("ForumDot",e.toString());

                        continue;
                    }
                }
//                if(totalUnreadMessages<=0){
                    tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.GONE);


//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setTabListener() {


        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            int prePos;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                tab.getCustomView().setAlpha((float) 1);
                switch (pos) {
                    case 0: {
                        findViewById(R.id.fab_cat_infone).setVisibility(View.GONE);
                        setToolbarTitle(Title);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, recent).commit();
                        break;
                    }
                    case 1: {
                        findViewById(R.id.fab_cat_infone).setVisibility(View.GONE);
                        if (UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                            newUserVerificationAlert.buildAlertCheckNewUser(UserUtilities.currentUser.getUserType(), "Forums", HomeActivity.this);
                            tabs.getTabAt(prePos);
                        } else {
                            setActionBarTitle("Forums");
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();

                            counterItemFormat.setUserID(mAuth.getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_TAB_OPEN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);
                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, forums).commit();
                        }
                        break;
                    }
                    case 2: {
                        findViewById(R.id.fab_cat_infone).setVisibility(View.GONE);

                        if (UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                            newUserVerificationAlert.buildAlertCheckNewUser(UserUtilities.currentUser.getUserType(), "Add", HomeActivity.this);
                            tabs.getTabAt(prePos);
                        } else {
                            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                        }
                        break;
                    }
                    case 3: {
                        findViewById(R.id.fab_cat_infone).setVisibility(View.VISIBLE);

                        if (UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                            newUserVerificationAlert.buildAlertCheckNewUser(UserUtilities.currentUser.getUserType(), "Infone", HomeActivity.this);
                            tabs.getTabAt(prePos);
                        } else {
                            setActionBarTitle("Infone");
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();

                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_TAB_OPEN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);

                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, infone).commit();
                        }
                        break;
                    }
                    case 4: {
                        findViewById(R.id.fab_cat_infone).setVisibility(View.GONE);
                        //setActionBarTitle("You");
                        setActionBarTitle("Notifications");

                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta = new HashMap<>();
                        meta.put("type", "fromRecents");
                        //meta.put("userType","myProfile");
                        meta.put("userType", "notifications");
                        meta.put("userUID", FirebaseAuth.getInstance().getUid());

                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        //counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_OPEN);
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTIFICATIONS_OPEN);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        counterItemFormat.setMeta(meta);

                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, notifications).commit();
                        //getSupportFragmentManager().beginTransaction().replace(R.id.container, myProfile).commit();
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() != 4)
                    tab.getCustomView().setAlpha((float) .7);
                prePos = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                int pos = tab.getPosition();
                tab.getCustomView().setAlpha((float) 1);
                switch (pos) {
                    case 0:
                        recentsLinearLayoutManager.scrollToPositionWithOffset(0, 0);
                        break;
                    case 2: {
                        if (UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                            newUserVerificationAlert.buildAlertCheckNewUser(UserUtilities.currentUser.getUserType(), "Add", HomeActivity.this);
                            tabs.getTabAt(prePos);
                        } else {
                            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                        }
                        break;
                    }
                }

            }
        });
    }

    //Setting contents in the different tabs
    void tabs() {
        View vRecents = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        recentsT = tabs.newTab();

        tabTitle[0] = (TextView) vRecents.findViewById(R.id.tabTitle);
        tabTitle[0].setText("Recents");

        tabImage[0] = (SimpleDraweeView) vRecents.findViewById(R.id.tabImage);
        tabImage[0].setImageResource(R.drawable.baseline_home_white_36);

        tabNotificationCircle[0] = (ImageView) vRecents.findViewById(R.id.notification_circle);

        recentsT.setCustomView(vRecents);


        View vForums = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        forumsT = tabs.newTab();

        tabTitle[1] = (TextView) vForums.findViewById(R.id.tabTitle);
        tabTitle[1].setText("Forums");

        tabImage[1] = (SimpleDraweeView) vForums.findViewById(R.id.tabImage);
        tabImage[1].setImageResource(R.drawable.baseline_forum_white_36);

        tabNotificationCircle[1] = (ImageView) vForums.findViewById(R.id.notification_circle);
        vForums.setAlpha((float) 0.7);
        forumsT.setCustomView(vForums);

        View vAdd = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        addT = tabs.newTab();

//        tabTitle[2] = (TextView) vAdd.findViewById(R.id.tabTitle);
//        tabTitle[2].setText("Add");

        tabImage[2] = (SimpleDraweeView) vAdd.findViewById(R.id.tabImage);
        tabImage[2].setImageResource(R.drawable.outline_add_circle_outline_white_36);

        tabNotificationCircle[2] = (ImageView) vAdd.findViewById(R.id.notification_circle);
        vAdd.setAlpha((float) 0.7);
        addT.setCustomView(vAdd);

        View vInfone = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        infoneT = tabs.newTab();

        tabTitle[3] = (TextView) vInfone.findViewById(R.id.tabTitle);
        tabTitle[3].setText("Infone");

        tabImage[3] = (SimpleDraweeView) vInfone.findViewById(R.id.tabImage);
        tabImage[3].setImageResource(R.drawable.baseline_phone_white_36);

        tabNotificationCircle[3] = (ImageView) vInfone.findViewById(R.id.notification_circle);
        vInfone.setAlpha((float) 0.7);
        infoneT.setCustomView(vInfone);

        View vNotification = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        notificationsT = tabs.newTab();
        tabTitle[4] = (TextView) vNotification.findViewById(R.id.tabTitle);
        tabTitle[4].setText("Notifications");
        tabImage[4] = (SimpleDraweeView) vNotification.findViewById(R.id.tabImage);
        tabImage[4].setImageResource(R.drawable.baseline_notifications_white_18dp);

        tabNotificationCircle[4] = (ImageView) vNotification.findViewById(R.id.notification_circle);
        notificationsT.setCustomView(vNotification);

        //View vProfile = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        //profileT = tabs.newTab();

        //tabTitle[4] = (TextView) vProfile.findViewById(R.id.tabTitle);
        //tabTitle[4].setText("Profile");
        //tabImage[4] = (SimpleDraweeView) vProfile.findViewById(R.id.tabImage);
        //tabImage[4].setImageResource(R.drawable.ic_person_white_24dp);
        //tabImage[4].setImageResource(R.drawable.avatar_circle_36dp);

        //tabNotificationCircle[4] = (ImageView) vProfile.findViewById(R.id.notification_circle);
//        vProfile.setAlpha((float) 0.7);
        //profileT.setCustomView(vProfile);

        tabs.addTab(recentsT);
        tabs.addTab(forumsT);
        tabs.addTab(addT);
        tabs.addTab(infoneT);
        //tabs.addTab(profileT);
        tabs.addTab(notificationsT);
        setForumNotificationDot();
//        tabs.getTabAt(0)
    }


    //All ValueEventListener used in this class are defined here.
    private void initListeners() {

//        TotalStats = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child("TotalNumbers").getValue() != null)
//                    TotalNumbers = Integer.parseInt(dataSnapshot.child("TotalNumbers").getValue().toString());
//                if (dataSnapshot.child("TotalEvents").getValue() != null)
//                    TotalEvents = Integer.parseInt(dataSnapshot.child("TotalEvents").getValue().toString());
////                if (dataSnapshot.child("TotalOffers").getValue() != null)
////                    TotalOffers = Integer.parseInt(dataSnapshot.child("TotalOffers").getValue(String.class));
//                if (dataSnapshot.child("TotalProducts").getValue() != null)
//                    TotalProducts = Integer.parseInt(dataSnapshot.child("TotalProducts").getValue().toString());
//                if (dataSnapshot.child("TotalCabpools").getValue() != null)
//                    TotalCabpools = Integer.parseInt(dataSnapshot.child("TotalCabpools").getValue().toString());
//
////                setNotificationCircle();
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG, "onCancelled: ", databaseError.toException());
//            }
//        };
////
//        UserStats = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child("TotalNumbers").getValue() != null) {
//                    UsersTotalNumbers = Integer.parseInt(dataSnapshot.child("TotalNumbers").getValue().toString());
//                }
//                if (dataSnapshot.child("TotalEvents").getValue() != null) {
//                    UsersTotalEvents = Integer.parseInt(dataSnapshot.child("TotalEvents").getValue().toString());
//                }
////                if (dataSnapshot.child("TotalOffers").getValue() != null) {
////                    UsersTotalOffers = Integer.parseInt(dataSnapshot.child("TotalOffers").getValue(String.class));
////                }
//                if (dataSnapshot.child("TotalProducts").getValue() != null) {
//                    UsersTotalProducts = Integer.parseInt(dataSnapshot.child("TotalProducts").getValue().toString());
//                }
//                if (dataSnapshot.child("TotalCabpools").getValue() != null) {
//                    UsersTotalCabpools = Integer.parseInt(dataSnapshot.child("TotalCabpools").getValue().toString());
//                }
//
//                setNotificationCircle();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG, "onCancelled: ", databaseError.toException());
//            }
//        };

        popupsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> popUpUrl1 = new ArrayList<>();
                ArrayList<String> importance = new ArrayList<>();

                Boolean updateAvailable = false;

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                String first = preferences.getString("popup", "");
                boolean firstTimePopUp = Boolean.parseBoolean(first);

                int versionCode = BuildConfig.VERSION_CODE;

                Integer newVersion = dataSnapshot.child("update").child("versionCode").getValue(Integer.class);

                if (newVersion != null && newVersion > (versionCode)) {

                    String updateImageURL = dataSnapshot.child("update").child("imageUrl").getValue(String.class);

                    CustomDialogClass cdd = new CustomDialogClass(HomeActivity.this, updateImageURL, "UPDATE");

                    if (!cdd.isShowing()) {
                        cdd.show();
                    }
                    if (cdd.getWindow() == null)
                        return;

                    cdd.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
                    Window window = cdd.getWindow();
                    window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                    updateAvailable = true;
                }


                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    if (shot.child("imp").getValue(String.class) != null && shot.child("imageUrl").getValue(String.class) != null) {
                        popUpUrl1.add(shot.child("imageUrl").getValue(String.class));
                        importance.add(shot.child("imp").getValue(String.class));
                    }
                }

                for (int i = 0; i < popUpUrl1.size() && firstTimePopUp && !updateAvailable; i++) {

                    double random1 = Math.random();
                    int random = (int) (random1 * 10);
                    int importanceDigit = Integer.parseInt(importance.get(i));

                    boolean show = false;

                    if (importanceDigit == 3) {
                        if (random % 2 == 0)
                            show = true;
                    } else if (importanceDigit == 2) {
                        if (random % 3 == 0)
                            show = true;
                    } else if (importanceDigit == 1) {
                        if (random % 4 == 0)
                            show = true;
                    } else show = importanceDigit == 4;

                    if (!importance.get(i).equals("0") && show) {
                        CustomDialogClass cdd = new CustomDialogClass(HomeActivity.this, popUpUrl1.get(i));
                        cdd.show();
                        if (cdd.getWindow() == null)
                            return;
                        cdd.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
                        Window window = cdd.getWindow();
                        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("popup", "false");
                        editor.apply();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        editProfileValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("mobileNumber")) {
                    DatabaseReference referredUsersRef = FirebaseDatabase.getInstance().getReference().child("referredUsers");
                    referredUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(mUser.getUid())) {
                                Log.d("RRR", "TO EDIT PROFILE - IS REFERRED");
                                Intent i = new Intent(HomeActivity.this, EditProfileActivity.class);
                                i.putExtra("newUser", true);
                                i.putExtra("isReferred", true);
                                startActivity(i);
                            } else {
                                Log.d("RRR", "TO EDIT PROFILE - IS NOT REFERRED");
                                Intent i = new Intent(HomeActivity.this, EditProfileActivity.class);
                                i.putExtra("newUser", true);
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(mAuth.getCurrentUser().getUid());
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserUtilities.currentUser = dataSnapshot.getValue(UserItemFormat.class);

                            if (!dataSnapshot.hasChild("userType")) {
                                UserUtilities.currentUser.setUserType(UsersTypeUtilities.KEY_VERIFIED);
                            }

                            if (communityReference != null && !flag) {
                                recent = new Recents();
                                forums = new JoinedForums();
                                myProfile = new MyProfileFragment();
                                infone = new InfoneActivity();
                                notifications = new NotificationsFragment();

                                getSupportFragmentManager().beginTransaction().replace(R.id.container, recent).commit();

                                //tabImage[4].setImageURI(UserUtilities.currentUser.getImageURLThumbnail());
                                setTabListener();
                                flag = true;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        };

    }


    // must be launched from onStart()
    // else remove the eventListener in corresponding call.
    // i.e. if called from onCreate() make sure onDestroy() removes editProfileValueEventListener
    // from currentUserReference
    @SuppressLint("ApplySharedPref")
    private void launchRelevantActivitiesIfNeeded() {

        //Check authentication
        if (mUser == null) {
            editProfileItem.setVisible(false);
            editProfileItem.setEnabled(false);

            Log.d("RRRRR", "IS NOT INVITED");
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));

        } else if (getIntent().getBooleanExtra("isReferred", false) && mUser != null) {
            String sharedPrefKey = getResources().getString(R.string.referredAnonymousUser);
            SharedPreferences sharedPref = getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("referredUserID", mUser.getUid());
            editor.putString("referredBy", getIntent().getStringExtra("referredBy"));
            editor.commit();
            Log.d("RRRRR", "IS INVITED");
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.putExtra("isReferred", true);
            intent.putExtra("referredBy", getIntent().getStringExtra("referredBy"));
            startActivity(intent);
        } else if (getSharedPreferences(getResources().getString(R.string.referredAnonymousUser), Context.MODE_PRIVATE).getString("referredUserID", null) != null && mUser != null) {
            Log.d("RRRRR", "IS INVITED PLUS SHARED PREF");
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.putExtra("isReferred", true);
            intent.putExtra("referredBy", getSharedPreferences(getResources().getString(R.string.referredAnonymousUser), Context.MODE_PRIVATE).getString("referredBy", null));
            startActivity(intent);
        } else if (mUser != null) {

            FirebaseMessaging.getInstance().subscribeToTopic(mUser.getUid());

            username = mAuth.getCurrentUser().getDisplayName();
            userEmail = mAuth.getCurrentUser().getEmail();

            editProfileItem.setEnabled(true);
            editProfileItem.setVisible(true);

            navHeaderUserNameTv.setText(username != null ? username : "ZConnect");

            if (!defaultPrefs.getBoolean("isReturningUser", false)) {
                Intent tutIntent = new Intent(HomeActivity.this, TutorialActivity.class);
                tutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(tutIntent);
                //mark first time has run.
                SharedPreferences.Editor editor = defaultPrefs.edit();
                editor.putBoolean("isReturningUser", true);
                editor.commit();


            } else if (communityReference != null) {
                Log.d("RRRRR", "COMM REF NOT NULL");
                initialiseNotifications();

                communityFeaturesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("communityFeatures");

                nav_Menu = navigationView.getMenu();

                communityFeaturesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CommunityFeatures communityFeatures = dataSnapshot.getValue(CommunityFeatures.class);

                        try {

                            if (communityFeatures.getCabpool().equals("true")){
                                nav_Menu.findItem(R.id.MyRides).setVisible(true);
                            }else {
                                nav_Menu.findItem(R.id.MyRides).setVisible(false);
                            }

                            if (communityFeatures.getShops().equals("true")){
                                nav_Menu.findItem(R.id.MyOrders).setVisible(true);
                            }else {
                                nav_Menu.findItem(R.id.MyOrders).setVisible(false);
                            }

                            if (communityFeatures.getStoreroom().equals("true")){
                                nav_Menu.findItem(R.id.MyProducts).setVisible(true);
                            }else {
                                nav_Menu.findItem(R.id.MyProducts).setVisible(false);
                            }


                        }catch (Exception e){

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                FirebaseMessaging.getInstance().subscribeToTopic(communityReference);
                LocalDate dateTime = new LocalDate();

                communityInfoRef = FirebaseDatabase.getInstance().getReference().child("communitiesInfo").child(communityReference);

                communityInfoRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (setTitleFlag) {
                            if (dataSnapshot.hasChild("name")) {
                                Title = dataSnapshot.child("name").getValue().toString() + " Connect";
                                setToolbarTitle(Title);
                                UserUtilities.CommunityName = Title;

                                SharedPreferences sharedPref2 = getSharedPreferences("communityTitle", MODE_PRIVATE);
                                SharedPreferences.Editor editInfo2 = sharedPref2.edit();
                                editInfo2.putString("communityTitleValue", Title);
                                editInfo2.commit();

                            } else {
                                Title = "Community Connect";
                                setToolbarTitle(Title);

                                SharedPreferences sharedPref2 = getSharedPreferences("communityTitle", MODE_PRIVATE);
                                SharedPreferences.Editor editInfo2 = sharedPref2.edit();
                                editInfo2.putString("communityTitleValue", Title);
                                editInfo2.commit();
                            }
                            setTitleFlag = false;
                        }
                        navHeaderBackGroundImageUrl = dataSnapshot.child("image").getValue(String.class);
                        if (navHeaderBackGroundImageUrl != null
                                && URLUtil.isNetworkUrl(navHeaderBackGroundImageUrl)
                                && navHeaderBackground != null) {
                            navHeaderBackground.setImageURI(navHeaderBackGroundImageUrl);
                        } else {
                            navHeaderBackground.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                currentUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(mUser.getUid());
                currentUserReference.keepSynced(true);
                currentUserReference.addListenerForSingleValueEvent(editProfileValueEventListener);
            } else if (communityReference == null) {
                Log.d("RRRRR", "COMM REF IS NULL");
                Intent i = new Intent(this, CommunitiesAround.class);
                startActivity(i);
                finish();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    public void initialiseNotifications() {

        DatabaseReference databaseReference;
        String Uid;

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(Uid).child("NotificationChannels");


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    Setting data = dataSnapshot.getValue(Setting.class);

                    if (data.getAddCabPool()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD + communityReference);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD + communityReference);
                    }

                    if (data.getAddEvent()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD + communityReference);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD + communityReference);
                    }

                    if (data.getOffers()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_OFFERS_ADD + communityReference);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_OFFERS_ADD + communityReference);
                    }

                    if (data.getStoreRoom()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD + communityReference);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD + communityReference);
                    }

                    if (data.getAddForum()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD + communityReference);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD + communityReference);
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Navigation Bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.admins: {
                Intent intent = new Intent(getApplicationContext(), ViewAdmin.class);
                startActivity(intent);
                break;
            }
            case R.id.edit_profile: {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                intent.putExtra("newUser", false);
                startActivity(intent);
                break;
            }
            case R.id.MyProducts: {

                Intent MyProductsIntent = new Intent(HomeActivity.this, MyProducts.class);
                startActivity(MyProductsIntent);
                break;
            }

            case R.id.MyOrders: {

                Intent MyOrdersIntent = new Intent(HomeActivity.this, MyOrdersActivity.class);
                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta = new HashMap<>();
                meta.put("type", "fromNavigationDrawer");
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_SHOPS_MY_ORDERS_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);
                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();
                startActivity(MyOrdersIntent);
                break;
            }
            case R.id.MyRides: {

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta = new HashMap<>();
                meta.put("type", "fromNavigationDrawer");
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_MY_RIDES_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);
                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                Intent intent = new Intent(HomeActivity.this, MyRides.class);
                startActivity(intent);
                break;
            }
            case R.id.signOut: {
                if (!isNetworkAvailable(getApplicationContext())) {
                    Snackbar snack = Snackbar.make(navHeaderUserNameTv, "No Internet. Can't Log Out.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    snack.show();
                } else {
                    logoutAndSendToLogin();
                }
                break;
            }
            case R.id.Noti_Settings: {
                startActivity(new Intent(HomeActivity.this, NotificationSettings.class));
                break;
            }
            case R.id.about: {
                startActivity(new Intent(HomeActivity.this, AboutUs.class));
                break;
            }
            case R.id.bugReport: {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Send a bug report or a feedback to: \nzconnectinc@gmail.com");

                builder.setPositiveButton("Bug Report", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "zconnectinc@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report");
                        // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send uid..."));
                    }
                });
                builder.setNegativeButton("Feedback", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "zconnectinc@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send uid..."));
                    }
                });
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorHighlight));
                break;
            }
            case R.id.share: {
                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent, RequestCodes.REQUEST_INVITE);
                break;
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_TEXT, "Download the ZConnect app on \n"
//                        + url);
//                intent.setType("text/plain");
//                startActivity(Intent.createChooser(intent, "Share app url via ... "));
            }
            case R.id.referral_code:
                Intent intent = new Intent(this, ReferralCode.class);
                startActivity(intent);
                break;
            default: {
                return false;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mUser = mAuth.getCurrentUser();

        launchRelevantActivitiesIfNeeded();

        if (communityReference != null) {
            uiDbRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("ui");

            mDatabasePopUps = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("PopUps");
            mDatabasePopUps.keepSynced(true);

            mDatabasePopUps.addValueEventListener(popupsListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (communityReference != null) {
            try {
                mDatabasePopUps.removeEventListener(popupsListener);
                currentUserReference.removeEventListener(editProfileValueEventListener);
            } catch (Exception e) {
            }
            if (addContactDialog != null) addContactDialog.cancel();
        }
    }

    @Override
    protected void onPause() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("popup", "true");
        editor.apply();
        // onPause is always called before onStop,
        // which in turn is always called before onDestroy


        super.onPause();
    }

    private void logoutAndSendToLogin() {

        FirebaseMessaging.getInstance().unsubscribeFromTopic(mAuth.getCurrentUser().getUid());
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(communityReference);
        } catch (Exception e) {
        }
        mAuth.signOut();
        SharedPreferences preferences = getSharedPreferences("communityName", 0);
        preferences.edit().remove("communityReference").commit();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        if (!(getSupportFragmentManager().findFragmentById(R.id.container) instanceof Recents)) {
            tabs.getTabAt(0).select();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
//
//    @SuppressLint("ApplySharedPref")
//    private void promptToAddContact() {
//        final Long currTime = Calendar.getInstance().getTimeInMillis();
//        SharedPreferences addNumberDialogPref = getSharedPreferences("addNumberDialog", MODE_PRIVATE);
//        if (!addNumberDialogPref.contains("firstLaunch")) {
//            addNumberDialogPref.edit().putBoolean("firstLaunch", false).commit();
//            startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
//        } else {
//            Boolean neverAddNumber = addNumberDialogPref.getBoolean("never", false);
//            if (!neverAddNumber || (currTime - addNumberDialogPref.getLong("date", 0) > 2 * 24 * 3600 * 1000)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
//                addContactDialog = builder.setTitle("Hi " + username)
//                        .setMessage("Add your information and get discovered.")
//                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss(); // when user finishes the editProfile activity, he shouldn't see the dialog
//                                startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
//                            }
//                        }).setNegativeButton("Later", null)
//                        .setNeutralButton("Lite", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                SharedPreferences sharedPref = getSharedPreferences("addNumberDialog", MODE_PRIVATE);
//                                SharedPreferences.Editor editInfo = sharedPref.edit();
//                                editInfo.putBoolean("never", true);
//                                editInfo.putLong("date", currTime);
//                                editInfo.apply();
//                            }
//                        })
//                        .create();
//                addContactDialog.show();
//            }
//        }
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: error message is " + connectionResult.getErrorMessage());
    }

//    @Override
//    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//        mUser = mAuth.getCurrentUser();
//        username = null;
//        userEmail = null;
//
//        if (mUser != null) {
//            username = mUser.getDisplayName();
//            userEmail = mUser.getEmail();
//        }
//        guestMode = guestPrefs.getBoolean("mode", false);
//        updateViews();
//    }


//    public void setCommunity(String communityName){
//        SharedPreferences sharedPref2 = getSharedPreferences("communityName", MODE_PRIVATE);
//        SharedPreferences.Editor editInfo2 = sharedPref2.edit();
//        editInfo2.putString("communityReference", communityName);
//        editInfo2.commit();
//    }


    public void changeFragment(int i) {
        tabs.getTabAt(i).select();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == RequestCodes.REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
                Snackbar snackbar = Snackbar.make(navHeaderUserNameTv, "Invite send failed.", Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                snackbar.show();
            }
        }

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof Recents) {
            Recents recentsFragment = (Recents) fragment;
            recentsFragment.setOnHomeIconListener(this);
        }
        super.onAttachFragment(fragment);
    }

    @Override
    public void getLayoutManager(LinearLayoutManager linearLayoutManager) {
        recentsLinearLayoutManager = linearLayoutManager;
    }
}
