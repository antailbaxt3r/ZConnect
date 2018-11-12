package com.zconnect.zutto.zconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.addActivities.AddEvent;
import com.zconnect.zutto.zconnect.addActivities.AddNotices;
import com.zconnect.zutto.zconnect.addActivities.AddProduct;
import com.zconnect.zutto.zconnect.CabPooling;
import com.zconnect.zutto.zconnect.addActivities.AddStatus;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CommunityFeatures;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;

import java.util.HashMap;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class HomeBottomSheet extends BottomSheetDialogFragment{

    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    Button test;
    DatabaseReference communityFeaturesRef;

    public HomeBottomSheet(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View bottomSheetView = inflater.inflate(R.layout.content_home_bottomsheet, null);
        final LinearLayout bottomSheetAddEvent = (LinearLayout) bottomSheetView.findViewById(R.id.addEvent_bottomSheet);
        final LinearLayout bottomSheetAddProduct = (LinearLayout) bottomSheetView.findViewById(R.id.addProduct_bottomSheet);
        LinearLayout bottomSheetAddMessage = (LinearLayout) bottomSheetView.findViewById(R.id.addMessage_bottomSheet);
        final LinearLayout bottomSheetSearchPool = (LinearLayout) bottomSheetView.findViewById(R.id.searchPool_bottomSheet);
        final LinearLayout bottomSheetAddNotices = bottomSheetView.findViewById(R.id.add_notices_bottomSheet);

        communityFeaturesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("communityFeatures");

        communityFeaturesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CommunityFeatures communityFeatures = dataSnapshot.getValue(CommunityFeatures.class);

                try {

                    if (communityFeatures.getCabpool().equals("true")){
                        bottomSheetSearchPool.setVisibility(View.VISIBLE);
                    }else {
                        bottomSheetSearchPool.setVisibility(View.GONE);
                    }


                    if (communityFeatures.getEvents().equals("true")){
                        bottomSheetAddEvent.setVisibility(View.VISIBLE);
                    }else {
                        bottomSheetAddEvent.setVisibility(View.GONE);
                    }

                    if (communityFeatures.getGallery().equals("true")){

                    }else {

                    }

                    if (communityFeatures.getNotices().equals("true")){
                        bottomSheetAddNotices.setVisibility(View.VISIBLE);
                    }else {
                        bottomSheetAddNotices.setVisibility(View.GONE);
                    }


                    if (communityFeatures.getLinks().equals("true")){

                    }else {

                    }

                    if (communityFeatures.getStoreroom().equals("true")){
                        bottomSheetAddProduct.setVisibility(View.VISIBLE);
                    }else {
                        bottomSheetAddProduct.setVisibility(View.GONE);
                    }


                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        View.OnClickListener addEventListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                Intent intent;
                intent = new Intent(getContext(), AddEvent.class);
                startActivity(intent);

            }
        };
        View.OnClickListener addProductListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();

                meta.put("type","fromRecents");

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_PRODUCT_ADD_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();
                Intent intent;
                intent = new Intent(getContext(), AddProduct.class);
                startActivity(intent);

            }
        };
        View.OnClickListener addMessageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();
                meta.put("type","fromRecents");
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_ADD_STATUS);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);
                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                Intent intent;
                intent = new Intent(getContext(), AddStatus.class);
                startActivity(intent);
            }
        };

        View.OnClickListener searchPoolListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();

                meta.put("type","fromRecents");


                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_SEARCH_POOL_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();
                Intent intent;
                intent = new Intent(getContext(), CabPooling.class);
                startActivity(intent);
            }
        };

        View.OnClickListener noticesListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();
                meta.put("type","fromRecents");
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTICES_ADD_NOTICES);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);
                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                Intent intent;
                intent = new Intent(getContext(), AddNotices.class);
                startActivity(intent);
            }
        };

        bottomSheetAddEvent.setOnClickListener(addEventListener);
        bottomSheetAddProduct.setOnClickListener(addProductListener);
        bottomSheetAddMessage.setOnClickListener(addMessageListener);
        bottomSheetSearchPool.setOnClickListener(searchPoolListener);

        return bottomSheetView;
    }
}
