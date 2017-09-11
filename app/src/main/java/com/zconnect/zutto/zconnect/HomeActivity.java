package com.zconnect.zutto.zconnect;

import android.annotation.SuppressLint;
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
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = getClass().getSimpleName();
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
     */
    private SimpleDraweeView navHeaderBackground;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.view_pager_app_bar_home)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_app_bar_home)
    TabLayout tabLayout;
    @BindView(R.id.toolbar_app_bar_home)
    Toolbar toolbar;
    private MenuItem editProfileItem;
    private ActionBarDrawerToggle toggle;
    private String userEmail;
    private String username;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference phoneBookDbRef;
    private DatabaseReference mDatabasePopUps;
    /**
     * /ui node
     */
    private DatabaseReference uiDbRef;
    private FirebaseUser mUser;
    private boolean guestMode;
    private SharedPreferences defaultPrefs;
    private SharedPreferences guestPrefs;
    private AlertDialog addContactDialog;
    private Homescreen homescreen;
    private Recents recents;

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
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
        homescreen = new Homescreen();
        recents = new Recents();
        initListeners();
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        editProfileItem = navigationView.getMenu().findItem(R.id.edit_profile);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setSupportActionBar(toolbar);

        setupViewPager(viewPager);

        //Setup tabLayout with viewpager
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

        FirebaseMessaging.getInstance().subscribeToTopic("ZCM");

        mDatabasePopUps = FirebaseDatabase.getInstance().getReference().child("PopUps");
        mDatabasePopUps.keepSynced(true);
    }

    /**
     * All {@link ValueEventListener}s used in this class are defined here.
     */
    private void initListeners() {
        popupsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> popUpUrl1 = new ArrayList<>();
                ArrayList<String> importance = new ArrayList<>();
                boolean dataComplete = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                String first = preferences.getString("popup", "");
                boolean firstTimePopUp = Boolean.parseBoolean(first);
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    if (shot.child("imp").getValue(String.class) != null && shot.child("imageUrl").getValue(String.class) != null) {
                        popUpUrl1.add(shot.child("imageUrl").getValue(String.class));
                        importance.add(shot.child("imp").getValue(String.class));
                        dataComplete = true;
                    } else {
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
                    } else if (importanceDigit == 4) {
                        show = true;
                    } else {
                        show = false;
                    }

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
            case R.id.infone: {
                CounterManager.InfoneOpen();
                Intent intent = new Intent(this, InfoneActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.shop: {
                CounterManager.ShopOpen();
                Intent intent = new Intent(this, Shop.class);
                startActivity(intent);
                break;
            }
            case R.id.storeRoom: {
                CounterManager.StoreRoomOpen();
                startActivity(new Intent(HomeActivity.this, TabStoreRoom.class));
                break;
            }
            case R.id.events: {
                CounterManager.EventOpen();
                startActivity(new Intent(HomeActivity.this, AllEvents.class));
                break;
            }
            case R.id.cabpool: {
                startActivity(new Intent(HomeActivity.this, CabPooling.class));
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

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void setupViewPager(ViewPager viewPager) {
        HomeActivity.ViewPagerAdapter adapter = new HomeActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(homescreen, "Features");
        adapter.addFragment(recents, "Recents");
        viewPager.setAdapter(adapter);
    }

    /**
     * This method should be called only for logged in users.
     */
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
