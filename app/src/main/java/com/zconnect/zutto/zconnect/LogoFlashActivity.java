package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;


public class LogoFlashActivity extends BaseActivity {
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_flash);
        // Setting full screen view
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        handlePermission();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(LogoFlashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
            ActivityCompat.requestPermissions(LogoFlashActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 7);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 7:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            Intent intent = new Intent(LogoFlashActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }, 1500);

                } else {
                    Toast.makeText(this, "Permission Denied !, Retrying.", Toast.LENGTH_SHORT).show();
                    checkPermission();
                }
                break;
        }
    }

    void openHome() {
        new Timer().schedule(new TimerTask() {
            public void run() {
                if (mUser == null) {
                    Intent loginIntent = new Intent(LogoFlashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
                FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(mUser.getUid())) {
                            Intent intent = new Intent(LogoFlashActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            DatabaseReference currentUserDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
                            currentUserDbRef.child("Image").setValue(mUser.getPhotoUrl());
                            currentUserDbRef.child("Username").setValue(mUser.getDisplayName());
                            currentUserDbRef.child("Email").setValue(mUser.getEmail());
                            Intent editProfileIntent = new Intent(LogoFlashActivity.this, EditProfile.class);
                            editProfileIntent.putExtra("caller", "home");
                            editProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(editProfileIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showToast("Internet available , try again");
                    }
                });
            }
        }, 1700);
    }

    void handlePermission() {
        if (checkPermission())
            openHome();
    }
}
