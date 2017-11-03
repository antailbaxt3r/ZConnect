package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.GalleryFormat;

import java.util.ArrayList;
import java.util.Vector;

public class ShopDetailFragment extends Fragment {

    TextView details;
    LinearLayout linearLayout, numberlayout;
    Button call,direction;

    String nam, detail, lat, lon, imageurl, num, menuurl, shopid = null;
    DatabaseReference mDatabase, mDatabaseMenu,database;
    HorizontalScrollView galleryScroll, menuScroll;

    GalleryAdapter adapter,adapter1;
    RecyclerView galleryRecycler;
    RecyclerView menuRecycler;
    ArrayList<String> menuImages = new ArrayList<String>();
    ArrayList<String> galleryImages = new ArrayList<String>();
    Vector<GalleryFormat>galleryFormats=new Vector<>();
    Vector<GalleryFormat>menu=new Vector<>();
    public ShopDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_detail, container, false);
        galleryScroll = (HorizontalScrollView) view.findViewById(R.id.galleryScroll);
        menuScroll = (HorizontalScrollView) view.findViewById(R.id.menuScroll);
        galleryScroll.setHorizontalScrollBarEnabled(false);
        menuScroll.setHorizontalScrollBarEnabled(false);
        call = (Button) view.findViewById(R.id.call);
        direction = (Button) view.findViewById(R.id.direction);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        galleryRecycler = (RecyclerView) view.findViewById(R.id.galleryRecycler);
        galleryRecycler.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManagerMenu = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        menuRecycler = (RecyclerView) view.findViewById(R.id.menuRecycler);
        menuRecycler.setLayoutManager(layoutManagerMenu);



        shopid = getActivity().getIntent().getStringExtra("ShopId");

        details = (TextView) view.findViewById(R.id.shop_details_details);

        Typeface ralewayRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Medium.ttf");

        details.setTypeface(ralewayRegular);


        if(shopid!=null){
            database=FirebaseDatabase.getInstance().getReference().child("Shop").child("Shops").child(shopid);
        }
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    nam = dataSnapshot.child("name").getValue().toString();
                    Log.v("TAg", nam);
                    detail = dataSnapshot.child("details").getValue().toString();
                    lat = dataSnapshot.child("lat").getValue().toString();
                    lon = dataSnapshot.child("lon").getValue().toString();
                    num = dataSnapshot.child("contactDescTv").getValue().toString();
                    details.setText(detail);

                    direction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CounterManager.shopDirections(nam);
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lon));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CounterManager.shopCall(nam);
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num)));
                        }
                    });
                }catch (Exception e) {
                    Log.d("Error Alert: ", e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        galleryRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CounterManager.shopGallery(nam);
            }
        });
        menuRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CounterManager.shopProducts(nam);
            }
        });

            mDatabase =FirebaseDatabase.getInstance().getReference().child("Shop").child("Gallery").child(shopid);
            mDatabaseMenu = FirebaseDatabase.getInstance().getReference().child("Shop").child("Menu").child(shopid);

        mDatabase.keepSynced(true);
        mDatabaseMenu.keepSynced(true);
        adapter=new GalleryAdapter(getContext(),galleryFormats);
        galleryRecycler.setAdapter(adapter);
        adapter1=new GalleryAdapter(getContext(),menu);
        menuRecycler.setAdapter(adapter1);

        //changing fonts
        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Light.ttf");
        TextView galleryText = (TextView)view.findViewById(R.id.galleryText);
        TextView productText = (TextView)view.findViewById(R.id.productText);
        galleryText.setTypeface(customFont);
        productText.setTypeface(customFont);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        menuImages.removeAll(menuImages);
        galleryImages.removeAll(galleryImages);
    }


    @Override
    public void onStart() {
        super.onStart();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                galleryFormats.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {

                    galleryFormats.add(shot.getValue(GalleryFormat.class));

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabaseMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                menu.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {

                    menu.add(shot.getValue(GalleryFormat.class));

                }

                adapter1.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
