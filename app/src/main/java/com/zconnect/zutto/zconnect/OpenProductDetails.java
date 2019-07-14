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
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.commonModules.viewImage;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.ProductUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class OpenProductDetails extends BaseActivity {

    private ImageView productImage;
    private TextView productName, productPrice, productPriceType, productDescription, productSellerName, productDate;
    private Button productShortlist, productCall;
    private String productCategory;
    private DatabaseReference mDatabaseProduct;
    private DatabaseReference mDatabaseViews;
    private FirebaseAuth mAuth;
    private View.OnClickListener mListener;
    private Boolean flag;
    private ProgressBar progressBar;
    private LinearLayout productContent;
    private String productKey, type;
    private FirebaseUser user;
    private ValueEventListener listener;

    private LinearLayout chatLayout;
    private EditText chatEditText;
    private String mImageUri;
    private ProgressDialog progressDialog;
    private String path;
    private Uri screenshotUri;
    Typeface ralewayBold, ralewayLight;
    private LinearLayout askTag;
    private TextView askText;

    private DatabaseReference databaseReferenceUser;
    private DatabaseReference databaseReferenceSellingUser;
    String productSellerUserUID;
    String productSellerUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_product_details);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setToolbar();
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
        ralewayBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Bold.ttf");
        ralewayLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Light.ttf");

        SharedPreferences communitySP = getSharedPreferences("communityName", MODE_PRIVATE);
        final String communityReference = communitySP.getString("communityReference", null);

        Intent intent = getIntent();
        productKey = intent.getStringExtra("key");

        productImage = (ImageView) findViewById(R.id.product_image);
        productName = (TextView) findViewById(R.id.product_name);
        productPrice = (TextView) findViewById(R.id.product_price);
        productPriceType = (TextView) findViewById(R.id.product_price_type);
        productDescription = (TextView) findViewById(R.id.product_description);
        productSellerName = (TextView) findViewById(R.id.product_seller_name);
        productDate = (TextView) findViewById(R.id.product_date);
        productShortlist = (Button) findViewById(R.id.product_shortlist);
        progressBar = (ProgressBar) findViewById(R.id.product_loading);
        productContent = (LinearLayout) findViewById(R.id.product_content);
        productCall = (Button) findViewById(R.id.product_call);
        productCall.setTypeface(ralewayBold);
        chatLayout = (LinearLayout) findViewById(R.id.chatLayout);
        chatEditText = (EditText) findViewById(R.id.typer);
        askTag = (LinearLayout) findViewById(R.id.ask_tag_open_product_details);
        askText = (TextView) findViewById(R.id.ask_text_open_product_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            chatEditText.setShowSoftInputOnFocus(false);
        }

        progressDialog = new ProgressDialog(this);
        View.OnClickListener onChat = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //char room clicked
                databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.child("userChats").hasChild(productSellerUserUID)) {
//                            userImageURL = dataSnapshot.child("imageURL").getValue().toString();
                            Log.d("Try", createPersonalChat(mAuth.getCurrentUser().getUid(), productSellerUserUID));
                        }
                        databaseReferenceUser.child("userChats").child(productSellerUserUID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String key = dataSnapshot.getValue().toString();
                                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                                intent.putExtra("type", "personalChats");
                                intent.putExtra("store_room_message", "Hey there, I was checking out the following product:\nProduct Name: " +
                                        productName.getText().toString() + "\nProduct Category:" + productCategory + "\nPrice:" +
                                        productPrice.getText().toString());

                                intent.putExtra("name", productSellerName.getText());
                                intent.putExtra("tab", "personalChats");
                                intent.putExtra("key", key);
                                startActivity(intent);
                                overridePendingTransition(0, 0);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


//                Intent intent = new Intent(OpenProductDetails.this, ChatActivity.class);
//                intent.putExtra("type","storeroom");
//                intent.putExtra("key",productKey);
//                intent.putExtra("name",productName.getText());
//                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(productKey).toString());
//                startActivity(intent);
//                overridePendingTransition(0, 0);
            }
        };

        chatLayout.setOnClickListener(onChat);


