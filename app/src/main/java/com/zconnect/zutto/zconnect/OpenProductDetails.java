package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.UsersListItemFormat;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_PRODUCT;

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

        productImage = (ImageView) findViewById(R.id.product_image);
        productName = (TextView) findViewById(R.id.product_name);
        productPrice = (TextView) findViewById(R.id.product_price);
        productPriceType = (TextView) findViewById(R.id.product_price_type);
        productDescription = (TextView) findViewById(R.id.product_description);
        productSellerName = (TextView) findViewById(R.id.product_seller_name);
        productShortlist = (Button) findViewById(R.id.product_shortlist);
        progressBar = (ProgressBar) findViewById(R.id.product_loading);
        productContent = (LinearLayout) findViewById(R.id.product_content);

        Typeface customfont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        productShortlist.setTypeface(customfont);
        productCall = (Button) findViewById(R.id.product_call);

        chatLayout= (LinearLayout) findViewById(R.id.chatLayout);
        chatEditText = (EditText) findViewById(R.id.typer);
        chatEditText.setShowSoftInputOnFocus(false);

        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //char room clicked
                Intent intent = new Intent(OpenProductDetails.this, ChatActivity.class);
                intent.putExtra("type","storeroom");
                intent.putExtra("key",productKey);
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
                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(productKey).toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        mDatabaseProduct = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");
        Intent intent = getIntent();
        productKey = intent.getStringExtra("key");

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
        switch (item.getItemId()) {
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

    @Override
    protected void onStart() {
        super.onStart();


        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        final String productKey = intent.getStringExtra("key");

        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                final String category = productCategory;
                final DatabaseReference userReservedReference = mDatabaseProduct.child(productKey);
                userReservedReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (flag) {

                            if (dataSnapshot.child("UsersReserved").hasChild(mAuth.getCurrentUser().getUid())) {
                                userReservedReference.child("UsersReserved").child(mAuth.getCurrentUser().getUid()).removeValue();
                                productShortlist.setText("Shortlist");
                                flag = false;
                                CounterManager.StoroomShortListDelete(category, dataSnapshot.getKey());
                                productShortlist.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curvedradiusbutton_sr));
                                Typeface customfont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
                                productShortlist.setTypeface(customfont);

                            } else {
                                CounterManager.StoroomShortList(category, dataSnapshot.getKey());
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
                                        NotificationSender notificationSender=new NotificationSender(dataSnapshot.child("PostedBy").child("UID").getValue().toString(),null,null,null,null,userDetails.getUserUID(),productName.getText().toString(),KEY_PRODUCT,false,true,getApplicationContext());
                                        notificationSender.execute();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                productShortlist.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curvedradiusbutton2_sr));
                                flag = false;
                                Typeface customfont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
                                productShortlist.setTypeface(customfont);
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


//        Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
//        post_price.setTypeface(ralewayMedium);
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
                mProgress.setMessage("Loading.....");
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
                    productShortlist.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curvedradiusbutton2_sr));
                    productShortlist.setText("Shortlisted");
                    CounterManager.StoroomShortList(category, key);
                    Typeface customfont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
                    productShortlist.setTypeface(customfont);
                    //ReserveStatus.setTextColor(ContextCompat.getColor(ctx, R.color.teal600));

                } else {

                    productShortlist.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curvedradiusbutton_sr));
                    productShortlist.setText("Shortlist");
                    Typeface customfont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
                    productShortlist.setTypeface(customfont);
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
