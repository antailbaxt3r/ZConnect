package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

public class NotificationNoImage extends BaseActivity {

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsername;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;

    private EditText notificationDescription;
    private EditText notificationTitle;
    private EditText nottificationURL;
    private Button sendNotification;
    private Toolbar mActionBarToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_no_image);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(mActionBarToolbar);

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
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

        mProgress = new ProgressDialog(this);

        notificationDescription =(EditText)findViewById(R.id.notif_description);
        notificationTitle =(EditText)findViewById(R.id.notif_title);
        nottificationURL =(EditText)findViewById(R.id.notif_url);
        sendNotification =(Button)findViewById(R.id.submit);

        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable(getApplicationContext())) {

                    Toast toast=Toast.makeText(getApplicationContext(),"No internet connection", Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    startPosting();
                }
            }
        });

    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void startPosting(){

        mProgress.setMessage("Posting Notification..");
        mProgress.show();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        final String userId = user.getUid();
        mUsername = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");

        if (!TextUtils.isEmpty(notificationDescription.getText()) && !TextUtils.isEmpty(notificationTitle.getText()) && !TextUtils.isEmpty(nottificationURL.getText()) ) {

            NotificationSender notificationSender = new NotificationSender(NotificationNoImage.this, userId);
            NotificationItemFormat addImageNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_TEXT_URL, userId);
            addImageNotification.setItemMessage(notificationDescription.getText().toString());
            addImageNotification.setItemTitle(notificationTitle.getText().toString());
            addImageNotification.setItemURL(nottificationURL.getText().toString());

            notificationSender.execute(addImageNotification);

            mProgress.dismiss();
            finish();
        }else {
            Snackbar snack = Snackbar.make(notificationDescription, "Fields are empty", Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            snack.show();
        }
    }




}
