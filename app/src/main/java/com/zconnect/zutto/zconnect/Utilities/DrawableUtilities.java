package com.zconnect.zutto.zconnect.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;

public class DrawableUtilities {
    private Context context;

    public DrawableUtilities() {

    }

    public DrawableUtilities(Context context) {
        this.context = context;
    }

    public BitmapDrawable writeOnDrawable(int drawableId, String text) {
        try {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);

            Canvas canvas = new Canvas(bm);
            canvas.drawText(text, bm.getWidth()/2, bm.getHeight()/2, paint);

            return new BitmapDrawable(context.getResources(), bm);
        }
        catch (Exception e) {
            Log.d("Error Alert! ", e.getMessage());
            return null;
        }
    }
}
