package com.zconnect.zutto.zconnect.commonModules;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static com.zconnect.zutto.zconnect.commonModules.GlobalFunctions.combineImages;

public class saveImage {

    public String saveImageLocally(Bitmap image, String name, Context ctx) {

        image = combineImages(image, ctx);
        OutputStream output;
        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath()
                + "/ZConnect/");
        dir.mkdirs();


        File file = new File(dir, name + ".jpg");

        try {

            output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            image.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();
            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
