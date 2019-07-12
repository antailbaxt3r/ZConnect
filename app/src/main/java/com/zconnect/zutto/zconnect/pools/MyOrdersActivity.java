package com.zconnect.zutto.zconnect.pools;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.pools.adapters.PoolOrderItemAdapter;
import com.zconnect.zutto.zconnect.pools.models.Order;

import java.util.ArrayList;
import java.util.Collections;

public class MyOrdersActivity extends BaseActivity {

    public final String TAG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView loading_text, noOrders;
    private String userUID;
    private ValueEventListener orderListener;
    private PoolOrderItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_previous_order);
        //TODO set proper data from the preference
        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setToolbar();
        attachID();
        loadOrderList();
        shimmerFrameLayout.startShimmerAnimation();

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

    }

    private void loadOrderList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(Order.URL_MY_ORDERS,communityReference,userUID));
        Log.d(TAG, "loadOrderList : ref " + ref.toString());
        ref.addValueEventListener(orderListener);
    }

    private void setProgressBarView(int visibility, String message) {
        loading_text.setText(message);

    }

    private void attachID() {
        toolbar.setTitle("Orders list");
        recyclerView = findViewById(R.id.recycleView);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container_my_orders);
        loading_text = findViewById(R.id.loading_text);
        noOrders = findViewById(R.id.no_my_orders);

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
                Log.i("YOLO", dataSnapshot.toString());
                ArrayList<Order> list = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Order order = child.getValue(Order.class);
                    if(order.getOrderID()!=null)
                        list.add(order);
                }
                Collections.reverse(list);
                if(list.size()>0)
                    noOrders.setVisibility(View.GONE);
                else
                    noOrders.setVisibility(View.VISIBLE);
                adapter.addAll(list);
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO on data cancel
            }
        };
    }
}
