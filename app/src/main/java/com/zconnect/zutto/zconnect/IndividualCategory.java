package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.Product;

import org.json.JSONObject;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.zconnect.zutto.zconnect.KeyHelper.KEY_PRODUCT;

public class IndividualCategory extends BaseActivity {

    public String category;
    Query queryCategory;
    private RecyclerView mProductList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private LinearLayoutManager linearLayoutManager;
    private boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            category = intent.getStringExtra("Category");
            getSupportActionBar().setTitle(category);
        }


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

        mProductList = (RecyclerView) findViewById(R.id.productList);
        mProductList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mProductList.setLayoutManager(linearLayoutManager);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");
        queryCategory = mDatabase.orderByChild("Category").equalTo(category);
        mDatabase.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(

                Product.class,
                R.layout.products_row,
                ProductViewHolder.class,
                queryCategory
        ) {

            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Product model, int position) {

                viewHolder.setProductName(model.getProductName());
                //viewHolder.setProductDesc(model.getProductDescription());
                viewHolder.setImage(IndividualCategory.this, model.getProductName(), IndividualCategory.this, model.getImage());
                viewHolder.setPrice(model.getPrice(),model.getNegotiable());
//                viewHolder.setSellerName(model.getPostedBy().getUsername());
//                viewHolder.setSellerNumber(model.getPhone_no(), category);


                SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);
                if (!status) {
                    viewHolder.defaultSwitch(model.getKey(), model.getCategory());
//                    viewHolder.mListener = new CompoundButton.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                            flag = true;
//
//                            mDatabase.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                    if (flag) {
//                                        if (dataSnapshot.child(model.getKey()).child("UsersReserved").hasChild(mAuth.getCurrentUser().getUid())) {
//                                            mDatabase.child(model.getKey()).child("UsersReserved").child(mAuth.getCurrentUser().getUid()).removeValue();
//                                            viewHolder.mReserve.setText("Shortlisted");
//                                            flag = false;
//                                            viewHolder.ReserveStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.teal600));
//
//                                        } else {
//
//                                            mDatabase.child(model.getKey()).child("UsersReserved").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
//                                            viewHolder.ReserveStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
//                                            viewHolder.mReserve.setText("Shortlist");
//                                            flag = false;
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                    };
                    viewHolder.mListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            flag = true;
                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (flag) {
                                        CounterManager.StoroomShortListDelete(category, model.getKey());

                                        if (dataSnapshot.child(model.getKey()).child("UsersReserved").hasChild(mAuth.getCurrentUser().getUid())) {
                                            mDatabase.child(model.getKey()).child("UsersReserved").child(mAuth.getCurrentUser().getUid()).removeValue();
                                            viewHolder.shortList.setImageResource(R.drawable.ic_bookmark_white_24dp);
                                            flag = false;

                                        } else {
                                            CounterManager.StoroomShortList(category, model.getKey());
                                            mDatabase.child(model.getKey()).child("UsersReserved")
                                                    .child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                            viewHolder.shortList.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                                            flag = false;

                                            NotificationSender notificationSender=new NotificationSender(model.getKey(),null,null,null,null,null,model.getProductName(),KEY_PRODUCT,false,true,getApplicationContext());
                                            notificationSender.execute();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                    };
                    viewHolder.shortList.setOnClickListener(viewHolder.mListener);
//                    viewHolder.mReserve.setOnCheckedChangeListener(viewHolder.mListener);


                }
            }
        };
        mProductList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private SharedPreferences communitySP;
        public String communityReference;


        public View.OnClickListener mListener;
        View mView;
        String[] keyList;
        String ReservedUid;
        SharedPreferences sharedPref;
        private Switch mReserve;
        private TextView ReserveStatus;
        private DatabaseReference StoreRoom;
        private DatabaseReference Users;
        private FirebaseAuth mAuth;
        private ImageView shortList;
        private ImageView post_image;
        private TextView negotiableText;


        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            sharedPref = mView.getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            post_image = (ImageView) mView.findViewById(R.id.postImg);
