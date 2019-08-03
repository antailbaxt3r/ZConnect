package com.zconnect.zutto.zconnect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
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

        openAddProduct = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isNetworkAvailable(MyProducts.this)) {
                    Snackbar snack = Snackbar.make(noProductRL, "No internet. Please try again later.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimaryDark));
                    snack.show();
                } else {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyProducts.this);
                    alertBuilder.setTitle("Sell/Ask")
                            .setMessage("Do you want to Sell a product or ask for a product?")
                            .setPositiveButton("Sell", new DialogInterface.OnClickListener() {
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
            }
        };

        noProductRL = (RelativeLayout) findViewById(R.id.no_product_relative_layout);
        noProductTextView = (TextView) findViewById(R.id.no_products_available_text);

        noProductRL.setVisibility(View.VISIBLE);
        noProductTextView.setVisibility(View.VISIBLE);
        noProductTextView.setOnClickListener(openAddProduct);

        GridLayoutManager productGrid = new GridLayoutManager(this,2);

        mProductList = (RecyclerView) findViewById(R.id.productList);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(productGrid);

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

                    Dialog removeProductDialog = new Dialog(itemView.getContext());
                    removeProductDialog.setContentView(R.layout.new_dialog_box);
                    removeProductDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    removeProductDialog.findViewById(R.id.dialog_box_image_sdv).setBackground(ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_message_white_24dp));
                    TextView heading =  removeProductDialog.findViewById(R.id.dialog_box_heading);
                    heading.setText("Delete product");
                    TextView body = removeProductDialog.findViewById(R.id.dialog_box_body);
                    body.setText("Are you sure you want to delete?");
                    Button positiveButton = removeProductDialog.findViewById(R.id.dialog_box_positive_button);
                    positiveButton.setText("Confirm");
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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

                            removeProductDialog.dismiss();

                        }
                    });
                    Button negativeButton = removeProductDialog.findViewById(R.id.dialog_box_negative_button);
                    negativeButton.setText("Skip");
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeProductDialog.dismiss();
                        }
                    });

                    removeProductDialog.show();





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
                    Intent intent = new Intent(itemView.getContext(),ShortlistedPeopleList.class).putExtra("Key",key);
                    itemView.getContext().startActivity(intent);
                }
            });
        }


        public void setImage(Context ctx, String image) {


            ImageView post_image = (ImageView) mView.findViewById(R.id.postImg);
            Picasso.with(ctx).load(image).into(post_image);


        }

        //Set Product Price
        public void setPrice(String productPrice) {
            TextView post_price = (TextView) mView.findViewById(R.id.price);
                if(productPrice!=null)
                    post_price.setText("₹" + productPrice + "/-");
                else
                    post_price.setText("ASKED");

            //"₹" + productPrice + "/-"
            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
            post_price.setTypeface(ralewayMedium);
        }


    }

}
