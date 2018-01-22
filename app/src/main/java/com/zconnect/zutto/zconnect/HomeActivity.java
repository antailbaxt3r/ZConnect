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
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener, SharedPreferences.OnSharedPreferenceChangeListener {

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
    private ValueEventListener phoneBookValueEventListener;
    private ValueEventListener popupsListener;
    /**
     * Listenes to /ui node in firebase.
     */
    private ValueEventListener uiDbListener;
    /**
     * The user name displayed in nav header.
     */
    private TextView navHeaderUserNameTv;
    /**
     * The email displayed in nav header.
     */
    private TextView navHeaderEmailTv;
    /**
     * Background of nav header.
     <activity
     android:name=".FullscreenActivity"
     android:configChanges="orientation|keyboardHidden|screenSize"
     android:label="@string/title_activity_fullscreen"
     android:screenOrientation="portrait"
     android:theme="@style/AppTheme.NoActionBar" />
     <activity
     android:name=".MyRides"
     android:label="@string/title_activity_my_rides"
     android:parentActivityName=".HomeActivity"
     android:theme="@style/AppTheme.NoActionBar" />

     <service android:name=".NotificationService">
     <intent-filter>
     <action android:name="com.google.firebase.MESSAGING_EVENT" />
     </inte
     */
    private SimpleDraweeView navHeaderBackground;
    private MenuItem editProfileItem;
    private ActionBarDrawerToggle toggle;
    private String userEmail;
    private String username;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference phoneBookDbRef;
    private DatabaseReference mDatabasePopUps;

    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2,fab3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    /**
     * /ui node
     */
    private DatabaseReference uiDbRef;
    private FirebaseUser mUser;
    private boolean guestMode;
    private SharedPreferences defaultPrefs;
    private SharedPreferences guestPrefs;
    private AlertDialog addContactDialog;
    private Fragment recent, cab, infone, store, shop, events;
    private DatabaseReference mDatabaseStats;
    private DatabaseReference mDatabaseUserStats;
    int UsersTotalNumbers = 0, TotalNumbers = 0;
    int UsersTotalProducts = 0, TotalProducts = 0;
    int UsersTotalOffers = 0, TotalOffers = 0;
    int UsersTotalEvents = 0, TotalEvents = 0;
    private ValueEventListener UserStats;
    private ValueEventListener TotalStats;

    TextView[] tabTitle= new TextView[6];
    ImageView[] tabImage = new ImageView[6];
    ImageView[] tabNotificationCircle = new ImageView[6];



    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        recent = new Recents();
        cab = new CabPoolMain();
        infone = new InfoneActivity();
        store = new TabStoreRoom();
        shop = new Shop();
        events = new TabbedEvents();
        tabs();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, recent).commit();
        uiDbRef = FirebaseDatabase.getInstance().getReference("ui");
        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        guestPrefs = getSharedPreferences("guestMode", MODE_PRIVATE);
        guestPrefs.registerOnSharedPreferenceChangeListener(this);
        guestMode = guestPrefs.getBoolean("mode", false);
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
        phoneBookDbRef = FirebaseDatabase.getInstance().getReference().child("Phonebook");
        phoneBookDbRef.keepSynced(true);

        View navHeader = navigationView.getHeaderView(0);
        //These initializations **can't** be done by glide
        navHeaderUserNameTv = (TextView) navHeader.findViewById(R.id.tv_name_nav_header);
        navHeaderEmailTv = (TextView) navHeader.findViewById(R.id.tv_email_nav_header);
        navHeaderBackground = (SimpleDraweeView) navHeader.findViewById(R.id.iv_nav_header_background);
        //necessary for icons to retain their color
        navigationView.setItemIconTintList(null);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        initListeners();
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.black);
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        //Floating Buttons
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);
                int i=tabs.getSelectedTabPosition();
                if(i==0){//Recents
                if (!status) {
                    animateFAB();
                }else {
                    alertBox();
                    }
                }
                if(i==1){//Infone
                    if (!status) {
                        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                        startActivity(intent);

                    }else {
                        alertBox();
                    }
                }
                if(i==2){//Storeroom
                    if (!status) {
                        CounterManager.StoreRoomFABclick();
                        Intent intent = new Intent(getApplicationContext(), AddProduct.class);
                        startActivity(intent);
                    }else {
                        alertBox();
                    }
                }
                if(i==3){//Events
                    if (!status) {
                        CounterManager.eventAddClick();
                        Intent intent = new Intent(getApplicationContext(), AddEvent.class);
                        startActivity(intent);   }else {
                        alertBox();
                    }
                }
                if(i==4){//CabPool
                    if (!status) {
                            setActionBarTitle("Search Pool");
                            CounterManager.RecentsOpen();
                            Intent intent = new Intent(HomeActivity.this, CabPooling.class);
                            startActivity(intent);
                       }else {
                        alertBox();
                    }
                }
                if(i==5){//Shops
                    if (!status) {
                        CounterManager.shopOffers();
                        Intent intent = new Intent(HomeActivity.this, Offers.class);
                        startActivity(intent);
                    }else {
                        alertBox();
                    }
                }

            }

        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);
                if (!status) {
                    CounterManager.StoreRoomFABclick();
                    Intent intent = new Intent(getApplicationContext(), AddProduct.class);
                    startActivity(intent);
                }
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);
                if (!status) {
                    CounterManager.eventAddClick();
                    Intent intent = new Intent(getApplicationContext(), AddEvent.class);
                    startActivity(intent);
                }
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);
                if (!status) {
                    setActionBarTitle("Search Pool");
                    CounterManager.RecentsOpen();
                    Intent intent = new Intent(getApplicationContext(), CabPooling.class);
                    startActivity(intent);
                }
            }
        });



        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        editProfileItem = navigationView.getMenu().findItem(R.id.edit_profile);


        FirebaseMessaging.getInstance().subscribeToTopic("ZCM");

        mDatabasePopUps = FirebaseDatabase.getInstance().getReference().child("PopUps");
        mDatabasePopUps.keepSynced(true);

        if(mAuth.getCurrentUser()!=null) {
            mDatabaseUserStats = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Stats");
            mDatabaseStats = FirebaseDatabase.getInstance().getReference().child("Stats");
            mDatabaseStats.addValueEventListener(TotalStats);
            mDatabaseUserStats.addValueEventListener(UserStats);
//            Toast.makeText(this, "Not null", Toast.LENGTH_SHORT).show();
        }


    }

    void alertBox(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Please Log In to access this feature.")
                .setTitle("Dear Guest!");

        builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("Lite :P", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
            Log.d("Raj", "open");

        }
    }


    void setNotificationCircle(){
        if(TotalEvents>UsersTotalEvents){
            tabNotificationCircle[4].setVisibility(View.VISIBLE);
        }else{
            tabNotificationCircle[4].setVisibility(View.GONE);
        }
        if (TotalNumbers>UsersTotalNumbers){
            tabNotificationCircle[1].setVisibility(View.VISIBLE);
        }else{
            tabNotificationCircle[1].setVisibility(View.GONE);
        }
        if(TotalOffers>UsersTotalOffers){
            tabNotificationCircle[5].setVisibility(View.VISIBLE);
        }else{
            tabNotificationCircle[5].setVisibility(View.GONE);
        }
        if(TotalProducts>UsersTotalProducts){
            tabNotificationCircle[2].setVisibility(View.VISIBLE);
        }else{
            tabNotificationCircle[2].setVisibility(View.GONE);
        }
    }

    void tabs() {

        View vRecents = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        TabLayout.Tab recentsT = tabs.newTab();

        tabTitle[0] = (TextView) vRecents.findViewById(R.id.tabTitle);
        tabTitle[0].setText("Recents");

        tabImage[0] = (ImageView) vRecents.findViewById(R.id.tabImage);
        tabImage[0].setImageResource(R.drawable.ic_home_white_24dp);

        tabNotificationCircle[0] = (ImageView) vRecents.findViewById(R.id.notification_circle);

        recentsT.setCustomView(vRecents);




        View vInfone = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        TabLayout.Tab infoneT = tabs.newTab();

        tabTitle[1] = (TextView) vInfone.findViewById(R.id.tabTitle);
        tabTitle[1].setText("Infone");

        tabImage[1] = (ImageView) vInfone.findViewById(R.id.tabImage);
        tabImage[1].setImageResource(R.drawable.ic_people_white_24dp);

        tabNotificationCircle[1] = (ImageView) vInfone.findViewById(R.id.notification_circle);
        vInfone.setAlpha((float) 0.7);
        infoneT.setCustomView(vInfone);


        View vStore = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        TabLayout.Tab storeT = tabs.newTab();

        tabTitle[2] = (TextView) vStore.findViewById(R.id.tabTitle);
        tabTitle[2].setText("StoreRoom");

        tabImage[2] = (ImageView) vStore.findViewById(R.id.tabImage);
        tabImage[2].setImageResource(R.drawable.ic_local_mall_white_24dp);

        tabNotificationCircle[2] = (ImageView) vStore.findViewById(R.id.notification_circle);
        vStore.setAlpha((float) 0.7);
        storeT.setCustomView(vStore);



        View vCab = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        TabLayout.Tab cabT = tabs.newTab();

        tabTitle[3] = (TextView) vCab.findViewById(R.id.tabTitle);
        tabTitle[3].setText("CabPool");

        tabImage[3] = (ImageView) vCab.findViewById(R.id.tabImage);
        tabImage[3].setImageResource(R.drawable.ic_local_taxi_white_24dp);

        tabNotificationCircle[3] = (ImageView) vCab.findViewById(R.id.notification_circle);
        vCab.setAlpha((float) 0.7);
        cabT.setCustomView(vCab);




        View vEvents = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        TabLayout.Tab eventT = tabs.newTab();

        tabTitle[4] = (TextView) vEvents.findViewById(R.id.tabTitle);
        tabTitle[4].setText("Events");

        tabImage[4] = (ImageView) vEvents.findViewById(R.id.tabImage);
        tabImage[4].setImageResource(R.drawable.ic_event_white_24dp);

        tabNotificationCircle[4] = (ImageView) vEvents.findViewById(R.id.notification_circle);
        vEvents.setAlpha((float) 0.7);
        eventT.setCustomView(vEvents);




        View vShop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
        TabLayout.Tab shopT = tabs.newTab();

        tabTitle[5] = (TextView) vShop.findViewById(R.id.tabTitle);
        tabTitle[5].setText("Shops");

        tabImage[5] = (ImageView) vShop.findViewById(R.id.tabImage);
        tabImage[5].setImageResource(R.drawable.ic_store_white_24dp);

        tabNotificationCircle[5] = (ImageView) vShop.findViewById(R.id.notification_circle);
        vShop.setAlpha((float) 0.7);
        shopT.setCustomView(vShop);


        tabs.addTab(recentsT);
        tabs.addTab(infoneT);
        tabs.addTab(storeT);
        tabs.addTab(eventT);
        tabs.addTab(cabT);
        tabs.addTab(shopT);


        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                tab.getCustomView().setAlpha((float) 1);
                switch (pos) {
                    case 0: {
                        setActionBarTitle("BITS Connect");
                        CounterManager.RecentsOpen();
                        fab.setImageResource(R.drawable.ic_add_white_36dp);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, recent).commit();
                        break;
                    }
                    case 1: {
                        setActionBarTitle("Infone");
                        CounterManager.InfoneOpen();
                        fab.setImageResource(R.drawable.ic_edit_white_24dp);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, infone).commit();
                        break;
                    }
                    case 5: {
                        setActionBarTitle("Shops");
                        fab.setImageResource(R.drawable.procent_badge_256);
                        CounterManager.ShopOpen();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, shop).commit();

                        break;
                    }
                    case 2: {
                        setActionBarTitle("StoreRoom");
                        CounterManager.StoreRoomOpen();
                        fab.setImageResource(R.drawable.ic_add_shopping_cart_white_24dp);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, store).commit();
                        break;
                    }
                    case 3: {
                        setActionBarTitle("Events");
                        CounterManager.EventOpen();
                        fab.setImageResource(R.drawable.ic_add_white_36dp);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, events).commit();
                        break;
                    }
                    case 4: {
                        setActionBarTitle("Cab Pool");
                        CounterManager.openCabPool();
                        fab.setImageResource(R.drawable.ic_search_white_24dp);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, cab).commit();
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().setAlpha((float) .7);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    /**
     *
     *
     * All {@link ValueEventListener}s used in this class are defined here.
     */
    private void initListeners() {

        TotalStats = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TotalNumbers = Integer.parseInt(dataSnapshot.child("TotalNumbers").getValue().toString());
                TotalEvents = Integer.parseInt(dataSnapshot.child("TotalEvents").getValue().toString());
                TotalOffers = Integer.parseInt(dataSnapshot.child("TotalOffers").getValue().toString());
                TotalProducts = Integer.parseInt(dataSnapshot.child("TotalProducts").getValue().toString());

//                setNotificationCircle();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }


        };
        UserStats = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("TotalNumbers").getValue() != null) {
                    UsersTotalNumbers = Integer.parseInt(dataSnapshot.child("TotalNumbers").getValue().toString());
                }
                if (dataSnapshot.child("TotalEvents").getValue() != null) {
                    UsersTotalEvents = Integer.parseInt(dataSnapshot.child("TotalEvents").getValue().toString());
                }
                if (dataSnapshot.child("TotalOffers").getValue() != null) {
                    UsersTotalOffers = Integer.parseInt(dataSnapshot.child("TotalOffers").getValue().toString());
                }
                if (dataSnapshot.child("TotalProducts").getValue() != null) {
                    UsersTotalProducts = Integer.parseInt(dataSnapshot.child("TotalProducts").getValue().toString());
                }

                setNotificationCircle();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        };


        popupsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> popUpUrl1 = new ArrayList<>();
                ArrayList<String> importance = new ArrayList<>();
                boolean dataComplete = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                String first = preferences.getString("popup", "");
                boolean firstTimePopUp = Boolean.parseBoolean(first);
                boolean updateAvailable=true;

                int versionCode = BuildConfig.VERSION_CODE;

                Integer newVersion = dataSnapshot.child("update").child("versionCode").getValue(Integer.class);

                if (newVersion != null && newVersion > (versionCode)) {

                    updateAvailable=false;
                    String updateImageURL = dataSnapshot.child("update").child("imageUrl").getValue(String.class);

                    CustomDialogClass cdd = new CustomDialogClass(HomeActivity.this, updateImageURL, "UPDATE");
                    cdd.show();
                    if (cdd.getWindow() == null)
                        return;
                    cdd.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
                    Window window = cdd.getWindow();
                    window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                }
                else {
                    updateAvailable=false;
                }


                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    if (shot.child("imp").getValue(String.class) != null && shot.child("imageUrl").getValue(String.class) != null) {
                        popUpUrl1.add(shot.child("imageUrl").getValue(String.class));
                        importance.add(shot.child("imp").getValue(String.class));
                        dataComplete = true;
                    } else if(!shot.getKey().equalsIgnoreCase("update")){
                        dataComplete = false;
                    }
                }
                for (int i = 0; i < popUpUrl1.size() && dataComplete && firstTimePopUp; i++) {
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
        phoneBookValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userAddedToInfone = false;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (TextUtils.equals(child.child("email").getValue(String.class), userEmail)) {
                        username = child.child("name").getValue(String.class);
                        navHeaderUserNameTv.setText(username);
                        userAddedToInfone = true;
                    }
                }
                if (!userAddedToInfone) promptToAddContact();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        };

        uiDbListener = new ValueEventListener() {

            /**
             * Updates nav header background and nav header text colors according to firebase.
             */
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final DataSnapshot navDrawerNode = dataSnapshot.child("navDrawer");
                final String textColorString = navDrawerNode.child("headerTextColor").getValue(String.class);
                final String navHeaderBackGroundImageUrl;
                try {
                    if (textColorString != null && textColorString.length() > 1) {
                        @ColorInt
                        final int textColor = Color.parseColor(textColorString);
                        if (navHeaderUserNameTv != null) navHeaderUserNameTv.setTextColor(textColor);
                        if (navHeaderEmailTv != null) navHeaderEmailTv.setTextColor(textColor);
                    }
                    navHeaderBackGroundImageUrl = navDrawerNode.child("headerBackground").getValue(String.class);
                    if (navHeaderBackGroundImageUrl != null
                            && URLUtil.isNetworkUrl(navHeaderBackGroundImageUrl)
                            && navHeaderBackground != null) {
                        navHeaderBackground.setImageURI(navHeaderBackGroundImageUrl);
                    }
                } catch (final DatabaseException e) {
                    // caused when data in db is of other type than accessed
                    Log.e(TAG, "onDataChange: ", e.fillInStackTrace());
                } catch (final IllegalArgumentException e) {
                    // caused when Color.parseColor is provided incorrect string
                    Log.e(TAG, "onDataChange: illegal color in /ui/navHeader/headerTextColor", e.fillInStackTrace());
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(TAG, "onCancelled:", databaseError.toException());
            }
        };
    }

    private void updateViews() {
        navHeaderUserNameTv.setText(username != null ? username : "ZConnect");
        navHeaderEmailTv.setText(userEmail != null ? userEmail : "The way to connect!");
        if (guestMode) {
            editProfileItem.setVisible(false);
            editProfileItem.setEnabled(false);
        } else {
            editProfileItem.setEnabled(true);
            editProfileItem.setVisible(true);
        }
    }

    // must be launched from onStart()
    // else remove the eventListener in corresponding call.
    // i.e. if called from onCreate() make sure onDestroy() removes phoneBookValueEventListener
    // from phoneBookDbRef
    @SuppressLint("ApplySharedPref")
    private void launchRelevantActivitiesIfNeeded() {
        //show tuts for first launch
        if (!defaultPrefs.getBoolean("isReturningUser", false)) {
            Intent tutIntent = new Intent(HomeActivity.this, FullscreenActivity.class);
            tutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(tutIntent);

            //mark first time has run.
            SharedPreferences.Editor editor = defaultPrefs.edit();
            editor.putBoolean("isReturningUser", true);
            editor.commit();
        } else /*check if login is needed*/ if (!guestMode && mUser == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        } else if (!guestMode) {
            phoneBookDbRef.addListenerForSingleValueEvent(phoneBookValueEventListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile: {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.messages: {
                tabs.getTabAt(1).select();
                break;
            }
            case R.id.Shop: {
                tabs.getTabAt(5).select();
                break;
            }
            case R.id.MyProducts: {
                tabs.getTabAt(2).select();
                break;
            }
            case R.id.Timeline: {
                tabs.getTabAt(3).select();
                break;
            }
            case R.id.MyRides: {
                Intent intent=new Intent(HomeActivity.this, MyRides.class);
                startActivity(intent);
                break;
            }
            case R.id.signOut: {
                if (!isNetworkAvailable(getApplicationContext())) {
                    Snackbar snack = Snackbar.make(navHeaderUserNameTv, "No Internet. Can't Log Out.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                    snack.show();
                } else {
                    logoutAndSendToLogin();
                }
                break;
            }
            case R.id.ad: {
                CounterManager.AdvertisementOpen();
                startActivity(new Intent(HomeActivity.this, Advertisement.class));
                break;
            }
            case R.id.about: {
                startActivity(new Intent(HomeActivity.this, AboutUs.class));
                break;
            }
            case R.id.mapActivity: {
                CounterManager.MapOpen();
                startActivity(new Intent(this, Campus_Map.class));
                break;
            }
            case R.id.bugReport: {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Send a Bug Report or a Feedback to \nzconnectinc@gmail.com")
                        .setTitle("Alert");

                builder.setPositiveButton("Bug Report", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "zconnectinc@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report");
                        // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
                builder.setNegativeButton("Feedback", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "zconnectinc@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case R.id.share: {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Download the ZConnect app on \n"
                        + url);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share app url via ... "));
            }
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
        launchRelevantActivitiesIfNeeded();
        mDatabasePopUps.addValueEventListener(popupsListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
        uiDbRef.addValueEventListener(uiDbListener);
    }

    @Override
    protected void onStop() {
        mDatabasePopUps.removeEventListener(popupsListener);
        phoneBookDbRef.removeEventListener(phoneBookValueEventListener);
        if (addContactDialog != null) addContactDialog.cancel();
        super.onStop();
    }

    @Override
    protected void onPause() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("popup", "true");
        editor.apply();
        // onPause is always called before onStop,
        // which in turn is always called before onDestroy

        uiDbRef.removeEventListener(uiDbListener);

        super.onPause();
    }

    private void logoutAndSendToLogin() {
        mAuth.signOut();
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

    @SuppressLint("ApplySharedPref")
    private void promptToAddContact() {
        final Long currTime = Calendar.getInstance().getTimeInMillis();
        SharedPreferences addNumberDialogPref = getSharedPreferences("addNumberDialog", MODE_PRIVATE);
        if (!addNumberDialogPref.contains("firstLaunch")) {
            addNumberDialogPref.edit().putBoolean("firstLaunch", false).commit();
            startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
        } else {
            Boolean neverAddNumber = addNumberDialogPref.getBoolean("never", false);
            if (!neverAddNumber || (currTime - addNumberDialogPref.getLong("date", 0) > 2 * 24 * 3600 * 1000)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                addContactDialog = builder.setTitle("Hi " + username)
                        .setMessage("Add your information and get discovered.")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // when user finishes the editProfile activity, he shouldn't see the dialog
                                startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
                            }
                        }).setNegativeButton("Later", null)
                        .setNeutralButton("Lite", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPref = getSharedPreferences("addNumberDialog", MODE_PRIVATE);
                                SharedPreferences.Editor editInfo = sharedPref.edit();
                                editInfo.putBoolean("never", true);
                                editInfo.putLong("date", currTime);
                                editInfo.apply();
                            }
                        })
                        .create();
                addContactDialog.show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: error message is " + connectionResult.getErrorMessage());
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        mUser = mAuth.getCurrentUser();
        username = null;
        userEmail = null;
        if (mUser != null) {
            mDatabaseUserStats = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Stats");
            mDatabaseStats = FirebaseDatabase.getInstance().getReference().child("Stats");
        }
        if (mUser != null) {
            username = mUser.getDisplayName();
            userEmail = mUser.getEmail();
        }
        guestMode = guestPrefs.getBoolean("mode", false);
        updateViews();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mUser = mAuth.getCurrentUser();
        username = null;
        userEmail = null;
        if (mUser != null) {
            username = mUser.getDisplayName();
            userEmail = mUser.getEmail();
        }
        guestMode = guestPrefs.getBoolean("mode", false);
        updateViews();
    }

    public void changeFragment(int i) {

        tabs.getTabAt(i).select();
    }
}
