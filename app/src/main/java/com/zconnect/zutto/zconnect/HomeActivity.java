package com.zconnect.zutto.zconnect;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.newUserVerificationAlert;
import com.zconnect.zutto.zconnect.fragments.InfoneFragment;
import com.zconnect.zutto.zconnect.fragments.JoinedForums;
import com.zconnect.zutto.zconnect.fragments.MyProfileFragment;
import com.zconnect.zutto.zconnect.fragments.InAppNotificationsFragment;
import com.zconnect.zutto.zconnect.fragments.UpdateAppActivity;
import com.zconnect.zutto.zconnect.itemFormats.CommunityFeatures;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserSeenNotifItemFormat;
import com.zconnect.zutto.zconnect.pools.MyOrdersActivity;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.PermissionUtilities;
import com.zconnect.zutto.zconnect.utilities.RequestCodes;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;
import com.zconnect.zutto.zconnect.fragments.HomeBottomSheet;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;


import butterknife.BindView;
import butterknife.ButterKnife;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.OnViewInflateListener;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, Recents.OnHomeIconListener {

    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar_app_bar_home)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    public TabLayout tabs;
    String url = "https://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect";
    private boolean doubleBackToExitPressedOnce = false;
    private ValueEventListener editProfileValueEventListener;
    private ValueEventListener popupsListener;
    private ValueEventListener inAppNotificationCountListener;
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


    private DatabaseReference uiDbRef;
    private DatabaseReference inAppNotificationCountReference;
    private FirebaseUser mUser;
    private SharedPreferences defaultPrefs;
    private SharedPreferences guestPrefs;
    private AlertDialog addContactDialog;
    private Fragment recent, forums, shop, myProfile, notifications, active;
    public Fragment infone;
    private FragmentManager fm;
    LinearLayout recentView;

    public Boolean flag = false;
    public Boolean setTitleFlag = true;

    private DatabaseReference mDatabaseStats;
    private DatabaseReference mDatabaseUserStats;
    private String navHeaderBackGroundImageUrl = null;
    SimpleDraweeView navHeaderImage;
    private String navHeaderImageUrl;
    private String Title;
    Context context;

    private Boolean isFabOpen;
    TextView[] tabTitle = new TextView[6];
    SimpleDraweeView[] tabImage = new SimpleDraweeView[6];
    ImageView[] tabNotificationCircle = new ImageView[6];

    public TabLayout.Tab recentsT, forumsT, addT, infoneT, profileT, notificationsT;
    HomeBottomSheet bottomSheetFragment;
    private LinearLayoutManager recentsLinearLayoutManager;

    private PermissionUtilities permissionUtilities;
    private HomeActivity _this;

    public HomeActivity() {

        isFabOpen = false;
    }

    public void showAppTour(){

        recentView = findViewById(R.id.recentView);

        FancyShowCaseView welcome = new FancyShowCaseView.Builder(this)
                .backgroundColor(R.color.deeppurple700)
                .customView(R.layout.welcome_zconnect, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NotNull View view) {
                    }
                })
                .build();

        FancyShowCaseView home = new FancyShowCaseView.Builder(this)
                .backgroundColor(R.color.deeppurple700)
                .focusOn(tabImage[0])
                .fitSystemWindows(true)
                .customView(R.layout.app_tour_home, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NotNull View view) {
                     }
                })
                .build();



        FancyShowCaseView forums = new FancyShowCaseView.Builder(this)
                .backgroundColor(R.color.deeppurple700)
                .focusOn(tabImage[1])
                .fitSystemWindows(true)
                .customView(R.layout.app_tour_forums, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NotNull View view) {
                    }
                })
                .build();

        FancyShowCaseView add = new FancyShowCaseView.Builder(this)
                .backgroundColor(R.color.deeppurple700)
                .focusOn(tabImage[2])
                .fitSystemWindows(true)
                .customView(R.layout.app_tour_add, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NotNull View view) {
                    }
                })
                .build();

        FancyShowCaseView infone = new FancyShowCaseView.Builder(this)
                .backgroundColor(R.color.deeppurple700)
                .focusOn(tabImage[3])
                .fitSystemWindows(true)
                .customView(R.layout.app_tour_infone, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NotNull View view) {
                    }
                })
                .build();

        FancyShowCaseView notifications = new FancyShowCaseView.Builder(this)
                .backgroundColor(R.color.deeppurple700)
                .focusOn(tabImage[4])
                .fitSystemWindows(true)
                .customView(R.layout.app_tour_notifications, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NotNull View view) {
                    }
                })
                .build();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpi = (int)(metrics.density);
        int x = 50*dpi;
        int y = 160*dpi;
        int w = 1100*dpi;
        int h = 120*dpi;
        FancyShowCaseView features = new FancyShowCaseView.Builder(this)
                .backgroundColor(R.color.deeppurple700)
                .focusRectAtPosition(x, y, w, h)
                .fitSystemWindows(true)
                .customView(R.layout.app_tour_features, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NotNull View view) {
                    }
                })
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(15)
                .build();

        FancyShowCaseView end = new FancyShowCaseView.Builder(this)
                .backgroundColor(R.color.deeppurple700)
                .customView(R.layout.app_tour_end, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NotNull View view) {
                    }
                })
                .build();

        FancyShowCaseQueue queue = new FancyShowCaseQueue()
                .add(welcome)
                .add(home)
                .add(forums)
                .add(add)
                .add(infone)
                .add(notifications)
                .add(features)
                .add(end);

        queue.show();

    }

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        TODO check for loopholes in referral system

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        _this = this;


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
        findViewById(R.id.fab_cat_infone).setVisibility(View.GONE);




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

        initListeners();
        tabs();
    /////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////

