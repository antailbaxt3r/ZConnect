package com.zconnect.zutto.zconnect;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.adapters.UsersListRVAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.utilities.FeatureNamesUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.ProductUtilities;

import java.util.ArrayList;
import java.util.Vector;

public class ShortlistedPeopleList extends BaseActivity {

    private Vector<UsersListItemFormat> usersListItemFormatVector = new Vector<UsersListItemFormat>();
    private ArrayList<String> names = new ArrayList<>(), nos = new ArrayList<>(), images = new ArrayList<>();
    private String pName, pPrice, pDescription, pImage;
    private TextView productName, productDescription, productPrice;
    private ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortlisted);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setToolbar();
        setTitle("Product Details");
        setSupportActionBar(toolbar);
//        setTitle("List of people");
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
        setToolbarTitle("Product Details");
        showBackButton();

        showProgressDialog();

        productName = findViewById(R.id.productNameInShortlist);
        productDescription = findViewById(R.id.productDescriptionInShortlist);
        productPrice = findViewById(R.id.priceInShortlist);
        productImage = findViewById(R.id.postImgInShortlist);

        final String key = getIntent().getStringExtra("Key");

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        final UsersListRVAdapter adapter = new UsersListRVAdapter(this, usersListItemFormatVector, FeatureNamesUtilities.KEY_STOREROOM, ForumsUserTypeUtilities.KEY_USER);

        LinearLayoutManager productLinearLayout = new LinearLayoutManager(this);
        productLinearLayout.setReverseLayout(true);
        productLinearLayout.setStackFromEnd(true);

        rv.setLayoutManager(productLinearLayout);
        rv.setAdapter(adapter);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference)
                .child("features").child("storeroom").child("products").child(key).child("UsersReserved");
        final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference)
                .child("features").child("storeroom").child("products").child(key);

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    pName = dataSnapshot.child("ProductName").getValue().toString();
                    pDescription = dataSnapshot.child("ProductDescription").getValue().toString();
                    pImage = dataSnapshot.child("Image").getValue().toString();

                    Picasso.with(getApplicationContext()).load(pImage).into(productImage);
                    productName.setText(pName);
                    productDescription.setText(pDescription);
                    if (!dataSnapshot.child("type").getValue(String.class).equals(ProductUtilities.TYPE_ASK_STR)) {
                        pPrice = dataSnapshot.child("Price").getValue().toString();
                        productPrice.setText("₹" + pPrice);

                    }
                }
                catch (Exception e){
                    Log.d("ShortlistedPeopleList","Product deleted but listener still intact: "+e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                usersListItemFormatVector.clear();
                UsersListItemFormat user;
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    try {
                        user = shot.getValue(UsersListItemFormat.class);
                        usersListItemFormatVector.add(user);
                    } catch (Exception e) {
                    }

                }
                adapter.notifyDataSetChanged();


//
//                nos.clear();
//                names.clear();
//                images.clear();
//                for(DataSnapshot snapshot : dataSnapshot.getChildren())
//                {
//                    String user = snapshot.getKey();
//                    FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference)
//                            .child("Users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            String email = dataSnapshot.child("Email").getValue(String.class);
//                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference)
//                                    .child("Phonebook").orderByChild("uid").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.getChildrenCount() != 0) {
//                                        PhonebookDisplayItem item = new PhonebookDisplayItem();
//                                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                                            item = snapshot.getValue(PhonebookDisplayItem.class);
//
//                                        if (item == null)
//                                            return;
//                                        try {
//                                            names.add(item.getName());
//                                            nos.add(item.getNumber());
//                                            images.add(item.getImageurl());
//                                        } catch (Exception e) {}
//                                        adapter.notifyDataSetChanged();
//
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
                hideProgressDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });

//    }
//    class vh extends RecyclerView.ViewHolder {
//
//        public vh(View itemView) {
//            super(itemView);
//        }
//
//        private void setData(final int pos) {
//            final SimpleDraweeView image = (SimpleDraweeView) itemView.findViewById(R.id.sdv_avatar_contact_item);
//            image.setImageURI(images.get(pos));
//            ((TextView) itemView.findViewById(R.id.tv_name_contact_item)).setText(names.get(pos));
//            itemView.findViewById(R.id.ib_call_contact_item).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nos.get(pos))));
//                }
//            });
//
//        }
//
//    }
//    class adapter extends RecyclerView.Adapter<vh> {
//
//        @Override
//        public vh onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//            View person = inflater.inflate(R.layout.view_contect_item, parent, false);
//            return new vh(person);
//        }
//
//        @Override
//        public void onBindViewHolder(vh holder, int position) {
//            holder.setData(position);
//        }
//
//        @Override
//        public int getItemCount() {
//            return names.size();
//        }
//    }
    }
}