//        chatEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //char room clicked
//                Intent intent = new Intent(OpenProductDetails.this, ChatActivity.class);
//                intent.putExtra("type","storeroom");
//                intent.putExtra("key",productKey);
//                intent.putExtra("name",productName.getText());
//                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(productKey).toString());
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//            }
//        });
        chatEditText.setOnClickListener(onChat);
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

    private String createPersonalChat(final String uid, final String sellingUserUID) {
        final DatabaseReference databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories");
        final DatabaseReference databaseReferenceTabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child("personalChats");
        final DatabaseReference newPush = databaseReferenceCategories.push();
        final DatabaseReference databaseReferenceUserForums = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("userForums");
        newPush.child("name").setValue(false);
        Long postTimeMillis = System.currentTimeMillis();
        newPush.child("PostTimeMillis").setValue(postTimeMillis);
        newPush.child("UID").setValue(newPush.getKey());
        newPush.child("tab").setValue("personalChats");
        newPush.child("Chat");
        final UserItemFormat[] user = {null};


        databaseReferenceSellingUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);

                UsersListItemFormat userDetails = new UsersListItemFormat();

                userDetails.setImageThumb(userItem.getImageURLThumbnail());

                userDetails.setName(userItem.getUsername());
                userDetails.setPhonenumber(userItem.getMobileNumber());
                userDetails.setUserUID(userItem.getUserUID());
                userDetails.setUserType(ForumsUserTypeUtilities.KEY_ADMIN);


                HashMap<String,UsersListItemFormat> userList = new HashMap<String,UsersListItemFormat>();
                userList.put(sellingUserUID,userDetails);
                Log.d("USEROBJECT",UserUtilities.currentUser.toString());
                UsersListItemFormat currentUser = new UsersListItemFormat();
                currentUser.setImageThumb(UserUtilities.currentUser.getImageURLThumbnail());
                currentUser.setName(UserUtilities.currentUser.getUsername());
                currentUser.setPhonenumber(UserUtilities.currentUser.getMobileNumber());
                currentUser.setUserUID(UserUtilities.currentUser.getUserUID());
                currentUser.setUserType(UserUtilities.currentUser.getUserType());
                userList.put(uid,currentUser);
                databaseReferenceTabsCategories.child(newPush.getKey()).child("users").setValue(userList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


        databaseReferenceUser.child("userChats").child(sellingUserUID).setValue(newPush.getKey());
        databaseReferenceSellingUser.child("userChats").child(uid).setValue(newPush.getKey());

        databaseReferenceTabsCategories.child(newPush.getKey()).child("name").setValue(false);
        databaseReferenceTabsCategories.child(newPush.getKey()).child("catUID").setValue(newPush.getKey());
        databaseReferenceTabsCategories.child(newPush.getKey()).child("tabUID").setValue("personalChats");
        databaseReferenceTabsCategories.child(newPush.getKey()).child("lastMessage").setValue("Null");



        return newPush.getKey();

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


        CounterItemFormat counterItemFormat = new CounterItemFormat();
        HashMap<String, String> meta = new HashMap<>();

        switch (item.getItemId()) {

            case R.id.share:
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_SHARE);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                shareProduct(mImageUri, this.getApplicationContext(), productKey);
                break;
            case R.id.menu_chat_room:

                meta.put("type", "fromMenu");
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_OPEN_CHAT);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);


                CounterPush counterPush2 = new CounterPush(counterItemFormat, communityReference);
                counterPush2.pushValues();