//        Log.d("USEROBJECT",UserUtilities.currentUser.toString());


//        fixFirebaseUserForum();
//        testTheFix();
//        fixUpdateTotalJoinedForums();
//        setForumNotificationDot();
//        fixUserType();
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
    void fixUpdateTotalJoinedForums(){
        final DatabaseReference userForums = FirebaseDatabase.getInstance().getReference().child("communities").child("testCollege").child("features").child("forums").child("userForums");
        userForums.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot user : dataSnapshot.getChildren()){
                    long count = user.child("joinedForums").getChildrenCount();
                    userForums.child(user.getKey()).child("totalJoinedForums").setValue(count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error",databaseError.toString());
            }
        });

    }
    //fixUserType to go inside each user, set userType to verified
    void fixUserType(){
        final DatabaseReference userList = FirebaseDatabase.getInstance().getReference().child("communities").child("testCollege").child("Users1");
        Log.d("Fix","Staring ficUserType");
        int count = 0;
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot user: dataSnapshot.getChildren()){
                    userList.child(user.getKey()).child("userType").setValue("verified");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


////fixFirebaseUserForum to add respective forum details under each user. change "testCollege" to desired community/use loops
    void fixFirebaseUserForum() {

        final DatabaseReference userForums = FirebaseDatabase.getInstance().getReference().child("communities").child("testCollege").child("features").child("forums").child("userForums");
        Log.d("Fix", "Starting fixFirebaseUserForum");
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

                            DatabaseReference newUserForm = userForums.child(user.getKey()).child("joinedForums").child(forumlist.getKey());
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

//    public void setForumNotificationDot(){
//        mUser = mAuth.getCurrentUser();
//        DBHelper mydb = new DBHelper(getApplicationContext());
//        final Map<String,Integer> allForumsSeenMessages = mydb.getAllForums();
////        forumsCategoriesRef.addValueEventListener(joinedForumsListener);
//        Log.d("Forum",mUser.getUid());
//        DatabaseReference userForum = FirebaseDatabase.getInstance().getReference().child("communities").child("testCollege").child("features").child("forums").child("userForums").child(mUser.getUid());
//        userForum.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer totalUnreadMessages = 0;
//                for(DataSnapshot forum: dataSnapshot.getChildren()){
//                    try {
//                        Integer totalMessages = Integer.getInteger(forum.child("totalMessages").toString());
//                        String catUID = forum.child("catUID").toString();
//                        Integer readMessages = allForumsSeenMessages.get(catUID);
//                        if(totalMessages == null){
//                            Log.d("totalMessages","null");
//
//
//                        }
//                    if(readMessages == null){
//                        Log.d("readlMessages","null");
//
//                    }
//
//                        totalUnreadMessages = totalUnreadMessages+ totalMessages - readMessages;
//                        if(totalUnreadMessages>0){
//                            tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);
//                            break;
//                        }
//                    }
//                    catch(Exception e){
//                        Log.d("ForumDot",e.toString());
//
//                        continue;
//                    }
//                }
////                if(totalUnreadMessages<=0){
//                    tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.GONE);
//
//
////                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void setTabListener() {


        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            int prePos;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                switch (pos) {
                    case 0: {
                        findViewById(R.id.fab_cat_infone).setVisibility(View.GONE);
                        setToolbarTitle(Title);
                        setColour(R.color.black);
                        tabImage[0].setImageResource(R.drawable.ic_home_purple_24dp);
                        tabImage[1].setImageResource(R.drawable.ic_forum_outline_24dp);
                        tabImage[2].setImageResource(R.drawable.ic_control_point_outline_24dp);
                        tabImage[3].setImageResource(R.drawable.ic_phone_outline_24dp);
                        tabImage[4].setImageResource(R.drawable.ic_notifications_outline_24dp);

                        fm.beginTransaction().hide(active).show(recent).commit();
                        active = recent;
                        break;
                    }
                    case 1: {

                        tabImage[0].setImageResource(R.drawable.ic_home_outline_24dp);
                        tabImage[1].setImageResource(R.drawable.ic_forum_purple_24dp);
                        tabImage[2].setImageResource(R.drawable.ic_control_point_outline_24dp);
                        tabImage[3].setImageResource(R.drawable.ic_phone_outline_24dp);
                        tabImage[4].setImageResource(R.drawable.ic_notifications_outline_24dp);


                        findViewById(R.id.fab_cat_infone).setVisibility(View.GONE);
                        if (UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                            newUserVerificationAlert.buildAlertCheckNewUser(UserUtilities.currentUser.getUserType(), "Forums", HomeActivity.this);
                            tabs.getTabAt(prePos);
                        }else{
                            setActionBarTitle("Forums");
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta= new HashMap<>();

                            counterItemFormat.setUserID(mAuth.getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_TAB_OPEN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);
                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();

                            fm.beginTransaction().hide(active).show(forums).commit();
                            active = forums;
                        }
                        break;
                    }
                    case 2: {

                        tabImage[0].setImageResource(R.drawable.ic_home_outline_24dp);
                        tabImage[1].setImageResource(R.drawable.ic_forum_outline_24dp);
                        tabImage[2].setImageResource(R.drawable.ic_control_point_purple_24dp);
                        tabImage[3].setImageResource(R.drawable.ic_phone_outline_24dp);
                        tabImage[4].setImageResource(R.drawable.ic_notifications_outline_24dp);


                        findViewById(R.id.fab_cat_infone).setVisibility(View.GONE);

                        if (UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                            newUserVerificationAlert.buildAlertCheckNewUser(UserUtilities.currentUser.getUserType(), "Add", HomeActivity.this);
                            tabs.getTabAt(prePos);
                        }else {
                            permissionUtilities = new PermissionUtilities(_this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if(!permissionUtilities.isEnabled(PermissionUtilities.READ_EXTERNAL_STORAGE))
                                    permissionUtilities.request(permissionUtilities.READ_EXTERNAL_STORAGE);
                            }
                            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                        }
                        break;
                    }
                    case 3: {

                        tabImage[0].setImageResource(R.drawable.ic_home_outline_24dp);
                        tabImage[1].setImageResource(R.drawable.ic_forum_outline_24dp);
                        tabImage[2].setImageResource(R.drawable.ic_control_point_outline_24dp);
                        tabImage[3].setImageResource(R.drawable.ic_phone_purple_24dp);
                        tabImage[4].setImageResource(R.drawable.ic_notifications_outline_24dp);


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

                            fm.beginTransaction().hide(active).show(infone).commit();
                            active = infone;
                        }
                        break;
                    }
                    case 4: {

                        TabLayout tabs = findViewById(R.id.navigation);
                        tabs.getTabAt(4).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.GONE);


                        tabImage[0].setImageResource(R.drawable.ic_home_outline_24dp);
                        tabImage[1].setImageResource(R.drawable.ic_forum_outline_24dp);
                        tabImage[2].setImageResource(R.drawable.ic_control_point_outline_24dp);
                        tabImage[3].setImageResource(R.drawable.ic_phone_outline_24dp);
                        tabImage[4].setImageResource(R.drawable.ic_notifications_purple_24dp);

                        findViewById(R.id.fab_cat_infone).setVisibility(View.GONE);
                        //setActionBarTitle("You");
                        setActionBarTitle("Notifications");

                        inAppNotificationCountReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot notificationSnapshot) {
                                try {
                                    UserSeenNotifItemFormat temp = notificationSnapshot.getValue(UserSeenNotifItemFormat.class);
                                    inAppNotificationCountReference.child("seenNotifications").setValue(temp.getTotalNotifications());
                                }catch (Exception e){

                                    inAppNotificationCountReference.child("seenNotifications").setValue(0);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


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

                        fm.beginTransaction().hide(active).show(notifications).commit();
                        active = notifications;
                        notifications.onResume();
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                prePos = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                int pos = tab.getPosition();
                if(pos!=3) {
                    findViewById(R.id.fab_cat_infone).setVisibility(View.GONE);
                }
                else{
                    findViewById(R.id.fab_cat_infone).setVisibility(View.VISIBLE);

                }

                switch (pos) {
                    case 0:
                        recentsLinearLayoutManager.scrollToPositionWithOffset(0, 0);
                        break;
                    case 2: {
                        if (UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                            newUserVerificationAlert.buildAlertCheckNewUser(UserUtilities.currentUser.getUserType(), "Add", HomeActivity.this);
                            tabs.getTabAt(prePos);
                        } else {
                            permissionUtilities = new PermissionUtilities(_this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if(!permissionUtilities.isEnabled(PermissionUtilities.READ_EXTERNAL_STORAGE))
                                    permissionUtilities.request(permissionUtilities.READ_EXTERNAL_STORAGE);
                            }
                            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                        }
                        break;
                    }
                }

            }
        });

        if(getIntent().getIntExtra("tab",0) != 0){
            tabs.getTabAt(getIntent().getIntExtra("tab",0)).select();
//            fm = getSupportFragmentManager();
//            recent = new Recents();
//            forums = new JoinedForums();
//            active = recent;
//
//
//            fm.beginTransaction().hide(active).show(forums).commit();
//            active = forums;
        }
    }

    //Setting contents in the different tabs
    void tabs() {
        View vRecents = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        recentsT = tabs.newTab();

        tabTitle[0] = (TextView) vRecents.findViewById(R.id.tabTitle);
        tabTitle[0].setText("Recents");

        tabImage[0] = (SimpleDraweeView) vRecents.findViewById(R.id.tabImage);
        tabImage[0].setImageResource(R.drawable.ic_home_purple_24dp);

        tabNotificationCircle[0] = (ImageView) vRecents.findViewById(R.id.notification_circle);

        recentsT.setCustomView(vRecents);


        View vForums = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        forumsT = tabs.newTab();

        tabTitle[1] = (TextView) vForums.findViewById(R.id.tabTitle);
        tabTitle[1].setText("Forums");

        tabImage[1] = (SimpleDraweeView) vForums.findViewById(R.id.tabImage);
        tabImage[1].setImageResource(R.drawable.ic_forum_outline_24dp);
        tabNotificationCircle[1] = (ImageView) vForums.findViewById(R.id.notification_circle);

        forumsT.setCustomView(vForums);

        View vAdd = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        addT = tabs.newTab();

//        tabTitle[2] = (TextView) vAdd.findViewById(R.id.tabTitle);
//        tabTitle[2].setText("Add");

        tabImage[2] = (SimpleDraweeView) vAdd.findViewById(R.id.tabImage);
        tabImage[2].setImageResource(R.drawable.ic_control_point_outline_24dp);
        tabNotificationCircle[2] = (ImageView) vAdd.findViewById(R.id.notification_circle);

        addT.setCustomView(vAdd);

        View vInfone = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        infoneT = tabs.newTab();

        tabTitle[3] = (TextView) vInfone.findViewById(R.id.tabTitle);
        tabTitle[3].setText("Infone");

        tabImage[3] = (SimpleDraweeView) vInfone.findViewById(R.id.tabImage);
        tabImage[3].setImageResource(R.drawable.ic_phone_outline_24dp);

        tabNotificationCircle[3] = (ImageView) vInfone.findViewById(R.id.notification_circle);

        infoneT.setCustomView(vInfone);

        View vNotification = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        notificationsT = tabs.newTab();
        tabTitle[4] = (TextView) vNotification.findViewById(R.id.tabTitle);
        tabTitle[4].setText("Notifications");
        tabImage[4] = (SimpleDraweeView) vNotification.findViewById(R.id.tabImage);
        tabImage[4].setImageResource(R.drawable.ic_notifications_outline_24dp);


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
//        setForumNotificationDot();
//        tabs.getTabAt(0)
    }


    //All ValueEventListener used in this class are defined here.
    private void initListeners() {

        inAppNotificationCountListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    UserSeenNotifItemFormat temp = dataSnapshot.getValue(UserSeenNotifItemFormat.class);
                    Log.d("testing",dataSnapshot.toString() + "  "+  temp.getTotalNotifications() + " ");

                    if (temp.getSeenNotifications()<temp.getTotalNotifications()) {
                        TabLayout tabs = findViewById(R.id.navigation);
                        tabs.getTabAt(4).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);

                        Log.d("testing","1");
                    } else {
                        TabLayout tabs = findViewById(R.id.navigation);
                        tabs.getTabAt(4).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.GONE);

                        Log.d("testing","2");
                    }


                }catch (Exception e){

                    Log.d("testing","3");
                    TabLayout tabs = findViewById(R.id.navigation);
                    tabs.getTabAt(4).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

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

                if(!dataSnapshot.hasChild("mobileNumber")){
                    DatabaseReference referredUsersRef = FirebaseDatabase.getInstance().getReference().child("referredUsers");
                    referredUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mUser.getUid()))
                            {
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
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserUtilities.currentUser = dataSnapshot.getValue(UserItemFormat.class);

                            try {

                                navHeaderImageUrl = dataSnapshot.child("imageURL").getValue(String.class);
                                String _username = dataSnapshot.getValue(UserItemFormat.class).getUsername();
                                if (_username != null || !_username.isEmpty())
                                    navHeaderUserNameTv.setText(dataSnapshot.getValue(UserItemFormat.class).getUsername());
                                else
                                    navHeaderUserNameTv.setText("ZConnect");
                                if (navHeaderImageUrl != null) {
                                    navHeaderImage = findViewById(R.id.iv_z_connect_logo_nav_header1);
                                    try {
                                        navHeaderImage.setImageURI(Uri.parse(navHeaderImageUrl));
                                    } catch (Exception e) {

                                    }
                                }

                            }catch (Exception e){}

                            if (!dataSnapshot.hasChild("userType")) {
                                UserUtilities.currentUser.setUserType(UsersTypeUtilities.KEY_VERIFIED);
                            }

                            if (communityReference != null && !flag) {
                                recent = new Recents();
                                forums = new JoinedForums();
                                myProfile = new MyProfileFragment();
                                infone = new InfoneFragment();
                                notifications = new InAppNotificationsFragment();

                                fm = getSupportFragmentManager();
                                active = recent;


                                fm.beginTransaction().add(R.id.container, notifications, "4").hide(notifications).commit();
                                fm.beginTransaction().add(R.id.container, infone, "3").hide(infone).commit();
                                fm.beginTransaction().add(R.id.container, forums, "2").hide(forums).commit();
                                fm.beginTransaction().add(R.id.container,recent, "1").commit();


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

            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("global").getValue(Integer.class)>BuildConfig.VERSION_CODE){
                        Intent intent = new Intent(HomeActivity.this, UpdateAppActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            FirebaseMessaging.getInstance().subscribeToTopic(mUser.getUid());

            username = mAuth.getCurrentUser().getDisplayName();
            userEmail = mAuth.getCurrentUser().getEmail();

            editProfileItem.setEnabled(true);
            editProfileItem.setVisible(true);

//            navHeaderUserNameTv.setText(username != null ? username : "ZConnect");

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
                            if (communityFeatures.getInternships().equals("true")){
                                nav_Menu.findItem(R.id.MyInternships).setVisible(true);
                            }else {
                                nav_Menu.findItem(R.id.MyInternships).setVisible(false);
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

                inAppNotificationCountReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(mUser.getUid()).child("notificationStatus");
                inAppNotificationCountReference.addValueEventListener(inAppNotificationCountListener);
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
            case R.id.MyInternships: {
                Intent intent = new Intent(getApplicationContext(), MyInternships.class);
                startActivity(intent);
                CounterItemFormat counterItemFormat = new CounterItemFormat();
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_INTERNSHIPS_MY_INTERNSHIPS_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();
                break;
            }
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
                FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                        child("cabpool").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                    Intent intent = new Intent(HomeActivity.this, UpdateAppActivity.class);
                                    intent.putExtra("feature", "shops");
                                    startActivity(intent);

                                } else {

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
                                }
                            }
                        });
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

                Dialog bugReportDialog = new Dialog(HomeActivity.this);
                bugReportDialog.setContentView(R.layout.new_dialog_box);
                bugReportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                bugReportDialog.findViewById(R.id.dialog_box_image_sdv).setBackground(ContextCompat.getDrawable(HomeActivity.this,R.drawable.ic_message_white_24dp));
                TextView heading =  bugReportDialog.findViewById(R.id.dialog_box_heading);
                heading.setText("Report/Feedback");
                TextView body = bugReportDialog.findViewById(R.id.dialog_box_body);
                body.setText("Send a bug report or a feedback to: \nzconnectinc@gmail.com");
                Button positiveButton = bugReportDialog.findViewById(R.id.dialog_box_positive_button);
                positiveButton.setText("Bug Report");
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "zconnectinc@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report");
                        // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send uid..."));
                        bugReportDialog.dismiss();

                    }
                });
                Button negativeButton = bugReportDialog.findViewById(R.id.dialog_box_negative_button);
                negativeButton.setText("Feedback");
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "zconnectinc@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send uid..."));
                        bugReportDialog.dismiss();
                    }
                });

                bugReportDialog.show();




                break;
            }
            case R.id.referral_code:
                Intent intent = new Intent(this, ReferralCode.class);
                startActivity(intent);
                break;
            default: {
                return false;
            }

            case R.id.app_tour_btn:
                drawer.closeDrawer(GravityCompat.START);
               tabs.getTabAt(0).select();
                showAppTour();
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

        if (active!=recent)
        {
            setToolbarTitle(Title);
            tabs.getTabAt(0).select();
            fm.beginTransaction().hide(active).show(recent).commit();
            active = recent;}

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
                    public void run() { doubleBackToExitPressedOnce = false;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtilities.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
