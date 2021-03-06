package com.zconnect.zutto.zconnect.pools;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.LoginActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.pools.adapters.PoolAddItemAdapter;
import com.zconnect.zutto.zconnect.pools.models.DiscountOffer;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.pools.models.PoolInfo;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class ActivePoolDetailsActivity extends BaseActivity {

    public static final String TAG = "UpPoolDetailsActivity";

    private Button btn_payment;
    private RecyclerView recyclerView;
    private TextView offers, orderDeadlineTime, deliveryTime, poolExtraDesc;
    private LinearLayout offerLayout;
    private TextView loading_text;
    private ShimmerFrameLayout shimmerFrameLayout;
    private PoolAddItemAdapter adapter;

    private ValueEventListener poolItemListener, poolOfferListener;

    private Pool pool;
    private String userUID;
    private int discount_percentage, max_amount, min_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pool_item);
        //base case
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("pool")) {

                pool = (Pool) getIntent().getSerializableExtra("pool");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    userUID = user.getUid();
                    setToolbar();

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
//                        getWindow().setStatusBarColor(colorDarkPrimary);
//                        getWindow().setNavigationBarColor(colorPrimary);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    }

                    attachID();
                    setPoolInfo();
                    loadItemView();

                    btn_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //paymentSuccess();
                            openBillView();
                        }
                    });
                }


            } else {
                Log.d(TAG, "onCreate : bundle does not contain newPool key finishing activity");
                finish();
            }
        } else {
            Log.d(TAG, "onCreate : null bundle finishing activity");
            finish();
        }

    }

    private void openBillView() {
        Intent intent = new Intent(this, PoolBillActivity.class);

        HashMap<String,PoolItem> orderList = adapter.getOrderItemList();

        int n = orderList.size();

        if (n == 0) {
            Toast.makeText(this, "Please select at least 1 item", Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra("orderList",orderList);
            intent.putExtra("pool",pool);
            startActivity(intent);
        }
    }

    private void loadItemView() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolItem.URL_POOL_ITEM, pool.getPoolInfo().getShopID(), pool.getPoolInfo().getPoolID()));
        Log.d(TAG, "loadItemView : ref " + ref.toString());
        ref.addValueEventListener(poolItemListener);
    }

    private void setPoolInfo() {
        toolbar.setTitle(pool.getPoolInfo().getName());
        TimeUtilities timeUtilities = new TimeUtilities(pool.getTimestampOrderReceivingDeadline());
        String text = "2. Orders will be taken till " + timeUtilities.getTimeInHHMMAPM() + ", " + timeUtilities.getMonthName("SHORT") + " " + timeUtilities.getDateTime().getDayOfMonth() + " " + timeUtilities.getDateTime().getYearOfEra();
        orderDeadlineTime.setText(text);
        timeUtilities = new TimeUtilities(pool.getDeliveryTime());
        text = "3. Orders will be delivered on " + timeUtilities.getMonthName("SHORT") + " " + timeUtilities.getDateTime().getDayOfMonth() + " " + timeUtilities.getDateTime().getYearOfEra() + ", " + timeUtilities.getTimeInHHMMAPM();
        deliveryTime.setText(text);
        poolExtraDesc.setText(pool.getPoolInfo().getExtras());
        offers.setVisibility(View.INVISIBLE);
        orderDeadlineTime.setVisibility(View.INVISIBLE);
        deliveryTime.setVisibility(View.INVISIBLE);
        poolExtraDesc.setVisibility(View.INVISIBLE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolInfo.URL_POOL_OFFER, pool.getPoolInfo().getShopID(), pool.getPoolInfo().getPoolID()));
        Log.d(TAG, "setPoolView : ref " + ref.toString());
        ref.addValueEventListener(poolOfferListener);
    }

    private void attachID() {
        btn_payment = findViewById(R.id.payment_btn);
        recyclerView = findViewById(R.id.pool_item_rv);
        offers = findViewById(R.id.pool_offers);
        orderDeadlineTime = findViewById(R.id.order_deadline_time);
        deliveryTime = findViewById(R.id.order_delivery_time);
        poolExtraDesc = findViewById(R.id.pool_extra_desc);
        loading_text = findViewById(R.id.loading_text);

        offerLayout = findViewById(R.id.offer_linear_layout);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container_pool_list);

        //setup joinedForumsAdapter
        adapter = new PoolAddItemAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        shimmerFrameLayout.startShimmerAnimation();


        defineListener();
    }

    private void defineListener() {
        poolItemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clearDataset();
                for (DataSnapshot items : dataSnapshot.getChildren()) {
                    PoolItem dish = items.getValue(PoolItem.class);
                    dish.setItemID(items.getKey());
                    adapter.insertAtEnd(dish);
                }
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO on cancel

            }
        };

        poolOfferListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    DiscountOffer discountOffer = dataSnapshot.getValue(DiscountOffer.class);

                    discount_percentage = discountOffer.getDiscountPercentage();
                    max_amount = discountOffer.getMaxDiscount();
                    min_item = discountOffer.getMinQuantity();
                    // if(disPer != 0 && maxDiscount != 0 && minQuantity !=0)
                    offers.setVisibility(View.VISIBLE);
                    orderDeadlineTime.setVisibility(View.VISIBLE);
                    deliveryTime.setVisibility(View.VISIBLE);
                    poolExtraDesc.setVisibility(View.VISIBLE);
                    offers.setText(String.format("%d%% OFF upto " + getApplicationContext().getString(R.string.Rs) + "%d on a minimum order of %d items.", discount_percentage, max_amount, min_item));

                    if (discount_percentage == 0){
                        offerLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }


}
