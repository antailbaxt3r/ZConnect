package com.zconnect.zutto.zconnect.commonModules;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.zconnect.zutto.zconnect.BuildConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shubhamk on 14/4/17.
 */

public class IntentHandle {

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    public Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();


        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureImageOutputUri(context));
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    "Pick Image ");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    public Uri getPickImageResultUri(Intent data, Context context) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri(context) : data.getData();
    }

    private Uri getCaptureImageOutputUri(Context context) {
        // File getImage = getExternalCacheDir();
        // if (getImage != null) {
        Uri outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.jpg"));
        // }
        return outputFileUri;
    }
}
