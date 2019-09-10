package com.zconnect.zutto.zconnect.utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.LogoFlashActivity;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtilities {

    Context context;

    public static String READ_EXTERNAL_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    public static String CALL_PHONE = android.Manifest.permission.CALL_PHONE;
    public static String READ_CONTACTS = android.Manifest.permission.READ_CONTACTS;

    public PermissionUtilities(Context context)
    {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isEnabled(String permission){
        if(context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void request(String permission){
        String _perm[] = new String [1];
        if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            _perm[0] = permission;
            ((Activity)context).requestPermissions(_perm, 101);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == 101){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                if(((Activity)context).shouldShowRequestPermissionRationale(permissions[0])){
                    new AlertDialog.Builder(context)
                            .setMessage("The application needs this permission")
                            .setPositiveButton("Allow", (dialog, which) -> request(permissions[0]))
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                }
                return;
            }
        }
    }
}
