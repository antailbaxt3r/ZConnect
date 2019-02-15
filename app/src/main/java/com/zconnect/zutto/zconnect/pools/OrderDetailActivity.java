package com.zconnect.zutto.zconnect.pools;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.pools.adapters.PoolItemCartAdapter;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;
import com.zconnect.zutto.zconnect.pools.models.Order;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;

import net.glxn.qrgen.android.QRCode;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;

public class OrderDetailActivity extends BaseActivity {

    public static final String TAG = "OrderDetailActivity";

    private ImageView qr_image;
    private String userUID;
    private Order order;
    private RecyclerView recyclerView;
    private PoolItemCartAdapter adapter;
    private ValueEventListener poolItemListener,orderItemListener;
    private ArrayList<PoolItem> poolItems = new ArrayList<>();
    private TextView orderStatus, userBillID, itemTotal, discountTotal, discountedTotal;
    private ImageView orderStatusIcon;
    private FrameLayout deliveredTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Bundle b = getIntent().getExtras();

        if (b != null) {
            if (b.containsKey("order")) {
                order = (Order) getIntent().getSerializableExtra("order");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    //TODO start login acitvity
                    finish();
                } else {
                    userUID = user.getUid();
                    //TODO set proper data from the preference

                    setToolbar();
                    //activity main block with all valid parameters
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

                    attachID();
                    setOrderQRView();
                    loadItemView();
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

    private void loadItemView() {
        //setProgressBarView(View.VISIBLE, "Loading list\nplease wait..");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolItem.URL_POOL_ITEM, order.getPoolInfo().getShopID(), order.getPoolInfo().getPoolID()));
        Log.d(TAG, "loadItemView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(poolItemListener);
    }

    private void setOrderQRView() {

        Bitmap myBitmap;
        if (order.getUserBillID() != null) {
            myBitmap = QRCode.from(order.getUserBillID()).bitmap();
        }
        //delete this else after cleaning database
        else {
            Log.i(TAG, "No userBillID");
            myBitmap = QRCode.from("NULLNULLNULL").bitmap();
        }
        qr_image.setImageBitmap(myBitmap);
        if (order.getOrderStatus().equals(OtherKeyUtilities.KEY_ORDER_OUT_FOR_DELIVERY))
        {
            DateTime dateTime = new DateTime(order.getDeliveryTime(), DateTimeZone.forID("Asia/Kolkata"));
            String text = "Order out for delivery on " + dateTime.toString("MMM") + " " + dateTime.getDayOfMonth();
            orderStatus.setText(text);
            orderStatusIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.baseline_local_shipping_24));
            orderStatusIcon.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorHighlightLight), PorterDuff.Mode.SRC_ATOP);
            deliveredTag.setVisibility(View.GONE);
        }
        else if(order.getOrderStatus().equals(OtherKeyUtilities.KEY_ORDER_DELIVERED))
        {
            DateTime dateTime = new DateTime(order.getDeliveryRcdTime(), DateTimeZone.forID("Asia/Kolkata"));
            String text = "Order delivered on " + dateTime.toString("MMM") + " " + dateTime.getDayOfMonth() + ", "
                    + (dateTime.getHourOfDay()>12?dateTime.getHourOfDay()-12:dateTime.getHourOfDay())
                    +":"+(dateTime.getMinuteOfHour()<10?"0"+dateTime.getMinuteOfHour():dateTime.getMinuteOfHour())
                    + (dateTime.getHourOfDay()<12?" AM":" PM");
            orderStatus.setText(text);
            orderStatusIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.baseline_check_white_24));
            orderStatusIcon.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorHighlightLight), PorterDuff.Mode.SRC_ATOP);
            deliveredTag.setVisibility(View.VISIBLE);
        }
        userBillID.setText(order.getUserBillID());
        itemTotal.setText(String.format("%s%d",getResources().getString(R.string.Rs),order.getTotalAmount()));
        discountTotal.setText(String.format("%s%d",getResources().getString(R.string.Rs),(order.getTotalAmount()-order.getDiscountedAmount())));
        discountedTotal.setText(String.format("%s%d",getResources().getString(R.string.Rs),order.getDiscountedAmount()));
    }

    private void attachID() {
        toolbar.setTitle(order.getPoolInfo().getName());
        qr_image = findViewById(R.id.qr_image);
        orderStatus = findViewById(R.id.order_status);
        userBillID = findViewById(R.id.userBillIDText);
        recyclerView = findViewById(R.id.recycleView);
        orderStatusIcon = findViewById(R.id.order_status_icon);
        deliveredTag = findViewById(R.id.delivered_tag);
        itemTotal = findViewById(R.id.item_total);
        discountTotal = findViewById(R.id.discount_total);
        discountedTotal = findViewById(R.id.discounted_total);

        adapter = new PoolItemCartAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        defineListener();
    }

    private void defineListener() {
        poolItemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                poolItems.clear();
                for (DataSnapshot items : dataSnapshot.getChildren()) {
                    PoolItem dish = items.getValue(PoolItem.class);
                    poolItems.add(dish);
                }
                loadOrderItemList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO on cancel
            }
        };
        orderItemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                  PoolItem orderItem =null;
                  for(int i = 0 ; i < poolItems.size();i++){
                      if(poolItems.get(i).getItemID().compareTo(item.getKey())==0){
                          orderItem = poolItems.get(i);
                          orderItem.setQuantity(item.getValue(PoolItem.class).getQuantity());
                          break;
                      }
                  }
                  if(orderItem != null){
                      adapter.insertAtEnd(orderItem);
                  }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void loadOrderItemList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(Order.URL_MY_ORDER_ITEM_LIST,
                communityReference, userUID, order.getOrderID()));
        Log.d(TAG, "loadItemView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(orderItemListener);
    }
}