//            mReserve = (Switch) mView.findViewById(R.id.switch1);
//            ReserveStatus = (TextView) mView.findViewById(R.id.switch1);
            shortList = (ImageView) mView.findViewById(R.id.shortList);

            communitySP = mView.getContext().getSharedPreferences("communityName", MODE_PRIVATE);
            communityReference = communitySP.getString("communityReference", null);

            StoreRoom = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");
            Users = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");

            if (status) {
                shortList.setVisibility(View.GONE);
//                mReserve.setVisibility(View.GONE);
//                ReserveStatus.setVisibility(View.GONE);
            }

            StoreRoom.keepSynced(true);

        }

        public void defaultSwitch(final String key, final String category) {
            // Getting User ID
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null)
                return;
            final String userId = user.getUid();

            //Getting  data from database
            StoreRoom.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    mReserve.setOnCheckedChangeListener(null);
                    shortList.setOnClickListener(null);
                    if (dataSnapshot.child(key).child("UsersReserved").hasChild(userId)) {
                        shortList.setImageResource(R.drawable.ic_bookmark_white_24dp);
                    } else {
                        shortList.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                    }
                    shortList.setOnClickListener(mListener);
//                    mReserve.setOnCheckedChangeListener(mListener);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });


        }

        public void setProductName(String productName) {

            TextView post_name = (TextView) mView.findViewById(R.id.productName);
            post_name.setText(productName);
            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
            post_name.setTypeface(ralewayMedium);

        }

//        public void setProductDesc(String productDesc) {
//
//            TextView post_desc = (TextView) mView.findViewById(R.id.productDescription);
//            post_desc.setText(productDesc);
//            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//            post_desc.setTypeface(ralewayMedium);
//
//        }


        public void animate(final Activity activity, final String name, String url) {
            final Intent i = new Intent(mView.getContext(), viewImage.class);
            i.putExtra("currentEvent", name);
            i.putExtra("eventImage", url);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, post_image, mView.getResources().getString(R.string.transition_string));

            mView.getContext().startActivity(i, optionsCompat.toBundle());


        }

        public void setImage(final Activity activity, final String name, final Context ctx, final String image) {
            Picasso.with(ctx).load(image).into(post_image);
            post_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ProgressDialog mProgress = new ProgressDialog(ctx);
                    mProgress.setMessage("Loading.....");
                    mProgress.show();
                    animate(activity, name, image);
                    mProgress.dismiss();
                }
            });
        }
        public void setPrice(String productPrice,String negotiable) {
            TextView post_price = (TextView) mView.findViewById(R.id.price);
            negotiableText = (TextView) mView.findViewById(R.id.negotiable);
            String price="";
            if(negotiable!=null) {
                if (negotiable.equals("1")) {
                    price = "₹" + productPrice + "/-";
                    negotiableText.setVisibility(View.VISIBLE);
                } else if (negotiable.equals("2")) {
                    price = "Price Negotiable";
                    post_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
                else
                    price = "₹" + productPrice + "/-";

                post_price.setText(price);
            }
            else
            {
                post_price.setText("₹" + productPrice + "/-");
            }
            //"₹" + productPrice + "/-"
            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
            post_price.setTypeface(ralewayMedium);
        }


//        public void setSellerName(String postedBy) {
//
//
//            Users.child(postedBy).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    String sellerName = dataSnapshot.child("Username").getValue().toString();
//                    TextView post_seller_name = (TextView) mView.findViewById(R.id.sellerName);
//                    post_seller_name.setText("Sold By: " + sellerName);
//                    Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//                    post_seller_name.setTypeface(ralewayMedium);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//
//        }

//        public void setSellerNumber(final String sellerNumber, final String category) {
//            Button post_seller_number = (Button) mView.findViewById(R.id.sellerNumber);
//            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//            post_seller_number.setTypeface(customfont);
//            post_seller_number.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    CounterManager.StoroomCall(category);
//                    mView.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(sellerNumber.trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                }
//            });
//
//        }


    }

    public static class sendNotification extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                final String key = params[0];

                RemoteMessage.Builder creator = new RemoteMessage.Builder(key);
                creator.addData("Type", "Shortlist");
                creator.addData("PersonName", user.getDisplayName());
                creator.addData("PersonEmail", user.getEmail());
                creator.addData("key", key);
                creator.addData("Product", params[1]);

                try {
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "key=AAAAGZIFvsE:APA91bG7rY-RLe6T3JxhFcmA4iRtihJCbD2RUwypt0aC8hVCvrm99LKZR__y3SqSIQmJocsuLaDltTuUui9BUrLwAM0SiCx0qSTrO8dpmxnjiHkaATnfYwVIN3T81lwlxYwBF7x9_3Kd");
                    connection.setDoOutput(true);
                    connection.connect();


                    OutputStream os = connection.getOutputStream();
                    OutputStreamWriter writer = new OutputStreamWriter(os);


                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("to", "/topics/" + key);

                    data.put("data", creator.build().getData());

                    JSONObject object = new JSONObject(data);
                    String s2 = object.toString().replace("\\", "");

                    writer.write(s2);
                    writer.flush();

                    Log.d("data", connection.getResponseMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

}


