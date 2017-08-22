package com.zconnect.zutto.zconnect;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservedTab extends Fragment {

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

    public ReservedTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reserved_tab, container, false);
        noitems = (TextView) view.findViewById(R.id.noitems);

        mProductList = (RecyclerView) view.findViewById(R.id.reservedProductList);
        mProductList.setHasFixedSize(true);
        LinearLayoutManager productLinearLayout = new LinearLayoutManager(getContext());
        productLinearLayout.setReverseLayout(true);
        productLinearLayout.setStackFromEnd(true);
        mProductList.setLayoutManager(productLinearLayout);


        mReservedProducts = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("storeroom");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        query = mDatabase.orderByChild("UsersReserved/" + userId).equalTo(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    noitems.setVisibility(View.GONE);
                } else {
                    noitems.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.reserved_products_row,
                ProductViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Product model, int position) {

                final String product_key = getRef(position).getKey();

//               if(reserveList.contains(model.getKey())) {
                viewHolder.setProductName(model.getProductName());
                viewHolder.setProductDesc(model.getProductDescription());
                viewHolder.setImage(getActivity(), model.getProductName(), getContext(), model.getImage());
                viewHolder.setProductPrice(model.getPrice());
                viewHolder.setSellerName(model.getPostedBy());
                viewHolder.setSellerNumber(model.getPhone_no(), getContext(), model.getCategory());
//                }else {
//               }
                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CounterManager.StoroomShortListDelete(model.getCategory());
                        viewHolder.ReserveReference = FirebaseDatabase.getInstance().getReference().child("storeroom/" + product_key + "/UsersReserved");

                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        final String userId = user.getUid();
                        viewHolder.ReserveReference.child(userId).removeValue();
                    }
                });
            }


        };
        mProductList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {


        View mView;
        String[] keyList;
        String ReservedUid;
        private DatabaseReference ReserveReference;
        private Switch mReserve;
        private TextView ReserveStatus;
        private Button deleteButton;
        private FirebaseAuth mAuth;
        private String sellerName;
        private DatabaseReference Users = FirebaseDatabase.getInstance().getReference().child("Users");
        private ImageView post_image;


        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //to delete reserved items
//            noitems.setVisibility(View.VISIBLE);
            post_image = (ImageView) mView.findViewById(R.id.postImg);
            deleteButton = (Button) mView.findViewById(R.id.delete);
            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
            deleteButton.setTypeface(customfont);
        }

        public void setProductName(String productName) {

            TextView post_name = (TextView) mView.findViewById(R.id.productName);
            post_name.setText(productName);
            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
            post_name.setTypeface(customfont);

        }

        public void setProductDesc(String productDesc) {

            TextView post_desc = (TextView) mView.findViewById(R.id.productDescription);
            post_desc.setText(productDesc);
            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            post_desc.setTypeface(customfont);

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
                    sellerName = dataSnapshot.child("Username").getValue().toString();
                    TextView post_seller_name = (TextView) mView.findViewById(R.id.sellerName);
                    post_seller_name.setText("Sold By: " + sellerName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setSellerNumber(final String sellerNumber, final Context ctx, final String category) {
            Button post_seller_number = (Button) mView.findViewById(R.id.sellerNumber);
            post_seller_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CounterManager.StoroomCall(category);
                    ctx.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(sellerNumber.trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
            post_seller_number.setTypeface(customfont);

        }

    }
}
