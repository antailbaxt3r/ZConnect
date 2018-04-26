package com.zconnect.zutto.zconnect.holders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.ItemFormats.Product;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.NotificationSender;
import com.zconnect.zutto.zconnect.OpenProductDetails;
import com.zconnect.zutto.zconnect.R;

import static android.content.Context.MODE_PRIVATE;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_PRODUCT;

/**
 * Created by Lokesh Garg on 28-03-2018.
 */

public class ProductsViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public View.OnClickListener mListener;

    private SharedPreferences communitySP;
    private String communityReference;

    SharedPreferences sharedPref;
    Boolean status;

    private DatabaseReference Users;
    private DatabaseReference StoreRoom;


    private ImageView productImage;
    private ImageView productShortList;
    private TextView productPrice;
    private TextView productNegotiableText;
//    private Button productSellerContact;

    private String sellerName;
    public Boolean flag;

    private FirebaseAuth mAuth;

    public ProductsViewHolder(final View itemView) {
        super(itemView);

        mView = itemView;
        communitySP = itemView.getContext().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        Users = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
        StoreRoom = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");

        sharedPref = itemView.getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
        status = sharedPref.getBoolean("mode", false);

        productShortList = (ImageView) itemView.findViewById(R.id.shortList);
        if (status) {
            productShortList.setVisibility(View.GONE);
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

    public void defaultSwitch(final String key, final Context ctx, final String category, final String productName) {

        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                StoreRoom.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (flag) {

                            if (dataSnapshot.child(key).child("UsersReserved").hasChild(mAuth.getCurrentUser().getUid())) {
                                StoreRoom.child(key).child("UsersReserved").child(mAuth.getCurrentUser().getUid()).removeValue();
//                                productShortList.setText("Shortlisted");
                                flag = false;
                                CounterManager.StoroomShortListDelete(category, key);
                                productShortList.setImageResource(R.drawable.ic_bookmark_white_24dp);
//                                productShortList.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.curvedradiusbutton2_sr));
//                                Typeface customfont = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//                                productShortList.setTypeface(customfont);

                            } else {
                                CounterManager.StoroomShortList(category, key);
//                                productShortList.setText("Shortlist");
                                final UsersListItemFormat userDetails = new UsersListItemFormat();
                                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                user.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                                        userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                                        userDetails.setName(userItemFormat.getUsername());
                                        userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                                        userDetails.setUserUID(userItemFormat.getUserUID());
                                        StoreRoom.child(key).child("UsersReserved").child(userItemFormat.getUserUID()).setValue(userDetails);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                flag = false;
                                productShortList.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
//                                productShortList.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.curvedradiusbutton_sr));
//                                Typeface customfont = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//                                productShortList.setTypeface(customfont);

                                NotificationSender notificationSender=new NotificationSender(key,null,null,null,null,mAuth.getCurrentUser().getEmail(),productName,KEY_PRODUCT,false,true,itemView.getContext());
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
        productShortList.setOnClickListener(mListener);

        // Getting User ID
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();


        //Getting  data from database
        StoreRoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                productShortList.setOnClickListener(null);
                if (dataSnapshot.child(key).child("UsersReserved").hasChild(userId)) {
                    productShortList.setImageResource(R.drawable.ic_bookmark_white_24dp);
//                    productShortList.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton2_sr));
//                    productShortList.setText("Shortlisted");
                    CounterManager.StoroomShortList(category, key);
//                    Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//                    productShortList.setTypeface(customfont);
                } else {
                    productShortList.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
//                    productShortList.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton_sr));
//                    productShortList.setText("Shortlist");
//                    Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//                    productShortList.setTypeface(customfont);
                    CounterManager.StoroomShortListDelete(category, key);
                }
                productShortList.setOnClickListener(mListener);
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

    public void setImage(Context ctx, final String image) {

        productImage = (ImageView) mView.findViewById(R.id.postImg);
        Picasso.with(ctx).load(image).into(productImage);
    }

    public void setPrice(String Price,String negotiable) {
        productPrice = (TextView) mView.findViewById(R.id.price);
        productNegotiableText = (TextView) mView.findViewById(R.id.negotiable);
        String priceString="";
        if(negotiable!=null) {
            if (negotiable.equals("1")) {
                priceString = "₹" + Price + "/-";
                productNegotiableText.setVisibility(View.VISIBLE);
            } else if (negotiable.equals("2")){
                priceString = "Price Negotiable";
                productPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            }
            else
                priceString = "₹" + Price + "/-";

            productPrice.setText(priceString);
        }
        else
        {
            productPrice.setText("₹" + Price + "/-");
        }
        Log.d("PRODUCT PRICE 2", "+" + Price);
        Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
        productPrice.setTypeface(ralewayMedium);
    }

//    public void setSellerName(String postedBy) {
//        Users.child(postedBy).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                TextView post_seller_name = (TextView) mView.findViewById(R.id.sellerName);
//
//                if (dataSnapshot.child("Username").getValue()!=null) {
//                    sellerName = dataSnapshot.child("Username").getValue().toString();
//                    post_seller_name.setText("Sold By: " + sellerName);
//                }else {
//                    post_seller_name.setText("");
//                }
//                Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//                post_seller_name.setTypeface(ralewayMedium);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

//    public void setSellerNumber(final String category, final String sellerNumber, final Context ctx) {
//        productSellerContact = (Button) mView.findViewById(R.id.sellerNumber);
//        Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//        productSellerContact.setTypeface(customfont);
//        productSellerContact.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CounterManager.StoroomCall(category);
//                ctx.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(sellerNumber.trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//            }
//        });
//    }
}