//                Intent intent = new Intent(OpenProductDetails.this, ChatActivity.class);
//                intent.putExtra("type","storeroom");
//                intent.putExtra("key",productKey);
//                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(productKey).toString());
//                startActivity(intent);
                databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.child("userChats").hasChild(productSellerUserUID)) {
//                            userImageURL = dataSnapshot.child("imageURL").getValue().toString();
                            Log.d("Try", createPersonalChat(mAuth.getCurrentUser().getUid(), productSellerUserUID));
                        }
                        databaseReferenceUser.child("userChats").child(productSellerUserUID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String key = dataSnapshot.getValue().toString();
                                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                                intent.putExtra("type", "personalChats");
                                intent.putExtra("store_room_message", "Hey there, I was checking out the following product:\nProduct Name: " +
                                        productName.getText().toString() + "\nProduct Category:" + productCategory + "\nPrice:" +
                                        productPrice.getText().toString());

                                intent.putExtra("name", productSellerName.getText());
                                intent.putExtra("tab", "personalChats");
                                intent.putExtra("key", key);
                                startActivity(intent);
                                overridePendingTransition(0, 0);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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
                    .setLongLink(Uri.parse("https://zconnect.page.link/?link=" + encodedUri + "&apn=com.zconnect.zutto.zconnect&amv=11"))
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(12).build())
                    .buildShortDynamicLink()
                    .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                        @Override
                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                            if (task.isSuccessful()) {
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
                            } else {
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

        productKey = getIntent().getExtras().getString("key");
        type = getIntent().getExtras().getString("type");
        if (type != null && type.equals(ProductUtilities.TYPE_ASK_STR)) {
            productPrice.setVisibility(View.GONE);
            productPriceType.setVisibility(View.GONE);
            askTag.setVisibility(VISIBLE);
        } else {
            askTag.setVisibility(View.GONE);
            productPrice.setVisibility(VISIBLE);
            productPriceType.setVisibility(VISIBLE);
        }
        mDatabaseProduct.child(productKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                try {
                    productName.setText(dataSnapshot.child("ProductName").getValue().toString());
                    productSellerUserUID = dataSnapshot.child("PostedBy").child("UID").getValue().toString();
                    productSellerUserImage = dataSnapshot.child("PostedBy").child("ImageThumb").getValue().toString();
                    databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    databaseReferenceSellingUser = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(productSellerUserUID);
                    if (!type.equals(ProductUtilities.TYPE_ASK_STR))
                        productPrice.setText(dataSnapshot.child("Price").getValue().toString());
                    productDescription.setText(dataSnapshot.child("ProductDescription").getValue().toString());
                    productCategory = dataSnapshot.child("Category").getValue().toString();
                    productCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();

                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_CALL);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);

                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(dataSnapshot.child("Phone_no").getValue().toString().trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    });

                    try {
                        productSellerName.setText("by " + dataSnapshot.child("PostedBy").child("Username").getValue().toString());
                    } catch (Exception e) {

                    }
                    try {
                        TimeUtilities tu = new TimeUtilities(dataSnapshot.child("PostTimeMillis").getValue(Long.class), System.currentTimeMillis());
                        productDate.setText(tu.calculateTimeAgoStoreroom());
                    } catch (Exception e) {
                    }
                    if (dataSnapshot.hasChild("Image")) {
                        askText.setVisibility(View.GONE);
                        productImage.setVisibility(VISIBLE);
                        mImageUri = dataSnapshot.child("Image").getValue().toString();
                        setImage(OpenProductDetails.this, dataSnapshot.child("ProductName").getValue().toString(), dataSnapshot.child("Image").getValue().toString(), productImage);
                    } else {
                        productImage.setVisibility(View.GONE);
                        askText.setVisibility(VISIBLE);
                        askText.setText(productName.getText());
                        progressBar.setVisibility(View.GONE);
                        productContent.setVisibility(VISIBLE);
                    }

                    setProductPrice(productPrice, dataSnapshot.child("Price").getValue().toString());

                    if (dataSnapshot.hasChild("isNegotiable")) {
                        setProductPriceType(productPriceType, dataSnapshot.child("isNegotiable").getValue(Boolean.class));
                    } else {
                        setProductPriceType(productPriceType, Boolean.FALSE);
                    }

                    defaultSwitch(productKey, productCategory, productShortlist);

