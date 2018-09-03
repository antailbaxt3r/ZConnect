package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.commonModules.viewImage;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class OpenProductDetails extends BaseActivity {

    private ImageView productImage;
    private TextView productName, productPrice, productPriceType, productDescription, productSellerName;
    private Button productShortlist, productCall;
    private String productCategory;
    private DatabaseReference mDatabaseProduct;
    private DatabaseReference mDatabaseViews;
    private FirebaseAuth mAuth;
    private View.OnClickListener mListener;
    private Boolean flag;
    private ProgressBar progressBar;
    private LinearLayout productContent;
    private String productKey;
    private FirebaseUser user;
    private ValueEventListener listener;

    private LinearLayout chatLayout;
    private EditText chatEditText;
    private String mImageUri;
    private ProgressDialog progressDialog;
    private String path;
    private Uri screenshotUri;
    Typeface ralewayBold, ralewayLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_product_details);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
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
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        ralewayBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Bold.ttf");
        ralewayLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Light.ttf");

        Intent intent = getIntent();
        productKey = intent.getStringExtra("key");

        productImage = (ImageView) findViewById(R.id.product_image);
        productName = (TextView) findViewById(R.id.product_name);
        productPrice = (TextView) findViewById(R.id.product_price);
        productPriceType = (TextView) findViewById(R.id.product_price_type);
        productDescription = (TextView) findViewById(R.id.product_description);
        productSellerName = (TextView) findViewById(R.id.product_seller_name);
        productShortlist = (Button) findViewById(R.id.product_shortlist);
        progressBar = (ProgressBar) findViewById(R.id.product_loading);
        productContent = (LinearLayout) findViewById(R.id.product_content);
        productCall = (Button) findViewById(R.id.product_call);
        productCall.setTypeface(ralewayBold);
        chatLayout= (LinearLayout) findViewById(R.id.chatLayout);
        chatEditText = (EditText) findViewById(R.id.typer);
        chatEditText.setShowSoftInputOnFocus(false);

        progressDialog = new ProgressDialog(this);

        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //char room clicked
                Intent intent = new Intent(OpenProductDetails.this, ChatActivity.class);
                intent.putExtra("type","storeroom");
                intent.putExtra("key",productKey);
                intent.putExtra("name",productName.getText());
                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(productKey).toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        chatEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //char room clicked
                Intent intent = new Intent(OpenProductDetails.this, ChatActivity.class);
                intent.putExtra("type","storeroom");
                intent.putExtra("key",productKey);
                intent.putExtra("name",productName.getText());
                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(productKey).toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        mDatabaseProduct = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");


        progressBar.setVisibility(VISIBLE);
        productContent.setVisibility(INVISIBLE);

        SharedPreferences sharedPref = this.getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

       // mDatabaseViews = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(productKey).child("views");

