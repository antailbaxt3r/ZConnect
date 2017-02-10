package com.zconnect.zutto.zconnect;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsTab extends Fragment {

    private RecyclerView mProductList;
    private DatabaseReference mDatabase;
    private boolean flag = false;
    private FirebaseAuth mAuth;

    public ProductsTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products_tab, container, false);

        mProductList = (RecyclerView) view.findViewById(R.id.productList);
        mProductList.setHasFixedSize(true);
        LinearLayoutManager productLinearLayout = new LinearLayoutManager(getContext());
        productLinearLayout.setReverseLayout(true);
        mProductList.setLayoutManager(productLinearLayout);

        mAuth = FirebaseAuth.getInstance();

        // StoreRoom feature Reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ZConnect/storeroom");
        mDatabase.keepSynced(true);
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
            protected void populateViewHolder(ProductsTab.ProductViewHolder viewHolder, final Product model, int position) {
                viewHolder.defaultSwitch(model.getKey());
                viewHolder.setProductName(model.getProductName());
                viewHolder.setProductDesc(model.getProductDescription());
                viewHolder.setImage(getContext(), model.getImage());
                viewHolder.setPrice("Rs " + model.getPrice());
                viewHolder.openProduct(model.getKey());


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
                                        flag = false;
                                    } else {

                                        mDatabase.child(model.getKey()).child("UsersReserved").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getDisplayName());
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

    // Each View Holder Class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public CompoundButton.OnCheckedChangeListener mListener;
        View mView;
        //Switch View
        Switch mReserve;
        TextView ReserveStatus;
        // Flag Variable to get each Reserve Id
        String[] keyList;
        // Flag to get combined user Id
        String ReservedUid;
        private DatabaseReference Users = FirebaseDatabase.getInstance().getReference().child("ZConnect/Users");
        private DatabaseReference StoreRoom = FirebaseDatabase.getInstance().getReference().child("ZConnect/storeroom");
        private DatabaseReference CurrentProduct;
        // Auth to get Current User
        private FirebaseAuth mAuth;

        // Constructor
        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mReserve = (Switch) mView.findViewById(R.id.switch1);
            ReserveStatus = (TextView) mView.findViewById(R.id.switch1);
            StoreRoom.keepSynced(true);
        }

        public void openProduct(final String key) {
            mView.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(mView.getContext(), OpenProductDetail.class);
                    i.putExtra("key", key);
                    mView.getContext().startActivity(i);
                }
            });
        }

        // Setting default switch
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
                    } else {
                        mReserve.setChecked(false);
                    }
                    mReserve.setOnCheckedChangeListener(mListener);

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

        }

        //Set Product Description
        public void setProductDesc(String productDesc) {

            TextView post_desc = (TextView) mView.findViewById(R.id.productDescription);
            post_desc.setText(productDesc);

        }

        //Set Product Image
        public void setImage(Context ctx, String image) {

            ImageView post_image = (ImageView) mView.findViewById(R.id.postImg);
            Picasso.with(ctx).load(image).into(post_image);

        }

        //Set Product Price
        public void setPrice(String productPrice) {
            TextView post_name = (TextView) mView.findViewById(R.id.price);
            post_name.setText(productPrice);
        }

    }
}
