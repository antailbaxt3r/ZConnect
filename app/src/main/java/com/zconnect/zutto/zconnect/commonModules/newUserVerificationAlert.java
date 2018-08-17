package com.zconnect.zutto.zconnect.commonModules;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.VerificationPage;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

public class newUserVerificationAlert {


    public static void buildAlertCheckNewUser(String userType, String featureName, final Context ctx)
    {

        if(userType.equals(UsersTypeUtilities.KEY_PENDING)){

            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
            builder.setMessage("Your profile will be verified soon to access " + featureName +", do you want to change proof ID?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(ctx, VerificationPage.class);
                            ctx.startActivity(intent);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            final android.app.AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ctx.getResources().getColor(R.color.colorHighlight));

        }else if(userType.equals(UsersTypeUtilities.KEY_NOT_VERIFIED)) {
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
            builder.setMessage("Please verify your profile to access " + featureName)
                    .setPositiveButton("Verify Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(ctx, VerificationPage.class);
                            ctx.startActivity(intent);

                        }
                    })
                    .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ctx, "You need to verify your profile to access any feature", Toast.LENGTH_LONG).show();
                        }
                    });

            final android.app.AlertDialog dialog = builder.create();

            dialog.setCancelable(false);
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ctx.getResources().getColor(R.color.colorHighlight));
        }
    }
}
