package com.zconnect.zutto.zconnect;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsTab extends Fragment {

    NotificationCompat.Builder mBuilder;
    FirebaseUser user;
    String TotalProducts;
    String userId;
    DatabaseReference mUserStats, mFeaturesStats;
    private RecyclerView mProductList;
    private DatabaseReference mDatabase;
    private Query query;
    private boolean flag = false;
    private FirebaseAuth mAuth;

    public ProductsTab(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products_tab, container, false);
        GridLayoutManager productGridLayout = new GridLayoutManager(getContext(), 2);
//        LinearLayoutManager productLinearLayout = new LinearLayoutManager(getContext());

        productGridLayout.setReverseLayout(true);
//        productGridLayout.setStackFromEnd(true);
//        productLinearLayout.setReverseLayout(true);
//        productLinearLayout.setStackFromEnd(true);
        mProductList = (RecyclerView) view.findViewById(R.id.productList);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(productGridLayout);

        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        SharedPreferences communitySP;
        String communityReference;

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        // StoreRoom feature Reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("storeroom");
        mDatabase.keepSynced(true);

        if(!status){
            user = mAuth.getCurrentUser();

            mUserStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users").child(user.getUid()).child("Stats");
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


        mBuilder = new NotificationCompat.Builder(getContext());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Firebase predefined Recycler Adapter
        FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductsTab.ProductViewHolder>(

                Product.class,
                R.layout.products_row,
                ProductsTab.ProductViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(final ProductsTab.ProductViewHolder viewHolder, final Product model, int position) {

                SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);
                viewHolder.setProductName(model.getProductName());
                viewHolder.openProduct(model.getKey());
//                viewHolder.setProductDesc(model.getProductDescription());
                try {
                    viewHolder.setImage(getActivity(), model.getProductName(), getContext(), model.getImage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                viewHolder.setPrice(model.getPrice(),model.getNegotiable());
                viewHolder.setSellerName(model.getPostedBy());
                viewHolder.setSellerNumber(model.getCategory(), model.getPhone_no(), getContext());

                if(!status) {
                    viewHolder.defaultSwitch(model.getKey(), getContext(), model.getCategory());

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
//                                            flag = false;
//                                            viewHolder.mReserve.setText("Shortlisted");
//                                            viewHolder.ReserveStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.teal600));
//                                        } else {
//                                            viewHolder.mReserve.setText("Shortlist");
//                                            mDatabase.child(model.getKey()).child("UsersReserved")
//                                                    .child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
//                                            viewHolder.ReserveStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
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
                            final String category = model.getCategory();
                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (flag) {

                                        if (dataSnapshot.child(model.getKey()).child("UsersReserved").hasChild(mAuth.getCurrentUser().getUid())) {
                                            mDatabase.child(model.getKey()).child("UsersReserved").child(mAuth.getCurrentUser().getUid()).removeValue();
                                            viewHolder.shortList.setText("Shortlisted");
                                            flag = false;
                                            CounterManager.StoroomShortListDelete(category, model.getKey());
                                            viewHolder.shortList.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.curvedradiusbutton2_sr));
                                            Typeface customfont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Light.ttf");
                                            viewHolder.shortList.setTypeface(customfont);

                                        } else {
                                            CounterManager.StoroomShortList(category, model.getKey());
                                            viewHolder.shortList.setText("Shortlist");
                                            mDatabase.child(model.getKey()).child("UsersReserved")
                                                    .child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                            viewHolder.shortList.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.curvedradiusbutton_sr));
                                            flag = false;
                                            Typeface customfont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Light.ttf");
                                            viewHolder.shortList.setTypeface(customfont);

                                            IndividualCategory.sendNotification notification = new IndividualCategory.sendNotification();
                                            notification.execute(model.getKey(), model.getProductName());

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

                }
            }
        };
        mProductList.setAdapter(firebaseRecyclerAdapter);
    }

    // Each View Holder Class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public View.OnClickListener mListener;
        View mView;
        //Switch View
        Switch mReserve;
        TextView ReserveStatus;
        // Flag Variable to get each Reserve Id
        String[] keyList;
        // Flag to get combined user Id
        String ReservedUid;
        SharedPreferences sharedPref;

        private SharedPreferences communitySP;
        public String communityReference;


        private DatabaseReference Users;
        private DatabaseReference StoreRoom;
        // Auth to get Current User
        private FirebaseAuth mAuth;
        private Button shortList;

        private ImageView post_image;
        private String sellerName;
        private TextView negotiableText;
        // Constructor
        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            communitySP = mView.getContext().getSharedPreferences("communityName", MODE_PRIVATE);
            communityReference = communitySP.getString("communityReference", null);

            Users = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users");
            StoreRoom = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("storeroom");

            post_image = (ImageView) mView.findViewById(R.id.postImg);
            sharedPref = mView.getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
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

        public void openProduct(final String key){


            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent= new Intent(mView.getContext(),OpenProductDetails.class);
                    intent.putExtra("key", key);
                    mView.getContext().startActivity(intent);

                }
            });

        }
        // Setting default switch
        public void defaultSwitch(final String key, final Context ctx, final String category) {
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
                        shortList.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton2_sr));
                        shortList.setText("Shortlisted");
                        CounterManager.StoroomShortList(category, key);
                        Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
                        shortList.setTypeface(customfont);
                        //ReserveStatus.setTextColor(ContextCompat.getColor(ctx, R.color.teal600));

                    } else {

                        shortList.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton_sr));
                        shortList.setText("Shortlist");
                        Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
                        shortList.setTypeface(customfont);
