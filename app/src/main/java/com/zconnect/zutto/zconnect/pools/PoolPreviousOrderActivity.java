package com.zconnect.zutto.zconnect.pools;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolAddItemAdapter;
import com.zconnect.zutto.zconnect.pools.adapters.PoolOrderItemAdapter;
import com.zconnect.zutto.zconnect.pools.models.ShopOrder;

import java.util.ArrayList;

public class PoolPreviousOrderActivity extends AppCompatActivity {

    public static final String TAG = "PoolOrderActivity";

    private RecyclerView recyclerView;
    private LinearLayout ll_progressBar;
    private TextView loading_text;
    private String communityID,userUID;
    private ValueEventListener orderListener;
    private PoolOrderItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_previous_order);
        //TODO set proper data from the preference
        communityID = "testCollege";
        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        attachID();
        loadOrderList();
    }

    private void loadOrderList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("communities/"+communityID);
        ref = ref.child("UsersShopOrder").child(userUID);
        Log.d(TAG,"loadOrderList : ref "+ref.toString());

        ref.addValueEventListener(orderListener);
    }

    private void setProgressBarView(int visibility, String message) {
        ll_progressBar.setVisibility(visibility);
        loading_text.setText(message);

    }
    private void attachID() {
        getSupportActionBar().setTitle("Orders list");
        recyclerView = findViewById(R.id.recycleView);
        ll_progressBar = findViewById(R.id.ll_progressBar);
        loading_text = findViewById(R.id.loading_text);

        //setup adapter
        adapter = new PoolOrderItemAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        defineListener();


    }

    private void defineListener() {
        orderListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ShopOrder> list = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    ShopOrder order = child.getValue(ShopOrder.class);
                    order.setID(child.getKey());
                    list.add(order);
                }
                adapter.addAll(list);
                setProgressBarView(View.GONE,"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO on data cancel
            }
        };
    }
}
