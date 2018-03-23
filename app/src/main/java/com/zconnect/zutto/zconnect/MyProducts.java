package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.Product;

public class MyProducts extends BaseActivity {
    Query query;
    //private DatabaseReference mReservedProducts;
    private DatabaseReference mDatabase;

    private RecyclerView mProductList;
    //    private List<String> reserveList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CounterManager.StoreRoomMyProductOpen();
        setContentView(R.layout.activity_my_products);
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

        mProductList = (RecyclerView) findViewById(R.id.productList);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(new LinearLayoutManager(MyProducts.this));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("storeroom");


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        query = mDatabase.orderByChild("PostedBy").equalTo(userId);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.my_product_row,
                ProductViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final MyProducts.ProductViewHolder viewHolder, Product model, int position) {

                final String product_key = getRef(position).getKey();

//               if(reserveList.contains(model.getKey())) {
                viewHolder.setProductName(model.getProductName());
                viewHolder.setProductDesc(model.getProductDescription());
                viewHolder.setPrice(model.getPrice(),model.getNegotiable());
                viewHolder.setIntent(model.getKey());
                viewHolder.setImage(getApplicationContext(), model.getImage());
//                }else {
//               }
                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CounterManager.StoreRoomMyProductDelete();
                        viewHolder.ReserveReference = FirebaseDatabase.getInstance().getReference().child("storeroom/" + product_key);
                        viewHolder.ReserveReference.getRef().removeValue();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(product_key);
                    }
                });

            }
        };
        mProductList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private DatabaseReference ReserveReference;
        //        private Switch mReserve;
//        private TextView ReserveStatus;
        private Button deleteButton;
//        private FirebaseAuth mAuth;
//        String [] keyList;
//        String ReservedUid;

        public ProductViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            //to delete reserved items
            deleteButton = (Button) mView.findViewById(R.id.delete);
        }

        public void setProductName(String productName) {

            TextView post_name = (TextView) mView.findViewById(R.id.productName);
            post_name.setText(productName);
            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
            post_name.setTypeface(ralewayMedium);

        }

        public void setIntent(final String key) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(),Shortlisted.class).putExtra("Key",key));
                }
            });
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

        //Set Product Price
        public void setPrice(String productPrice,String negotiable) {
            TextView post_price = (TextView) mView.findViewById(R.id.price);
            String price="";
            if(negotiable!=null) {
                if (negotiable.equals("1")) {
                    price = "₹" + productPrice + "/-";
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


    }

}
