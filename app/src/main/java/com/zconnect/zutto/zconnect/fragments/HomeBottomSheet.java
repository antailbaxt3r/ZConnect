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
import android.widget.Toast;

import com.zconnect.zutto.zconnect.AddEvent;
import com.zconnect.zutto.zconnect.AddProduct;
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.NewMessageActivity;
import com.zconnect.zutto.zconnect.R;

public class HomeBottomSheet extends BottomSheetDialogFragment{

    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    Button test;

    public HomeBottomSheet () {
        //Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bottomSheetView = inflater.inflate(R.layout.content_home_bottomsheet, null);
        LinearLayout bottomSheetAddEvent = (LinearLayout) bottomSheetView.findViewById(R.id.addEvent_bottomSheet);
        LinearLayout bottomSheetAddProduct = (LinearLayout) bottomSheetView.findViewById(R.id.addProduct_bottomSheet);
        LinearLayout bottomSheetAddMessage = (LinearLayout) bottomSheetView.findViewById(R.id.addMessage_bottomSheet);


        View.OnClickListener addEventListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CounterManager.eventAddClick();
                Intent intent;
                intent = new Intent(getContext(), AddEvent.class);
                startActivity(intent);
            }
        };
        View.OnClickListener addProductListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CounterManager.eventAddClick();
                Intent intent;
                intent = new Intent(getContext(), AddProduct.class);
                startActivity(intent);
            }
        };
        View.OnClickListener addMessageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CounterManager.eventAddClick();
                Intent intent;
                intent = new Intent(getContext(), NewMessageActivity.class);
                startActivity(intent);
            }
        };

        bottomSheetAddEvent.setOnClickListener(addEventListener);
        bottomSheetAddProduct.setOnClickListener(addProductListener);
        bottomSheetAddMessage.setOnClickListener(addMessageListener);

        return bottomSheetView;
    }
}
