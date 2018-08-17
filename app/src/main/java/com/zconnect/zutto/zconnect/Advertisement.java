package com.zconnect.zutto.zconnect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.collection.LLRBNode;
import com.zconnect.zutto.zconnect.adapters.RecyclerViewAdapterAdvertisement;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.AdItemFormat;
import com.zconnect.zutto.zconnect.adapters.AdRVAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Advertisement extends BaseActivity {
    Vector<AdItemFormat> adItemFormats = new Vector<>();
    private AdRVAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Advertisement");
    private ProgressBar progressBar;


    //for G ads
    // A Native Express ad is placed in every nth position in the RecyclerView.
    public static final int ITEMS_PER_AD = 2;

    // The Native Express ad height.
    private static final int NATIVE_EXPRESS_AD_HEIGHT = 150;

    // The Native Express ad unit ID.
    private static final String AD_UNIT_ID ="ca-app-pub-9212249564562455/7570111641";

    // The RecyclerView that holds and displays Native Express ads and menu items.
    private RecyclerView mRecyclerView;

    // List of Native Express ads and MenuItems that populate the RecyclerView.
    private List<Object> mRecyclerViewItems = new ArrayList<>(10);


    //for native single
    private NativeExpressAdView mAdViewNative,mAdViewNative2,mAdViewNative3;
    VideoController mVideoController;

    //for banner
    private AdView mAdViewBanner1;
    private int NO_OF_ADS=30;
    private AdView mAdViewBanner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

        //recyclerView = (RecyclerView) findViewById(R.id.content_ad_rv);
        //progressBar = (ProgressBar) findViewById(R.id.content_ad_progress);

        //MAIN--------------------------------------------------------------------------------------

        //Keep databaseReference in sync even without needing to call valueEventListener
        databaseReference.keepSynced(true);

        //setHasFixedSize is used to optimise RV if we know for sure that this view's bounds do not
        // change with data
        //recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        //Setup layout manager. VERY IMP ALWAYS


        mRecyclerView = (RecyclerView) findViewById(R.id.content_ad_rv);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView.
        mRecyclerView.setHasFixedSize(true);

        // Specify a linear layout manager.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);


        // Update the RecyclerView item's list with Native Express ads.
        // addMenuItemsFromJson();
        //addNativeExpressAds();
        //setUpAndLoadNativeExpressAds();

        RecyclerView.Adapter adapter = new RecyclerViewAdapterAdvertisement(this, mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

        //adapter = new AdRVAdapter(this, adItemFormats);
        //recyclerView.setAdapter(adapter);

        String android_id = Settings.Secure.getString(Advertisement.this.getContentResolver(),Settings.Secure.ANDROID_ID);

        //for banner ads
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, "ca-app-pub-9212249564562455~7570111641");

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdViewBanner1 = (AdView) findViewById(R.id.ad_view_banner1);
        mAdViewBanner2 = (AdView) findViewById(R.id.ad_view_banner2);
        mAdViewNative=(NativeExpressAdView)findViewById(R.id.adView);
        mAdViewNative2=(NativeExpressAdView)findViewById(R.id.adView2);


        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("D0C534373C4F3E50798BCF32BFF922C5")
                .build();
        AdRequest adRequest2 = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("D0C534373C4F3E50798BCF32BFF922C5")
                .build();

        // Start loading the ad in the background.
        mAdViewBanner1.loadAd(adRequest);
        mAdViewBanner2.loadAd(adRequest2);

        // Set its video options.
        mAdViewNative.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());

        // The VideoController can be used to get lifecycle events and info about an ad's video
        // asset. One will always be returned by getVideoController, even if the ad has no video
        // asset.
        mVideoController = mAdViewNative.getVideoController();
        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
                Log.d("Ads", "Video playback is finished.");
                super.onVideoEnd();
            }
        });

        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        mAdViewNative.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mVideoController.hasVideoContent()) {
                    Log.d("Ads", "Received an ad that contains a video asset.");
                } else {
                    Log.d("Ads", "Received an ad that does not contain a video asset.");
                }
            }
        });


        //mAdViewNative.loadAd(new AdRequest.Builder().addTestDevice("D0C534373C4F3E50798BCF32BFF922C5").build());
        mAdViewNative.loadAd(new AdRequest.Builder().build());

        mAdViewNative2.loadAd(new AdRequest.Builder().build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_advertisement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_info) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Advertisement.this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("To post Advertisements on the ZConnect \nContact : zconnectinc@gmail.com");

            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Contact", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "zconnectinc@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Post Advertisements on ZConnect");
                    // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send uid..."));
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorHighlight));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void addNativeExpressAds() {

        // Loop through the items array and place a new Native Express ad in every ith position in
        // the items List.
        for (int i = 0; i < NO_OF_ADS; i +=1) {
            final NativeExpressAdView adView = new NativeExpressAdView(Advertisement.this);
            adView.setAdSize(new AdSize(320,150));
            adView.setAdUnitId(AD_UNIT_ID);
            mRecyclerViewItems.add(i,adView);
        }
    }


    private void setUpAndLoadNativeExpressAds() {
        // Use a Runnable to ensure that the RecyclerView has been laid out before setting the
        // ad size for the Native Express ad. This allows us to set the Native Express ad's
        // width to match the full width of the RecyclerView.
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                final float scale = Advertisement.this.getResources().getDisplayMetrics().density;
                // Set the ad size and ad unit ID for each Native Express ad in the items list.
                for (int i = 0; i < NO_OF_ADS; i += 1) {
                    final NativeExpressAdView adView =
                            (NativeExpressAdView) mRecyclerViewItems.get(i);
                    final CardView cardView = (CardView) findViewById(R.id.ad_card_view);
                    final int adWidth = cardView.getWidth() - cardView.getPaddingLeft()
                            - cardView.getPaddingRight();
                    AdSize adSize = new AdSize((int) (adWidth / scale), NATIVE_EXPRESS_AD_HEIGHT);
                      adView.setAdSize(adSize);
                      adView.setAdUnitId(AD_UNIT_ID);
                }

                // Load the first Native Express ad in the items list.
                loadNativeExpressAd(0);



            }
        });
    }

    /**
     * Loads the Native Express ads in the items list.
     */
    private void loadNativeExpressAd(final int index) {

        if (index >= mRecyclerViewItems.size()) {
            return;
        }

        Object item = mRecyclerViewItems.get(index);
        if (!(item instanceof NativeExpressAdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a Native"
                    + " Express ad.");
        }

        final NativeExpressAdView adView = (NativeExpressAdView) item;

        // Set an AdListener on the NativeExpressAdView to wait for the previous Native Express ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous Native Express ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadNativeExpressAd(index + 1);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous Native Express ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("Ads", "The previous Native Express ad failed to load. Attempting to"
                        + " load the next Native Express ad in the items list.");
                loadNativeExpressAd(index + 1);
            }
        });

        // Load the Native Express ad.
        //adView.loadAd(new AdRequest.Builder().build());//.addTestDevice("D0C534373C4F3E50798BCF32BFF922C5").build());

        mAdViewBanner1.loadAd(new AdRequest.Builder().addTestDevice("D0C534373C4F3E50798BCF32BFF922C5").build());

    }

}
