package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
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

public class IndividualCategory extends AppCompatActivity {

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
        linearLayoutManager.setReverseLayout(false);
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
                viewHolder.defaultSwitch(model.getKey());
                //viewHolder.setSwitch(model.getKey());
                viewHolder.setProductName(model.getProductName());
                viewHolder.setProductDesc(model.getProductDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setProductPrice(model.getPrice());
                viewHolder.setSellerName(model.getPostedBy());
                viewHolder.setSellerNumber(model.getPhone_no());
                viewHolder.defaultSwitch(model.getKey());

                viewHolder.mListener = new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        flag = true;

                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (flag) {
                                    if (dataSnapshot.child(model.getKey()).child("UsersReserved").hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabase.child(model.getKey()).child("UsersReserved").child(mAuth.getCurrentUser().getUid()).removeValue();
                                        viewHolder.mReserve.setText("Shortlisted");
                                        flag = false;
                                        viewHolder.ReserveStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.teal600));

                                    } else {

                                        mDatabase.child(model.getKey()).child("UsersReserved").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        viewHolder.ReserveStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                                        viewHolder.mReserve.setText("Shortlist");
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
                viewHolder.mReserve.setOnCheckedChangeListener(viewHolder.mListener);

            }
        };
        mProductList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public CompoundButton.OnCheckedChangeListener mListener;
        View mView;
        String[] keyList;
        String ReservedUid;
        private Switch mReserve;
        private TextView ReserveStatus;
        private DatabaseReference StoreRoom = FirebaseDatabase.getInstance().getReference().child("storeroom");
        private DatabaseReference Users = FirebaseDatabase.getInstance().getReference().child("Users");
        private FirebaseAuth mAuth;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mReserve = (Switch) mView.findViewById(R.id.switch1);
            ReserveStatus = (TextView) mView.findViewById(R.id.switch1);
            StoreRoom.keepSynced(true);

        }

        public void defaultSwitch(final String key) {
            // Getting User ID
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            final String userId = user.getUid();

            //Getting  data from database
            StoreRoom.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mReserve.setOnCheckedChangeListener(null);
                    if (dataSnapshot.child(key).child("UsersReserved").hasChild(userId)) {
                        mReserve.setChecked(true);
                        mReserve.setText("Shortlisted");
                    } else {
                        mReserve.setChecked(false);
                        mReserve.setText("Shortlist");

                    }
                    mReserve.setOnCheckedChangeListener(mListener);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });


        }

        public void setProductName(String productName) {

            TextView post_name = (TextView) mView.findViewById(R.id.productName);
            post_name.setText(productName);

        }

        public void setProductDesc(String productDesc) {

            TextView post_desc = (TextView) mView.findViewById(R.id.productDescription);
            post_desc.setText(productDesc);

        }


        public void setImage(Context ctx, String image) {

            ImageView post_image = (ImageView) mView.findViewById(R.id.postImg);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setProductPrice(String productPrice) {
            TextView post_name = (TextView) mView.findViewById(R.id.price);
            post_name.setText(productPrice);
        }


        public void setSellerName(String postedBy) {


            Users.child(postedBy).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String sellerName = dataSnapshot.child("Username").getValue().toString();
                    TextView post_seller_name = (TextView) mView.findViewById(R.id.sellerName);
                    post_seller_name.setText("Sold By: " + sellerName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setSellerNumber(final String sellerNumber) {
            ImageView post_seller_number = (ImageView) mView.findViewById(R.id.sellerNumber);

            post_seller_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mView.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(sellerNumber.trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });

        }


    }

}


