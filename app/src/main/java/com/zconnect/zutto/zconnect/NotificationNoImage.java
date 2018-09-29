package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    EditText notificationDescription=(EditText)findViewById(R.id.notif_description);
    EditText notificationTitle=(EditText)findViewById(R.id.notif_title);
    EditText nottificationURL=(EditText)findViewById(R.id.notif_url);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_no_image);

        EditText notification=(EditText)findViewById(R.id.notif_description);
        Button sendNotification=(Button)findViewById(R.id.submit);

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
        mProgress.cancel();

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


        }
    }




}
