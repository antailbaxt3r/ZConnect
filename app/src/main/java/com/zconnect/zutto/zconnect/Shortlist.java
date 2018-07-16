package com.zconnect.zutto.zconnect;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.Product;
import com.zconnect.zutto.zconnect.adapters.ProductsRVAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Shortlist extends BaseActivity {

    String reserveString;
    Query query;
    private DatabaseReference mReservedProducts;
    private DatabaseReference mDatabase;
    private RecyclerView mProductList;
    private List<String> reserveList;
    private FirebaseAuth mAuth;
    private NotificationCompat.Builder mBuilder;
    private TextView errorMessage;
    private TextView noitems;

    Toolbar mActionBarToolbar;
    private SharedPreferences communitySP;
    public String communityReference;

    private ProductsRVAdapter productAdapter;
    private Vector<Product> productVector= new Vector<Product>();
    private ValueEventListener mListener;
    private Product singleProduct;

    public Shortlist() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_reserved_tab);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(mActionBarToolbar);

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
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



        GridLayoutManager productGridLayout = new GridLayoutManager(this, 2);
        noitems = (TextView) findViewById(R.id.noitems);

        mProductList = (RecyclerView) findViewById(R.id.reservedProductList);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(productGridLayout);
        productAdapter = new ProductsRVAdapter(productVector,this);
        mProductList.setAdapter(productAdapter);

        communitySP = getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);


        mReservedProducts = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productVector.clear();;
                Boolean flag = false;
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    try{
                        if(shot.child("UsersReserved").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            singleProduct = shot.getValue(Product.class);
                            productVector.add(singleProduct);
                            flag=true;
                        }
                    }
                    catch (Exception e){
                        Log.d("Error Alert", e.getMessage());
                    }
                }
                if (flag){
                    noitems.setVisibility(View.GONE);
                } else {
                    noitems.setVisibility(View.VISIBLE);
                }

                Collections.reverse(productVector);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabase.addValueEventListener(mListener);

//        FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
//                Product.class,
//                R.layout.reserved_products_row,
//                ProductViewHolder.class,
//                query
//        ) {
//            @Override
//            protected void populateViewHolder(final ProductViewHolder viewHolder, final Product model, int position) {
//
//                final String product_key = getRef(position).getKey();
//
////               if(reserveList.contains(model.getKey())) {
//                viewHolder.setProductName(model.getProductName());
////                viewHolder.setProductDesc(model.getProductDescription());
//                viewHolder.setImage(getActivity(), model.getProductName(), getContext(), model.getImage());
//                viewHolder.setProductPrice(model.getPrice(),model.getNegotiable());
////                viewHolder.setSellerName(model.getPostedBy().getUsername());
////                viewHolder.setSellerNumber(model.getPhone_no(), getContext(), model.getCategory());
////                }else {
////               }
//                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        CounterManager.StoroomShortListDelete(model.getCategory(), model.getKey());
//                        viewHolder.ReserveReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products").child(product_key + "/UsersReserved");
//
//                        mAuth = FirebaseAuth.getInstance();
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        final String userId = user.getUid();
//                        viewHolder.ReserveReference.child(userId).removeValue();
//                    }
//                });
//            }
//
//
//        };
//        mProductList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatabase.removeEventListener(mListener);
    }
//    public static class ProductViewHolder extends RecyclerView.ViewHolder {
//
//
//        View mView;
//        String[] keyList;
//        String ReservedUid;
//        private DatabaseReference ReserveReference;
//        private Switch mReserve;
//        private TextView ReserveStatus;
//        private ImageView deleteButton;
//        private FirebaseAuth mAuth;
//        private String sellerName;
//        private SharedPreferences communitySP;
//        public String communityReference;
//
//        private DatabaseReference Users;
//        private ImageView post_image;
//        private TextView negotiableText;
//
//
//        public ProductViewHolder(View itemView) {
//            super(itemView);
//            mView = itemView;
//            //to delete reserved items
////            noitems.setVisibility(View.VISIBLE);
//
//            communitySP = mView.getContext().getSharedPreferences("communityName", MODE_PRIVATE);
//            communityReference = communitySP.getString("communityReference", null);
//
//            Users = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
//
//            post_image = (ImageView) mView.findViewById(R.id.postImg);
//            deleteButton = (ImageView) mView.findViewById(R.id.archive);
//        }
//
//        public void setProductName(String productName) {
//
//            TextView post_name = (TextView) mView.findViewById(R.id.productName);
//            post_name.setText(productName);
//            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
//            post_name.setTypeface(customfont);
//
//        }
//
////        public void setProductDesc(String productDesc) {
////
////            TextView post_desc = (TextView) mView.findViewById(R.id.productDescription);
////            post_desc.setText(productDesc);
////            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
////            post_desc.setTypeface(customfont);
////
////        }
//
//        public void animate(final Activity activity, final String name, String url) {
//            final Intent i = new Intent(mView.getContext(), viewImage.class);
//            i.putExtra("currentEvent", name);
//            i.putExtra("eventImage", url);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, post_image, mView.getResources().getString(R.string.transition_string));
//
//            mView.getContext().startActivity(i, optionsCompat.toBundle());
//
//
//        }
//
//        public void setImage(final Activity activity, final String name, final Context ctx, final String image) {
//            Picasso.with(ctx).load(image).into(post_image);
//            post_image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    ProgressDialog mProgress = new ProgressDialog(ctx);
//                    mProgress.setMessage("Loading.....");
//                    mProgress.show();
//                    animate(activity, name, image);
//                    mProgress.dismiss();
//                }
//            });
//        }
//
//        public void setProductPrice(String productPrice,String negotiable) {
//
//            TextView post_price = (TextView) mView.findViewById(R.id.price);
//            negotiableText = (TextView) mView.findViewById(R.id.negotiable);
//            String price="";
//            if(negotiable!=null) {
//                if (negotiable.equals("1")) {
//                    price = "₹" + productPrice + "/-";
//                    negotiableText.setVisibility(View.VISIBLE);
//                } else if (negotiable.equals("2")){
//                    price = "Price Negotiable";
//                    post_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                }
//                else
//                    price = "₹" + productPrice + "/-";
//
//                post_price.setText(price);
//            }
//            else
//            {
//                post_price.setText("₹" + productPrice + "/-");
//            }
//            //"₹" + productPrice + "/-"
//            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
//            post_price.setTypeface(ralewayMedium);
//
//        }
//
////        public void setSellerName(String username) {
////            sellerName = username;
////            TextView post_seller_name = (TextView) mView.findViewById(R.id.sellerName);
////            post_seller_name.setText("Sold By: " + sellerName);
////        }
//
////        public void setSellerNumber(final String sellerNumber, final Context ctx, final String category) {
////            Button post_seller_number = (Button) mView.findViewById(R.id.sellerNumber);
////            post_seller_number.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    CounterManager.StoroomCall(category);
////                    ctx.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(sellerNumber.trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
////                }
////            });
////            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
////            post_seller_number.setTypeface(customfont);
////
////        }
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent eventsIntent=new Intent(OpenEventDetail.this,TrendingEvents.class);
//        startActivity(eventsIntent);
        finish();
    }
}
