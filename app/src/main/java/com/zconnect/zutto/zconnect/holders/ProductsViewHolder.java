package com.zconnect.zutto.zconnect.holders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
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
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.OpenProductDetails;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import org.w3c.dom.Text;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityTitle;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_PRODUCT;

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
    private TextView productViewsCount;
    private TextView productNegotiableText;
//    private Button productSellerContact;
    private TextView productDate;

    Long ViewsCounter;

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

    public void openProduct(final String key, final String type){

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();

                meta.put("type","fromFeature");
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_OPEN_PRODUCT);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                StoreRoom.child(key).child("NumberOfViews").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try
                        {
                            ViewsCounter = (Long) dataSnapshot.getValue();
                            ViewsCounter++;
                            StoreRoom.child(key).child("NumberOfViews").setValue(ViewsCounter);
                        }
                        catch (Exception e)
                        {
                            Log.e("Error","Number of views not found for this product");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                Intent intent= new Intent(mView.getContext(),OpenProductDetails.class);
                intent.putExtra("key", key);
                intent.putExtra("type", type);
                mView.getContext().startActivity(intent);


            }
        });

    }

    public void defaultSwitch(final String key, final Context ctx, final String category, final String productName) {

        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                flag = true;

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();

                meta.put("type","fromRV");

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_SHORTLIST);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                StoreRoom.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (flag) {

                            if (dataSnapshot.child(key).child("UsersReserved").hasChild(mAuth.getCurrentUser().getUid())) {
                                StoreRoom.child(key).child("UsersReserved").child(mAuth.getCurrentUser().getUid()).removeValue();
//                                productShortList.setText("ShortlistedPeopleList");
                                flag = false;
                                productShortList.setImageResource(R.drawable.ic_bookmark_white_24dp);
//                                productShortList.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.curvedradiusbutton2_sr));
//                                Typeface customfont = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//                                productShortList.setTypeface(customfont);


                            } else {
//                                productShortList.setText("Shortlist");
                                final UsersListItemFormat userDetails = new UsersListItemFormat();
                                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                user.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                        UserItemFormat userItemFormat = dataSnapshot2.getValue(UserItemFormat.class);
                                        userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                                        userDetails.setName(userItemFormat.getUsername());
                                        userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                                        userDetails.setUserUID(userItemFormat.getUserUID());

                                        StoreRoom.child(key).child("UsersReserved").child(userItemFormat.getUserUID()).setValue(userDetails);

                                        NotificationSender notificationSender = new NotificationSender(itemView.getContext(),userItemFormat.getUserUID());
                                        NotificationItemFormat productShortlistNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_SHORTLIST,userItemFormat.getUserUID(), (String) dataSnapshot.child("PostedBy").child("UID").getValue(),1);
                                        productShortlistNotification.setCommunityName(communityTitle);
                                        productShortlistNotification.setItemKey(key);
                                        productShortlistNotification.setItemName(productName);
                                        productShortlistNotification.setUserName(userItemFormat.getUsername());
                                        productShortlistNotification.setUserMobileNumber(userItemFormat.getMobileNumber());
                                        productShortlistNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                                        productShortlistNotification.setRecieverKey(dataSnapshot.child(key).child("PostedBy").child("UID").getValue().toString());
                                        notificationSender.execute(productShortlistNotification);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                flag = false;
                                productShortList.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
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
                    flag = true;
//                    productShortList.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton2_sr));
//                    productShortList.setText("ShortlistedPeopleList");
//                    Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//                    productShortList.setTypeface(customfont);
                } else {
                    flag = false;
                    productShortList.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
//                    productShortList.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton_sr));
//                    productShortList.setText("Shortlist");
//                    Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//                    productShortList.setTypeface(customfont);
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

    public void setAskText(String productName) {
        TextView ask_text = (TextView) mView.findViewById(R.id.ask_text_products_row);
        ask_text.setText(productName);
        ask_text.setVisibility(View.VISIBLE);
        Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
        ask_text.setTypeface(ralewayMedium);
    }

    public void setImage(final Context ctx, final String image) {
        productImage = (ImageView) mView.findViewById(R.id.postImg);
        Picasso.with(ctx).load(image).into(productImage);
    }

    public void hideAskText() {
        TextView ask_text = (TextView) mView.findViewById(R.id.ask_text_products_row);
        ask_text.setVisibility(View.GONE);
    }

    public void setPrice(String Price) {
        productPrice = (TextView) mView.findViewById(R.id.price);

        productPrice.setText("₹" + Price + "/-");

        Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
        productPrice.setTypeface(ralewayMedium);
    }

    public void setNegotiable(Boolean isNegotiable) {

        productNegotiableText = (TextView) mView.findViewById(R.id.negotiable);
        if (isNegotiable){
            productNegotiableText.setVisibility(View.VISIBLE);
        }else {
            productNegotiableText.setVisibility(View.GONE);
        }

        Typeface ralewayMedium = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
        productPrice.setTypeface(ralewayMedium);
    }

    public void setProductDate(long postedTime, long currentTime) {
        productDate = (TextView) mView.findViewById(R.id.product_date);
        TimeUtilities tu = new TimeUtilities(postedTime, currentTime);
        String timeAgo = tu.calculateTimeAgoStoreroom();
        productDate.setText(timeAgo);

    }

    public void setNumberOfViewsInHolder(int numberOfViews) {
        productViewsCount = (TextView) mView.findViewById(R.id.views);
        productViewsCount.setVisibility(View.VISIBLE);

        if (numberOfViews==0)
            productViewsCount.setText("No Views");
        else if (numberOfViews==1)
            productViewsCount.setText("1 View");
        else
            productViewsCount.setText(numberOfViews+" Views");

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
//                ctx.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(sellerNumber.trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//            }
//        });
//    }
}
