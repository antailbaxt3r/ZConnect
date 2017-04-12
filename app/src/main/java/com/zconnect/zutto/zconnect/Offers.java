package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.zconnect.zutto.zconnect.ItemFormats.ShopOfferItemFormat;

import java.util.Vector;

public class Offers extends AppCompatActivity {

    RecyclerView offersRecycler;
    DatabaseReference offersDatabase;
    TextView defaultmsg;
    LinearLayoutManager offersLinearLayoutManager;
    Vector<ShopOfferItemFormat>shopOfferItemFormats=new Vector<>();
    ShopOfferRV adapter;


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
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }


        offersRecycler = (RecyclerView) findViewById(R.id.offersRecycler);
        defaultmsg=(TextView)findViewById(R.id.shop_errorMessage1);
        offersDatabase = FirebaseDatabase.getInstance().getReference().child("Shop").child("Offers");

adapter=new ShopOfferRV(this,shopOfferItemFormats);
        offersRecycler.setHasFixedSize(true);
        offersRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        offersRecycler.setAdapter(adapter);
        offersDatabase.keepSynced(true);


    }

    @Override
    protected void onStart() {
        super.onStart();

        offersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                defaultmsg.setVisibility(View.INVISIBLE);
                offersRecycler.setVisibility(View.VISIBLE);
                shopOfferItemFormats.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    ShopOfferItemFormat shopOfferItemFormat = shot.getValue(ShopOfferItemFormat.class);

                        shopOfferItemFormats.add(shopOfferItemFormat);
                    }



                // Need to add empty search result log message
                if (shopOfferItemFormats.isEmpty()) {
                    defaultmsg.setVisibility(View.VISIBLE);
                    offersRecycler.setVisibility(View.INVISIBLE);

                } else {

                    offersRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //defaultmsg.setVisibility(INVISIBLE);
            }
        });

    }


}
