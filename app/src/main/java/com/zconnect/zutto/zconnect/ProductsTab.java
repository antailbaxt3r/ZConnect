package com.zconnect.zutto.zconnect;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.addActivities.AddEvent;
import com.zconnect.zutto.zconnect.addActivities.AddProduct;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.Product;
import com.zconnect.zutto.zconnect.adapters.ProductsRVAdapter;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsTab extends Fragment {

    NotificationCompat.Builder mBuilder;
    FirebaseUser user;
    String TotalProducts;
    String userId;
    Query productsQuery;
    DatabaseReference mUserStats, mFeaturesStats;
    private RecyclerView mProductList;
    private DatabaseReference mDatabase;
    private Query query;
    private boolean flag = false;
    private FirebaseAuth mAuth;
    private ProductsRVAdapter productAdapter;
    private Vector<Product> productVector= new Vector<Product>();
    private ValueEventListener mListener;
    private Product singleProduct;
    private Boolean flagNoProductsAvailable;
    private TextView noProductsAvailableText;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    public ProductsTab(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products_tab, container, false);
        GridLayoutManager productGridLayout = new GridLayoutManager(getContext(), 2);
//        LinearLayoutManager productLinearLayout = new LinearLayoutManager(getContext());

//        productLinearLayout.setReverseLayout(true);
//        productLinearLayout.setStackFromEnd(true);

        noProductsAvailableText = (TextView) view.findViewById(R.id.no_products_available_text);
        progressBar = (ProgressBar) view.findViewById(R.id.products_tab_progress_bar);
        mProductList = (RecyclerView) view.findViewById(R.id.productList);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(productGridLayout);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_content_store_room);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();

                meta.put("type","fromFeature");
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_PRODUCT_ADD_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                getContext().startActivity(new Intent(getContext(), AddProduct.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        SharedPreferences communitySP;
        String communityReference;

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        // StoreRoom feature Reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");
        productsQuery = mDatabase.orderByPriority();
        mDatabase.keepSynced(true);

        if(!status){
            user = mAuth.getCurrentUser();

            mUserStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(user.getUid()).child("Stats");
            mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Stats");

            mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TotalProducts = dataSnapshot.child("TotalProducts").getValue().toString();
                    DatabaseReference newPost = mUserStats;
                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put("TotalProducts", TotalProducts);
                    newPost.updateChildren(taskMap);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        productAdapter = new ProductsRVAdapter(productVector,getContext());
        mProductList.setAdapter(productAdapter);



        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productVector.clear();;
                flagNoProductsAvailable = true;
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    try{
                        singleProduct = shot.getValue(Product.class);
                        if(!singleProduct.getKey().equals(null)&& !singleProduct.getProductName().equals(null)) {
                            if (!shot.hasChild("isNegotiable")){
                                if(shot.hasChild("negotiable")){
                                    if(shot.child("negotiable").getValue(Integer.class)==1){
                                        singleProduct.setIsNegotiable(Boolean.TRUE);
                                    }else {
                                        singleProduct.setIsNegotiable(Boolean.FALSE);
                                    }
                                }else {
                                    singleProduct.setIsNegotiable(Boolean.FALSE);
                                }
                            }
                            productVector.add(singleProduct);
                            flagNoProductsAvailable = false;
                        }
                    }
                    catch (Exception e){
                        Log.d("Error Alert", e.getMessage());
                    }
                }

                progressBar.setVisibility(View.INVISIBLE);
                if(flagNoProductsAvailable){
                    noProductsAvailableText.setVisibility(View.VISIBLE);
                }else{
                    noProductsAvailableText.setVisibility(View.GONE);
                }
                Collections.reverse(productVector);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mBuilder = new NotificationCompat.Builder(getContext());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabase.addValueEventListener(mListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatabase.removeEventListener(mListener);
    }

    //    // Each View Holder Class
//    public static class ProductViewHolder extends RecyclerView.ViewHolder {
//
//        public View.OnClickListener mListener;
//        View mView;
//        //Switch View
//        Switch mReserve;
//        TextView ReserveStatus;
//        // Flag Variable to get each Reserve Id
//        String[] keyList;
//        // Flag to get combined user Id
//        String ReservedUid;
//        SharedPreferences sharedPref;
//
//        private SharedPreferences communitySP;
//        public String communityReference;
//
//
//        private DatabaseReference Users;
//        private DatabaseReference StoreRoom;
//        // Auth to get Current User
//        private FirebaseAuth mAuth;
//        private Button shortList;
//
//        private ImageView post_image;
//        private String sellerName;
//        private TextView negotiableText;
//        // Constructor
//        public ProductViewHolder(View itemView) {
//            super(itemView);
//            mView = itemView;
//
//            communitySP = mView.getContext().getSharedPreferences("communityName", MODE_PRIVATE);
//            communityReference = communitySP.getString("communityReference", null);
//
//            Users = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users");
//            StoreRoom = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("storeroom");
//
//            post_image = (ImageView) mView.findViewById(R.id.postImg);
//            sharedPref = mView.getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
//            Boolean status = sharedPref.getBoolean("mode", false);
////            mReserve = (Switch) mView.findViewById(R.id.switch1);
////            ReserveStatus = (TextView) mView.findViewById(R.id.switch1);
//            shortList = (Button) mView.findViewById(R.id.shortList);
//            if(status){
//                shortList.setVisibility(View.GONE);
////                mReserve.setVisibility(View.GONE);
////                ReserveStatus.setVisibility(View.GONE);
//            }
//            StoreRoom.keepSynced(true);
//        }
//
//        public void openProduct(final String key){
//
//
//            mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent= new Intent(mView.getContext(),OpenProductDetails.class);
//                    intent.putExtra("key", key);
//                    mView.getContext().startActivity(intent);
//
//                }
//            });
//
//        }
//        // Setting default switch
//        public void defaultSwitch(final String key, final Context ctx, final String category) {
//            // Getting User ID
//            mAuth = FirebaseAuth.getInstance();
//            FirebaseUser user = mAuth.getCurrentUser();
//            final String userId = user.getUid();
//
//
//            //Getting  data from database
//            StoreRoom.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
////                    mReserve.setOnCheckedChangeListener(null);
//                    shortList.setOnClickListener(null);
//                    if (dataSnapshot.child(key).child("UsersReserved").hasChild(userId)) {
//                        shortList.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton2_sr));
//                        shortList.setText("Shortlisted");
//                        Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//                        shortList.setTypeface(customfont);
//                        //ReserveStatus.setTextColor(ContextCompat.getColor(ctx, R.color.teal600));
//
//                    } else {
//
//                        shortList.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton_sr));
//                        shortList.setText("Shortlist");
//                        Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//                        shortList.setTypeface(customfont);
////                        mReserve.setChecked(false);
////                        mReserve.setText("Shortlist");
////                        ReserveStatus.setTextColor(ContextCompat.getColor(ctx, R.color.black));
//                    }
//                    shortList.setOnClickListener(mListener);
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//
//            });
//        }
//
//        //Set name of product
//        public void setProductName(String productName) {
//            TextView post_name = (TextView) mView.findViewById(R.id.productName);
//            post_name.setText(productName);
//            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
//            post_name.setTypeface(ralewayMedium);
//        }
//
//        //Set Product Description
//        public void setProductDesc(String productDesc) {
//            TextView post_desc = (TextView) mView.findViewById(R.id.productDescription);
//            post_desc.setText(productDesc);
//            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//            post_desc.setTypeface(ralewayMedium);
//        }
//
//        public void animate(final Activity activity, final String name, String url) {
//            final Intent i = new Intent(mView.getContext(), viewImage.class);
//            i.putExtra("currentEvent", name);
//            i.putExtra("eventImage", url);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, post_image, mView.getResources().getString(R.string.transition_string));
//
//            mView.getContext().startActivity(i, optionsCompat.toBundle());
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
//        //Set Product Imag
//
//        //Set Product Price
//        public void setPrice(String productPrice,String negotiable) {
//            TextView post_price = (TextView) mView.findViewById(R.id.price);
//            negotiableText = (TextView) mView.findViewById(R.id.negotiable);
//            String price="";
//            if(negotiable!=null) {
//                if (negotiable.equals("1")) {
//                    price = "₹" + productPrice + "/-";
//                    negotiableText.setVisibility(View.VISIBLE);
//                } else if (negotiable.equals("2")){
//                    price = "Price Negotiable";
//                    post_price.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
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
//        }
//
//        public void setSellerName(String postedBy) {
//            Users.child(postedBy).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    TextView post_seller_name = (TextView) mView.findViewById(R.id.sellerName);
//
//                    if (dataSnapshot.child("Username").getValue()!=null) {
//                        sellerName = dataSnapshot.child("Username").getValue().toString();
//                        post_seller_name.setText("Sold By: " + sellerName);
//                    }else {
//                        post_seller_name.setText("");
//                    }
//                    Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//                    post_seller_name.setTypeface(ralewayMedium);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//
//        public void setSellerNumber(final String category, final String sellerNumber, final Context ctx) {
//            Button post_seller_number = (Button) mView.findViewById(R.id.sellerNumber);
//            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//            post_seller_number.setTypeface(customfont);
//            post_seller_number.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ctx.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(sellerNumber.trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                }
//            });
//        }
//    }
}

