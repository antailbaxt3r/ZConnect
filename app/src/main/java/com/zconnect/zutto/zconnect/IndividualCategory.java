package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.Product;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("storeroom");
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
                viewHolder.setProductDesc(model.getProductDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setProductPrice(model.getPrice());
                viewHolder.setSellerName(model.getPostedBy());
                viewHolder.setSellerNumber(model.getPhone_no(), category);


                SharedPreferences sharedPref = getSharedPreferences("guestMode",MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);
                if(!status) {
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
                                        if (dataSnapshot.child(model.getKey()).child("UsersReserved").hasChild(mAuth.getCurrentUser().getUid())) {
                                            mDatabase.child(model.getKey()).child("UsersReserved").child(mAuth.getCurrentUser().getUid()).removeValue();
                                            viewHolder.shortList.setText("Shortlisted");
                                            flag = false;
                                            viewHolder.shortList.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curvedradiusbutton2_sr));
                                            Typeface customfont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Light.ttf");
                                            viewHolder.shortList.setTypeface(customfont);
                                        } else {
                                            viewHolder.shortList.setText("Shortlist");
                                            mDatabase.child(model.getKey()).child("UsersReserved")
                                                    .child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                            viewHolder.shortList.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curvedradiusbutton_sr));
                                            flag = false;
                                            Typeface customfont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Light.ttf");
                                            viewHolder.shortList.setTypeface(customfont);
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

        public View.OnClickListener mListener;
        View mView;
        String[] keyList;
        String ReservedUid;
        SharedPreferences sharedPref;
        private Switch mReserve;
        private TextView ReserveStatus;
        private DatabaseReference StoreRoom = FirebaseDatabase.getInstance().getReference().child("storeroom");
        private DatabaseReference Users = FirebaseDatabase.getInstance().getReference().child("Users");
        private FirebaseAuth mAuth;
        private Button shortList;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            sharedPref = mView.getContext().getSharedPreferences("guestMode",Context.MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
//            mReserve = (Switch) mView.findViewById(R.id.switch1);
//            ReserveStatus = (TextView) mView.findViewById(R.id.switch1);
            shortList = (Button) mView.findViewById(R.id.shortList);

            if(status){
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
            final String userId = user.getUid();

            //Getting  data from database
            StoreRoom.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    mReserve.setOnCheckedChangeListener(null);
                    shortList.setOnClickListener(null);
                    if (dataSnapshot.child(key).child("UsersReserved").hasChild(userId)) {
                        CounterManager.StoroomShortList(category);
                        shortList.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton2_sr));
                        shortList.setText("Shortlisted");
                        Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
                        shortList.setTypeface(customfont);
//                        Reserve.setChecked(true);
//                        mReserve.setText("Shortlisted");
                        shortList.setText("Shortlisted");
                    } else {
                        CounterManager.StoroomShortListDelete(category);
                        shortList.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton_sr));
                        shortList.setText("Shortlist");
                        Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
                        shortList.setTypeface(customfont);
//                        mReserve.setChecked(false);
//                        mReserve.setText("Shortlist");

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

        public void setProductDesc(String productDesc) {

            TextView post_desc = (TextView) mView.findViewById(R.id.productDescription);
            post_desc.setText(productDesc);
            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            post_desc.setTypeface(ralewayMedium);

        }


        public void setImage(Context ctx, String image) {

            ImageView post_image = (ImageView) mView.findViewById(R.id.postImg);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setProductPrice(String productPrice) {
            TextView post_price = (TextView) mView.findViewById(R.id.price);
            post_price.setText("₹" + productPrice + "/-");
            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
            post_price.setTypeface(ralewayMedium);
        }


        public void setSellerName(String postedBy) {


            Users.child(postedBy).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String sellerName = dataSnapshot.child("Username").getValue().toString();
                    TextView post_seller_name = (TextView) mView.findViewById(R.id.sellerName);
                    post_seller_name.setText("Sold By: " + sellerName);
                    Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
                    post_seller_name.setTypeface(ralewayMedium);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setSellerNumber(final String sellerNumber, final String category) {
            Button post_seller_number = (Button) mView.findViewById(R.id.sellerNumber);
            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
            post_seller_number.setTypeface(customfont);
            post_seller_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CounterManager.StoroomCall(category);
                    mView.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(sellerNumber.trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });

        }


    }

}


