package com.zconnect.zutto.zconnect.utilities;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.CabPooling;
import com.zconnect.zutto.zconnect.R;

public class UIUtilities {
    private Snackbar snackbar;

    public Snackbar getSnackbar(View view, String message, int length, Context context) {
        snackbar = Snackbar.make(view, message, length);
        TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackBarText.setTextColor(Color.WHITE);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        return snackbar;
    }
}
