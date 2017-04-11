package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.OfferFormat;

public class Offers extends AppCompatActivity {

    RecyclerView offersRecycler;
    DatabaseReference offersDatabase;
    LinearLayoutManager offersLinearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
            getWindow().setStatusBarColor(colorPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        offersRecycler = (RecyclerView) findViewById(R.id.offersRecycler);
        offersDatabase = FirebaseDatabase.getInstance().getReference().child("Shop").child("Offers");
        offersLinearLayoutManager = new LinearLayoutManager(this);
        offersLinearLayoutManager.setReverseLayout(true);
        offersLinearLayoutManager.setStackFromEnd(true);
        offersLinearLayoutManager.scrollToPosition(1);

        offersDatabase.keepSynced(true);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<OfferFormat, offerViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OfferFormat, offerViewHolder>(
                OfferFormat.class,
                R.layout.events_row,
                offerViewHolder.class,
                offersDatabase
        ) {
            @Override
            protected void populateViewHolder(offerViewHolder viewHolder, OfferFormat model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(model.getImageUrl(), getApplicationContext());
            }


        };

        offersRecycler.setAdapter(firebaseRecyclerAdapter);

    }

    public class offerViewHolder extends RecyclerView.ViewHolder {

        DatabaseReference shopReference;
        View mView;

        public offerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setLink(String ShopNo) {

            shopReference = FirebaseDatabase.getInstance().getReference().child("Shop").child("Shops").child(ShopNo);

            shopReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Intent intent = new Intent(getApplicationContext(), Shop_detail.class);
                    intent.putExtra("Name", dataSnapshot.child("name").getValue().toString());
                    intent.putExtra("Details", dataSnapshot.child("details").getValue().toString());
                    intent.putExtra("Imageurl", dataSnapshot.child("imageurl").getValue().toString());
                    intent.putExtra("Lat", dataSnapshot.child("lat").getValue().toString());
                    intent.putExtra("Lon", dataSnapshot.child("lon").getValue().toString());
                    intent.putExtra("Number", dataSnapshot.child("number").getValue().toString());
                    intent.putExtra("ShopId", dataSnapshot.child("shopid").getValue().toString());
                    startActivity(intent);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setTitle(String Title) {
            TextView TitleText = (TextView) mView.findViewById(R.id.offerTitle);
        }

        public void setDescription(String Description) {
            TextView DescriptionText = (TextView) mView.findViewById(R.id.offerDescription);
        }

        public void setImage(String ImageUrl, Context ctx) {
            ImageView OfferImageView = (ImageView) mView.findViewById(R.id.offerImage);
            Picasso.with(ctx).load(ImageUrl).into(OfferImageView);
        }

    }


}
