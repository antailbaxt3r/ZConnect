package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;


public class LogoFlashActivity extends BaseActivity {
    /*private final String TAG = getClass().getSimpleName();*/
    //Request code permission request external storage
    private final int RC_PERM_REQ_EXT_STORAGE = 7;
    private ImageView bgImage;
    private DatabaseReference mDatabase;
    private View bgColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_flash);
        bgImage = (ImageView) findViewById(R.id.bgImage);
        bgColor = (View) findViewById(R.id.bgColor);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("ui/logoFlash");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("bgUrl").getValue()!=null)
                {
                    bgImage.setImageURI(Uri.parse(dataSnapshot.child("bgUrl").getValue(String.class)));
                } else{
                    bgColor.setVisibility(View.GONE);
                    bgImage.setBackground(null);
                    bgImage.setBackgroundColor(Color.parseColor(dataSnapshot.child("bgUrl").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Setting full screen view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                if(checkPermission()){
                    // Do not wait so that user doesn't realise this is a new launch.
                    startActivity(new Intent(LogoFlashActivity.this, HomeActivity.class));
                    finish();
            }

        }},2000);
    }

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
                startActivity(new Intent(LogoFlashActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Permission Denied !, Retrying.", Toast.LENGTH_SHORT).show();
                checkPermission();
            }
        }
    }
}

