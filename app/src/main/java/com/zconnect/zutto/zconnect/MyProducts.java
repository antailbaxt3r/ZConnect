package com.zconnect.zutto.zconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.addActivities.AddProduct;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.Product;
import com.zconnect.zutto.zconnect.utilities.ProductUtilities;

public class MyProducts extends BaseActivity {
    Query query;
    //private DatabaseReference mReservedProducts;
    private DatabaseReference mDatabase;

    private RecyclerView mProductList;
    //    private List<String> reserveList;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerOptions<Product> options;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RelativeLayout noProductRL;
    private TextView noProductTextView;
    private Boolean flag = false;
    private View.OnClickListener openAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        openAddProduct = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyProducts.this);
                alertBuilder.setTitle("Add/Ask")
                        .setMessage("Do you want to add a product or ask for a product?")
                        .setPositiveButton("Ask", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MyProducts.this, AddProduct.class);
                                intent.putExtra("type", ProductUtilities.TYPE_ASK_STR);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MyProducts.this, AddProduct.class);
                                intent.putExtra("type", ProductUtilities.TYPE_ADD_STR);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        };

        noProductRL = (RelativeLayout) findViewById(R.id.no_product_relative_layout);
        noProductTextView = (TextView) findViewById(R.id.no_products_available_text);

        noProductRL.setVisibility(View.VISIBLE);
        noProductTextView.setVisibility(View.VISIBLE);
        noProductTextView.setOnClickListener(openAddProduct);

        mProductList = (RecyclerView) findViewById(R.id.productList);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(new LinearLayoutManager(MyProducts.this));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        query = mDatabase.orderByChild("userID").equalTo(userId);
        options = new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_product_row, parent, false);

                return new ProductViewHolder(view);
//                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                noProductRL.setVisibility(View.GONE);
                noProductTextView.setVisibility(View.GONE);
                noProductRL.setOnClickListener(null);

                final String product_key = getRef(position).getKey();
                holder.setProductName(model.getProductName());
                holder.setProductDesc(model.getProductDescription());
                holder.setPrice(model.getPrice());
                holder.setIntent(model.getKey());
                holder.setImage(getApplicationContext(), model.getImage());
                holder.setArchiveButton(product_key, model.getProductName());
            }
        };

        mProductList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Boolean flag = false;
        private DatabaseReference ReserveReference;
        //        private Switch mReserve;
//        private TextView ReserveStatus;
        private Button archiveButton;
//        private FirebaseAuth mAuth;
//        String [] keyList;
//        String ReservedUid;

        public ProductViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;



            archiveButton = (Button) mView.findViewById(R.id.archive);
        }

        public void setArchiveButton(final String product_key, final String productName){

            FirebaseMessaging.getInstance().unsubscribeFromTopic(product_key);
            ReserveReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(product_key);

            archiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("Are you sure you want to delete " + productName)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    flag=false;
                                    ReserveReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(!flag) {
                                                FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("archiveProducts").child(product_key).setValue(dataSnapshot.getValue());
                                                flag= true;
                                                ReserveReference.removeValue();
                                                FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(product_key).removeValue();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    final android.app.AlertDialog dialog = builder.create();

                    dialog.setCancelable(false);
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(itemView.getContext().getResources().getColor(R.color.colorHighlight));

                }
            });
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
                    itemView.getContext().startActivity(new Intent(itemView.getContext(),ShortlistedPeopleList.class).putExtra("Key",key));
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
        public void setPrice(String productPrice) {
            TextView post_price = (TextView) mView.findViewById(R.id.price);

                post_price.setText("₹" + productPrice + "/-");

            //"₹" + productPrice + "/-"
            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
            post_price.setTypeface(ralewayMedium);
        }


    }

}
