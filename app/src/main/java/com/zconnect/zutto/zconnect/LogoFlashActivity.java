package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class
LogoFlashActivity extends BaseActivity {
    private FirebaseUser mUser;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_flash);
        // Setting full screen view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                } else {
                    FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(mUser.getUid())) {
                                Intent intent = new Intent(LogoFlashActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                DatabaseReference currentUserDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
                                if (mUser.getPhotoUrl() != null) {
                                    currentUserDbRef.child("Image").setValue(mUser.getPhotoUrl().toString());
                                }
                                currentUserDbRef.child("Username").setValue(mUser.getDisplayName());
                                currentUserDbRef.child("Email").setValue(mUser.getEmail());
                                Intent editProfileIntent = new Intent(LogoFlashActivity.this, EditProfileActivity.class);
                                startActivity(editProfileIntent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: ", databaseError.toException());
                            showToast("Internet available , try again");
                        }
                    });
                }
            }
        }, 1700);
    }

    void handlePermission() {
        if (checkPermission())
            openHome();
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}

