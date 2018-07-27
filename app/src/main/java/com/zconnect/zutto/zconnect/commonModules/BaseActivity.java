package com.zconnect.zutto.zconnect.commonModules;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.R;

import org.joda.time.LocalDate;


public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected Snackbar snack;
    private ProgressDialog progress;
    private networkStatereceiver receiver;


    public static String communityReference;

    public static DatabaseReference ref;
    private static LocalDate dateTime = new LocalDate();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            setColour(colorPrimary);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        isNetworkAvailable(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new networkStatereceiver();
        registerReceiver(receiver, filter);
        SharedPreferences communitySP = getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        //CounterManager.communityCode = communityReference;
    }

    public void setColour(int colour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(colour);
            getWindow().setNavigationBarColor(colour);

        }
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBackPressed();
                        }
                    });
        }
    }


    @Override
    protected void onResume() {
        isNetworkAvailable(getApplicationContext());
        super.onResume();
    }

    public void hideToolbar() {
        if (getToolbar() != null)
            getToolbar().setVisibility(View.GONE);
    }

    protected void showBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays a toast in current activity. In this method the duration
     * supplied is Short by default. If you want to specify duration
     * use {@link BaseActivity#showToast(String, int)} method.
     *
     * @param message Message that the toast must show.
     */
    public void showToast(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    /**
     * Displays a toast in current activity. The duration can of two types:
     * <ul>
     * <li>SHORT</li>
     * <li>LONG</li>
     * </ul>
     *
     * @param message   Message that the toast must show.
     * @param toastType Duration for which the toast must be visible.
     */
    public void showToast(String message, int toastType) {
        Toast.makeText(BaseActivity.this, message, toastType).show();
    }

    public Snackbar showSnack(String message, int length) {
        try {
            return showSnack(message, length, findViewById(R.id.CoordinatorLt));
        } catch (Exception e) {

            try {
                return showSnack(message, length, getCurrentFocus());
            } catch (Exception e1) {
                return showSnack(message, length, getWindow().getDecorView());
            }
        }
    }

    public Snackbar showSnack(String message, int length, View view) {
        try {
            snack = Snackbar.make(view, message, length);
            TextView snackBarText = (TextView) snack.getView().findViewById(R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snackBarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                    snack.getView().getLayoutParams();
            snack.getView().setLayoutParams(params);

            snack.show();
            return snack;
        } catch (Exception e) {
//            showToast(e.getMessage());
            Log.d("Snack Error", e.getMessage());
            return null;
        }
    }

    public Snackbar showSnack(String message) {
        return showSnack(message, Snackbar.LENGTH_LONG);
    }

    public Snackbar getSnack() {
        return snack;
    }

    public void showProgressDialog() {
        showProgressDialog("Loading", this);
    }

    public void showProgressDialog(String message) {
        showProgressDialog(message, this);
    }

    public void showProgressDialog(String message, Context ctx) {

        try {
            if (progress == null) {
                progress = new ProgressDialog(ctx, ProgressDialog.STYLE_SPINNER);
                progress.setCancelable(false);
            }
            progress.setMessage(message);
            progress.show();
        } catch (Exception e) {
        }
    }

    public void hideProgressDialog() {
        if (progress != null && progress.isShowing()) {
            try {
                progress.dismiss();
                progress = null;
            } catch (Exception e) {
            }
        }
    }

    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            setTitle(title);
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbarTitle(String title) {
        setActionBarTitle(title);
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void makeActivityFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class networkStatereceiver extends BroadcastReceiver {
        public networkStatereceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            isNetworkAvailable(BaseActivity.this);
        }
    }
}