//                        mReserve.setChecked(false);
//                        mReserve.setText("Shortlist");
//                        ReserveStatus.setTextColor(ContextCompat.getColor(ctx, R.color.black));
                        CounterManager.StoroomShortListDelete(category, key);
                    }
                    shortList.setOnClickListener(mListener);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }

        //Set name of product
        public void setProductName(String productName) {
            TextView post_name = (TextView) mView.findViewById(R.id.productName);
            post_name.setText(productName);
            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
            post_name.setTypeface(ralewayMedium);
        }

        //Set Product Description
        public void setProductDesc(String productDesc) {
            TextView post_desc = (TextView) mView.findViewById(R.id.productDescription);
            post_desc.setText(productDesc);
            Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            post_desc.setTypeface(ralewayMedium);
        }

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
        //Set Product Imag

        //Set Product Price
        public void setPrice(String productPrice,String negotiable) {
            TextView post_price = (TextView) mView.findViewById(R.id.price);
            negotiableText = (TextView) mView.findViewById(R.id.negotiable);
            String price="";
            if(negotiable!=null) {
                if (negotiable.equals("1")) {
                    price = "₹" + productPrice + "/-";
                    negotiableText.setVisibility(View.VISIBLE);
                } else if (negotiable.equals("2")){
                    price = "Price Negotiable";
                    post_price.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
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

        public void setSellerName(String postedBy) {
            Users.child(postedBy).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView post_seller_name = (TextView) mView.findViewById(R.id.sellerName);

                    if (dataSnapshot.child("Username").getValue()!=null) {
                        sellerName = dataSnapshot.child("Username").getValue().toString();
                        post_seller_name.setText("Sold By: " + sellerName);
                    }else {
                        post_seller_name.setText("");
                    }
                    Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
                    post_seller_name.setTypeface(ralewayMedium);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setSellerNumber(final String category, final String sellerNumber, final Context ctx) {
            Button post_seller_number = (Button) mView.findViewById(R.id.sellerNumber);
            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
            post_seller_number.setTypeface(customfont);
            post_seller_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CounterManager.StoroomCall(category);
                    ctx.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(sellerNumber.trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }
    }
}