//        if (!status) {
//            mAuth = FirebaseAuth.getInstance();
//            user = mAuth.getCurrentUser();
//
//            listener = new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    boolean userExists = false;
//                    for (DataSnapshot childSnapshot :
//                            dataSnapshot.getChildren()) {
//                        if (childSnapshot.getKey().equals(user.getUid()) && childSnapshot.exists() && childSnapshot.getValue(Integer.class) != null) {
//                            userExists = true;
//                            int originalViews = childSnapshot.getValue(Integer.class);
//                            mDatabaseViews.child(user.getUid()).setValue(originalViews + 1);
//
//                            break;
//                        } else {
//                            userExists = false;
//                        }
//                    }
//                    if (!userExists) {
//                        mDatabaseViews.child(user.getUid()).setValue(1);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            };
//
//            mDatabaseViews.addListenerForSingleValueEvent(listener);
//        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mDatabaseViews.removeEventListener(listener);
    }

    //Menu Overwrite
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_product_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent productKeyIntent = getIntent();
        productKey = productKeyIntent.getStringExtra("key");

        switch (item.getItemId()) {

            case R.id.share:

                CounterManager.eventShare(productKey);
                shareProduct(mImageUri, this.getApplicationContext(), productKey);
                break;
            case R.id.menu_chat_room:
                //char room clicked
                Intent intent = new Intent(OpenProductDetails.this, ChatActivity.class);
                intent.putExtra("type","storeroom");
                intent.putExtra("key",productKey);
                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(productKey).toString());
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    public Bitmap mergeBitmap(Bitmap bitmap2, Bitmap bitmap1, Context context) {
        Bitmap mergedBitmap = null;


        Drawable[] layers = new Drawable[2];

        layers[0] = new BitmapDrawable(context.getResources(), bitmap1);
        layers[1] = new BitmapDrawable(context.getResources(), bitmap2);

        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int width = layers[0].getIntrinsicWidth();
        int height = layers[0].getIntrinsicHeight();

        mergedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mergedBitmap);
        layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        layerDrawable.draw(canvas);


        //mergedBitmap=BitmapFactory.decodeResourceStream(layerDrawable)

        return mergedBitmap;
    }

    private void shareProduct(final String image, final Context context, final String productKey) {

        try {
            Uri BASE_URI = Uri.parse("http://www.zconnect.com/openproduct/");

            Uri APP_URI = BASE_URI.buildUpon().appendQueryParameter("key", productKey)
                    .appendQueryParameter("communityRef", communityReference)
                    .build();
            String encodedUri = null;
            try {
                encodedUri = URLEncoder.encode(APP_URI.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
//            https://your_subdomain.page.link/?link=your_deep_link&apn=package_name[&amv=minimum_version][&afl=fallback_link]
//            https://example.page.link/?link=https://www.example.com/invitation?gameid%3D1234%26referrer%3D555&apn=com.example.android&ibi=com.example.ios&isi=12345
                    .setLongLink(Uri.parse("https://zconnect.page.link/?link="+encodedUri+"&apn=com.zconnect.zutto.zconnect&amv=11" ))
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(12).build())
                    .buildShortDynamicLink()
                    .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                        @Override
                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                            if(task.isSuccessful())
                            {
                                //short link
                                final Uri shortLink = task.getResult().getShortLink();
                                final Uri flowcharLink = task.getResult().getPreviewLink();
                                progressDialog.setMessage("Loading...");
                                progressDialog.show();
                                //shareIntent.setPackage("com.whatsapp");
                                //Add text and then Image URI
                                Thread thread = new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            //Your code goes here
                                            Uri imageUri = Uri.parse(image);
                                            Intent shareIntent = new Intent();
                                            shareIntent.setAction(Intent.ACTION_SEND);

                                            Bitmap bm = BitmapFactory.decodeStream(new URL(image)
                                                    .openConnection()
                                                    .getInputStream());


                                            bm = mergeBitmap(BitmapFactory.decodeResource(context.getResources(),
                                                    R.drawable.background_icon_z), bm, context);
                                            String temp = "Hey, check out this product!" +
                                                    "\n*Item:* " + productName.getText()
                                                    + "\n*Price:* " + productPrice.getText()
                                                    + "\n*About:* " + productDescription.getText()
                                                    + "\n\n" + shortLink;

                                            shareIntent.putExtra(Intent.EXTRA_TEXT, temp);
                                            shareIntent.setType("text/plain");

                                            path = MediaStore.Images.Media.insertImage(
                                                    context.getContentResolver(),
                                                    bm, "", null);
                                            screenshotUri = Uri.parse(path);

                                            shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                                            shareIntent.setType("image/png");
                                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                            progressDialog.dismiss();
                                            startActivityForResult(Intent.createChooser(shareIntent, "Share Via"), 0);
                                            //context.startActivity(shareIntent);

                                        } catch (Exception e) {
                                            progressDialog.dismiss();
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                thread.start();
                            }
                            else {
                                Log.d("Dynamic link ERROR", task.getException().getMessage());

                            }
                        }
                    });
