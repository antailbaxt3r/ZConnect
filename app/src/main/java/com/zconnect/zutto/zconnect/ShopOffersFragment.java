package com.zconnect.zutto.zconnect;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.itemFormats.ShopOfferItemFormat;

import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;

public class ShopOffersFragment extends Fragment {
    TextView defaultmsg;
    RecyclerView recyclerView;
    String shopid;
    ShopOfferRV adapter;
    private SharedPreferences communitySP;
    public String communityReference;

    Vector<ShopOfferItemFormat> shopOfferItemFormats = new Vector<>();
    private DatabaseReference databaseReference;
    public ShopOffersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_offers, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_shop_rv);
        defaultmsg = (TextView) view.findViewById(R.id.shop_errorMessage);

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Shop").child("Offers");

        shopid = getActivity().getIntent().getStringExtra("ShopId");
        databaseReference.keepSynced(true);


        //setHasFixedSize is used to optimise RV if we know for sure that this view's bounds do not
        // change with data
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        //Setup layout manager. VERY IMP ALWAYS
        adapter = new ShopOfferRV(getContext(), shopOfferItemFormats);

        recyclerView.setAdapter(adapter);

        //changing fonts
        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Light.ttf");
        defaultmsg.setTypeface(customFont);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    defaultmsg.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    shopOfferItemFormats.clear();

                    for (DataSnapshot shot : dataSnapshot.getChildren()) {
                        ShopOfferItemFormat shopOfferItemFormat = shot.getValue(ShopOfferItemFormat.class);
                        if (shopOfferItemFormat.getkey().equals(shopid)) {
                            shopOfferItemFormats.add(shopOfferItemFormat);
                        }

                    }
                }catch (Exception e) {
                    Log.d("Error Alert: ", e.getMessage());
                }

                // Need to add empty search result log message
                if (shopOfferItemFormats.isEmpty()) {
                    defaultmsg.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);

                } else {

                    recyclerView.setAdapter(adapter);
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
