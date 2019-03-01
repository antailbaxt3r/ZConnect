package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class ReferralCode extends BaseActivity {

    private SimpleDraweeView referralCodeImage;
    private Button inviteButton;
    private Uri mInvitationUrl;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_code);

        Toolbar toolbar = findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        progressDialog = new ProgressDialog(this);
        referralCodeImage = findViewById(R.id.image_referral_code);
        referralCodeImage.setImageResource(R.drawable.referral_code_bg_img);
//        Picasso.with(this).load(R.drawable.referral_code_bg_img).into(referralCodeImage);

        inviteButton = findViewById(R.id.invite_btn_referral_code);

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvitiation(FirebaseAuth.getInstance().getUid());
            }
        });

    }

    public void sendInvitiation(String referrerId) {
        Uri BASE_URI = Uri.parse("http://www.zconnect.com//");

        Uri APP_URI = BASE_URI.buildUpon().appendQueryParameter("referredBy", referrerId)
                .appendQueryParameter("communityRef", communityReference)
                .build();
        String encodedUri = null;
        try {
            encodedUri = URLEncoder.encode(APP_URI.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Task<ShortDynamicLink> referLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse("https://zconnect.page.link/?link="+encodedUri+"&apn=com.zconnect.zutto.zconnect&amv=11" ))
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(12).build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl = shortDynamicLink.getShortLink();

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //Your code goes here
//                                    Uri imageUri = Uri.parse(image);
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);

//                                    Bitmap bm = BitmapFactory.decodeStream(new URL(image)
//                                            .openConnection()
//                                            .getInputStream());


//                                    bm = mergeBitmap(BitmapFactory.decodeResource(context.getResources(),
//                                            R.drawable.background_icon_z), bm, context);
                                    String temp = "Join me on ZConnect, a private social network for your community!" +
                                            "\nWe'll both get 50 points each!"
                                            + "\n\n" + mInvitationUrl;

                                    shareIntent.putExtra(Intent.EXTRA_TEXT, temp);
                                    shareIntent.setType("text/plain");

//                                    path = MediaStore.Images.Media.insertImage(
//                                            context.getContentResolver(),
//                                            bm, "", null);
//                                    screenshotUri = Uri.parse(path);

//                                    shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
//                                    shareIntent.setOfferType("image/png");
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    progressDialog.dismiss();
                                    startActivityForResult(Intent.createChooser(shareIntent, "Share Via"), 0);
                                    Log.d("RRRR link share", "DONE");

                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    }
                });
//        progressDialog.dismiss();
    }
}
