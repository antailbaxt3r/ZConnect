package com.zconnect.zutto.zconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.zconnect.zutto.zconnect.addActivities.AddEvent;
import com.zconnect.zutto.zconnect.addActivities.AddProduct;
import com.zconnect.zutto.zconnect.CabPooling;
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.addActivities.AddStatus;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;

import java.util.HashMap;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class HomeBottomSheet extends BottomSheetDialogFragment{

    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    Button test;

    public HomeBottomSheet(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View bottomSheetView = inflater.inflate(R.layout.content_home_bottomsheet, null);
        LinearLayout bottomSheetAddEvent = (LinearLayout) bottomSheetView.findViewById(R.id.addEvent_bottomSheet);
        LinearLayout bottomSheetAddProduct = (LinearLayout) bottomSheetView.findViewById(R.id.addProduct_bottomSheet);
        LinearLayout bottomSheetAddMessage = (LinearLayout) bottomSheetView.findViewById(R.id.addMessage_bottomSheet);
        LinearLayout bottomSheetSearchPool = (LinearLayout) bottomSheetView.findViewById(R.id.searchPool_bottomSheet);


        View.OnClickListener addEventListener = new View.OnClickListener() {
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
                counterItemFormat.setUniqueID(CounterUtilities.KEY_EVENTS_ADD_EVENT_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

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

                CounterManager.StoreRoomAddClick();
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

                CounterManager.publicStatusAddClick();
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

                meta.put("type","fromHome");


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

        bottomSheetAddEvent.setOnClickListener(addEventListener);
        bottomSheetAddProduct.setOnClickListener(addProductListener);
        bottomSheetAddMessage.setOnClickListener(addMessageListener);
        bottomSheetSearchPool.setOnClickListener(searchPoolListener);

        return bottomSheetView;
    }
}