//                    chatLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            CounterItemFormat counterItemFormat = new CounterItemFormat();
//                            HashMap<String, String> meta = new HashMap<>();
//                            meta.put("type", "fromTextBox");
//                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
//                            counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_OPEN_CHAT);
//                            counterItemFormat.setTimestamp(System.currentTimeMillis());
//                            counterItemFormat.setMeta(meta);
//
//
//                            CounterPush counterPush2 = new CounterPush(counterItemFormat, communityReference);
//                            counterPush2.pushValues();
//
//                            //char room clicked
//                            Intent intent = new Intent(OpenProductDetails.this, ChatActivity.class);
//                            intent.putExtra("type", "storeroom");
//                            intent.putExtra("key", productKey);
//                            intent.putExtra("name", productName.getText());
//                            intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(productKey).toString());
//                            startActivity(intent);
//                            overridePendingTransition(0, 0);
//                        }
//                    });

//                    chatEditText.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //char room clicked
//                            CounterItemFormat counterItemFormat = new CounterItemFormat();
//                            HashMap<String, String> meta = new HashMap<>();
//                            meta.put("type", "fromTextBox");
//                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
//                            counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_OPEN_CHAT);
//                            counterItemFormat.setTimestamp(System.currentTimeMillis());
//                            counterItemFormat.setMeta(meta);
//
//
//                            CounterPush counterPush2 = new CounterPush(counterItemFormat, communityReference);
//                            counterPush2.pushValues();
//
//
//                            Intent intent = new Intent(OpenProductDetails.this, ChatActivity.class);
//                            intent.putExtra("type", "storeroom");
//                            intent.putExtra("key", productKey);
//                            intent.putExtra("name", productName.getText());
//                            intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(productKey).toString());
//                            startActivity(intent);
//                            overridePendingTransition(0, 0);
//                        }
//                    });
                } catch (Exception e) {
                }
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

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta = new HashMap<>();
                meta.put("type", "fromRV");
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_SHORTLIST);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();
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

                                        NotificationSender notificationSender = new NotificationSender(OpenProductDetails.this, userItemFormat.getUserUID());
                                        NotificationItemFormat productShortlistNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_SHORTLIST, userItemFormat.getUserUID(), (String) dataSnapshot.child("PostedBy").child("UID").getValue(),1);
                                        productShortlistNotification.setCommunityName(communityTitle);
                                        productShortlistNotification.setItemKey(productKey);
                                        productShortlistNotification.setItemName(dataSnapshot.child("ProductName").getValue().toString());
                                        productShortlistNotification.setUserName(userItemFormat.getUsername());
                                        productShortlistNotification.setUserMobileNumber(userItemFormat.getMobileNumber());
                                        productShortlistNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                                        productShortlistNotification.setRecieverKey((String) dataSnapshot.child("PostedBy").child("UID").getValue());

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

    public void setProductPrice(TextView productPrice, String productPriceValue) {

        productPrice.setText("₹" + productPriceValue + "/-");

    }

    public void setProductPriceType(TextView productPriceType, Boolean isNegotiable) {
        if (isNegotiable) {
            productPriceType.setVisibility(View.VISIBLE);
        } else {
            productPriceType.setVisibility(View.GONE);
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
                    productShortlist.setTextColor(getApplicationContext().getResources().getColor(R.color.deepPurple500));
                    productShortlist.setTypeface(ralewayBold);
                    productShortlist.setText("Shortlisted");
                } else {

                    productShortlist.setTextColor(getApplicationContext().getResources().getColor(R.color.primaryText));
                    productShortlist.setTypeface(ralewayBold);
                    productShortlist.setText("Shortlist");
                }
                productShortlist.setOnClickListener(mListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}