//                    .setLink(Uri.parse("https://www.zconnect.com/openevent?eventid%" ))
//                    .setDynamicLinkDomain("zconnect.page.link/")
//                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
//                            .setMinimumVersion(11)
//                            .build())
//                    .setIosParameters(new DynamicLink.IosParameters.Builder("https://www.google.com").build())
//                    .buildDynamicLink();
//            final Uri dynamicUri = dynamicLink.getUri();




        } catch (android.content.ActivityNotFoundException ex) {
            progressDialog.dismiss();
            //ToastHelper.MakeShortText("Whatsapp have not been installed.");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        productKey = intent.getStringExtra("key");
        mDatabaseProduct.child(productKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                productName.setText(dataSnapshot.child("ProductName").getValue().toString());
                productPrice.setText(dataSnapshot.child("Price").getValue().toString());
                productDescription.setText(dataSnapshot.child("ProductDescription").getValue().toString());
                productCategory = dataSnapshot.child("Category").getValue().toString();
                productCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CounterManager.StoroomCall(dataSnapshot.child("Category").getValue().toString());
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(dataSnapshot.child("Phone_no").getValue().toString().trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });

                try{
                    productSellerName.setText("by " + dataSnapshot.child("PostedBy").child("Username").getValue().toString());
                } catch (Exception e){

                }
                mImageUri = dataSnapshot.child("Image").getValue().toString();
                setImage(OpenProductDetails.this, dataSnapshot.child("ProductName").getValue().toString(), dataSnapshot.child("Image").getValue().toString(), productImage);

                if (dataSnapshot.hasChild("negotiable")) {
                    setProductPrice(productPrice, productPriceType, dataSnapshot.child("Price").getValue().toString(), dataSnapshot.child("negotiable").getValue().toString());
                } else {
                    setProductPrice(productPrice, productPriceType, dataSnapshot.child("Price").getValue().toString(), null);
                }

                defaultSwitch(productKey, productCategory, productShortlist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressBar.setVisibility(View.GONE);
                productContent.setVisibility(VISIBLE);
            }
        });


        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                final DatabaseReference userReservedReference = mDatabaseProduct.child(productKey);
                userReservedReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (flag) {

                            if (dataSnapshot.child("UsersReserved").hasChild(mAuth.getCurrentUser().getUid())) {
                                userReservedReference.child("UsersReserved").child(mAuth.getCurrentUser().getUid()).removeValue();
                                productShortlist.setText("Shortlist");
                                flag = false;
                                productShortlist.setTextColor(getApplicationContext().getResources().getColor(R.color.primaryText));
                                productShortlist.setTypeface(ralewayBold);

                            } else {
                                productShortlist.setText("Shortlisted");
                                final UsersListItemFormat userDetails = new UsersListItemFormat();
                                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                user.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                        UserItemFormat userItemFormat = dataSnapshot2.getValue(UserItemFormat.class);
                                        userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                                        userDetails.setName(userItemFormat.getUsername());
                                        userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                                        userDetails.setUserUID(userItemFormat.getUserUID());
                                        userReservedReference.child("UsersReserved").child(userItemFormat.getUserUID()).setValue(userDetails);
//
//                                        NotificationSender notificationSender=new NotificationSender(dataSnapshot.child("PostedBy").child("UID").getValue().toString(),null,null,null,null,userDetails.getUserUID(),productName.getText().toString(),KEY_PRODUCT,false,true,getApplicationContext());
//                                        notificationSender.execute();

                                        NotificationSender notificationSender = new NotificationSender(OpenProductDetails.this,userItemFormat.getUserUID());
                                        NotificationItemFormat productShortlistNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_SHORTLIST,userItemFormat.getUserUID());
                                        productShortlistNotification.setCommunityName(communityTitle);
                                        productShortlistNotification.setItemKey(productKey);
                                        productShortlistNotification.setItemName(dataSnapshot.child("ProductName").getValue().toString());
                                        productShortlistNotification.setUserName(userItemFormat.getUsername());
                                        productShortlistNotification.setUserMobileNumber(userItemFormat.getMobileNumber());
                                        productShortlistNotification.setUserImage(userItemFormat.getImageURLThumbnail());


                                        notificationSender.execute(productShortlistNotification);


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                productShortlist.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                                productShortlist.setTypeface(ralewayBold);
                                flag = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        productShortlist.setOnClickListener(mListener);



    }

    public void setProductPrice(TextView productPrice, TextView productPriceType, String productPriceValue, String negotiable) {
        String price = "";
        if (negotiable != null) {
            if (negotiable.equals("1")) {
                price = "₹" + productPriceValue + "/-";
                productPriceType.setVisibility(View.VISIBLE);
            } else if (negotiable.equals("2")) {
                price = "Price Negotiable";
                productPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else
                price = "₹" + productPriceValue + "/-";

            productPrice.setText(price);
        } else {
            productPrice.setText("₹" + productPriceValue + "/-");
        }

    }

    public void setImage(final Activity activity, final String productName, final String imageUrl, final ImageView productImage) {
        Picasso.with(this).load(imageUrl).into(productImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(INVISIBLE);
                productContent.setVisibility(VISIBLE);
            }

            @Override
            public void onError() {

            }
        });
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog mProgress = new ProgressDialog(OpenProductDetails.this);
                mProgress.setMessage("Loading...");
                mProgress.show();
                animate(activity, productName, imageUrl, productImage);
                mProgress.dismiss();
            }
        });

    }

    public void animate(final Activity activity, final String name, String url, ImageView productImage) {
        final Intent i = new Intent(this, viewImage.class);
        i.putExtra("currentEvent", name);
        i.putExtra("eventImage", url);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, productImage, this.getResources().getString(R.string.transition_string));

        startActivity(i, optionsCompat.toBundle());
    }

    public void defaultSwitch(final String key, final String category, final Button productShortlist) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        DatabaseReference storeroomReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");

        storeroomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productShortlist.setOnClickListener(null);
                if (dataSnapshot.child(key).child("UsersReserved").hasChild(userId)) {
                    productShortlist.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                    productShortlist.setTypeface(ralewayBold);
                    productShortlist.setText("Shortlisted");
                    CounterManager.StoroomShortList(category, key);
                    //ReserveStatus.setTextColor(ContextCompat.getColor(ctx, R.color.teal600));

                } else {

                    productShortlist.setTextColor(getApplicationContext().getResources().getColor(R.color.primaryText));
                    productShortlist.setTypeface(ralewayBold);
                    productShortlist.setText("Shortlist");
                    CounterManager.StoroomShortListDelete(category, key);
                }
                productShortlist.setOnClickListener(mListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}
