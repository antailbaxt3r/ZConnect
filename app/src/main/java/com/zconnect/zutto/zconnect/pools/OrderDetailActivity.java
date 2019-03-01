package com.zconnect.zutto.zconnect.pools;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.pools.adapters.PoolItemCartAdapter;
import com.zconnect.zutto.zconnect.pools.models.Pool;
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
    private ValueEventListener orderItemListener;
    private ArrayList<PoolItem> poolItems = new ArrayList<>();
    private TextView orderStatus, userBillID, itemTotal, discountedTotal, poolName;
//    private TextView discountTotal;
    private ImageView orderStatusIcon;
    private FrameLayout deliveredTag;
    private RelativeLayout paymentFailedLayout, paymentProcessingLayout, paymentPendingLayout;
    private LinearLayout paymentConfirmLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Bundle b = getIntent().getExtras();
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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
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
                    if(order==null)
                    {
                        String orderID = getIntent().getStringExtra("orderID");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(Order.URL_MY_PARTICULAR_ORDER, communityReference, FirebaseAuth.getInstance().getUid(), orderID));
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                order = dataSnapshot.getValue(Order.class);
                                attachID();
                                setOrderQRView();
                                loadItemView();
                                invalidateOptionsMenu();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else
                    {
                        attachID();
                        setOrderQRView();
                        loadItemView();
                    }
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
        poolName.setText(order.getPoolInfo().getName());
        itemTotal.setText(String.format("%s%s",getResources().getString(R.string.Rs),String.valueOf(order.getTotalAmount())));
//        discountTotal.setText(String.format("%s%s",getResources().getString(R.string.Rs),String.valueOf(order.getTotalAmount()-order.getDiscountedAmount())));
        discountedTotal.setText(String.format("%s%s",getResources().getString(R.string.Rs),String.valueOf(order.getDiscountedAmount())));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(Order.URL_MY_ORDER_ITEM_LIST, communityReference, userUID, order.getOrderID()));
        Log.d(TAG, "loadItemView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(orderItemListener);
    }

    private void setOrderQRView() {

        if (order.getPaymentStatus().equals(Order.KEY_PAYMENT_FAIL)) {
            paymentConfirmLayout.setVisibility(View.GONE);
            paymentProcessingLayout.setVisibility(View.GONE);
            paymentPendingLayout.setVisibility(View.GONE);
            paymentFailedLayout.setVisibility(View.VISIBLE);
        }
        else if(order.getPaymentStatus().equals(Order.KEY_PAYMENT_PENDING)) {
            paymentConfirmLayout.setVisibility(View.GONE);
            paymentProcessingLayout.setVisibility(View.GONE);
            paymentPendingLayout.setVisibility(View.VISIBLE);
            paymentFailedLayout.setVisibility(View.GONE);
        }
        else if(order.getPaymentStatus().equals(Order.KEY_PAYMENT_PROCESSING))
        {
            paymentConfirmLayout.setVisibility(View.GONE);
            paymentProcessingLayout.setVisibility(View.VISIBLE);
            paymentPendingLayout.setVisibility(View.GONE);
            paymentFailedLayout.setVisibility(View.GONE);
        }
        else if(order.getPaymentStatus().equals(Order.KEY_PAYMENT_SUCCESS)) {

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

            paymentConfirmLayout.setVisibility(View.VISIBLE);
            paymentProcessingLayout.setVisibility(View.GONE);
            paymentPendingLayout.setVisibility(View.GONE);
            paymentFailedLayout.setVisibility(View.GONE);

            if (order.getOrderStatus().equals(Order.KEY_ORDER_OUT_FOR_DELIVERY))
            {
                DateTime dateTime = new DateTime(order.getDeliveryTime(), DateTimeZone.forID("Asia/Kolkata"));
                String text = "Order out for delivery on " + dateTime.toString("MMM") + " " + dateTime.getDayOfMonth();
                orderStatus.setText(text);
                orderStatusIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.baseline_local_shipping_24));
                orderStatusIcon.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorHighlightLight), PorterDuff.Mode.SRC_ATOP);
                deliveredTag.setVisibility(View.GONE);
            } else if(order.getOrderStatus().equals(Order.KEY_ORDER_DELIVERED))
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
        }
    }

    private void attachID() {
        toolbar.setTitle(order.getPoolInfo().getName());
        poolName = findViewById(R.id.pool_name);
        qr_image = findViewById(R.id.qr_image);
        orderStatus = findViewById(R.id.order_status);
        userBillID = findViewById(R.id.userBillIDText);
        recyclerView = findViewById(R.id.recycleView);
        orderStatusIcon = findViewById(R.id.order_status_icon);
        deliveredTag = findViewById(R.id.delivered_tag);
        itemTotal = findViewById(R.id.item_total);
//        discountTotal = findViewById(R.id.discount_total);
        discountedTotal = findViewById(R.id.discounted_total);

        paymentFailedLayout = findViewById(R.id.payment_failed_layout);
        paymentConfirmLayout = findViewById(R.id.payment_confirm_layout);
        paymentProcessingLayout = findViewById(R.id.payment_processing_layout);
        paymentPendingLayout = findViewById(R.id.payment_pending_layout);

        adapter = new PoolItemCartAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        defineListener();
    }

    private void defineListener() {
        orderItemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clearDataset();
                for(DataSnapshot item : dataSnapshot.getChildren()){
                  PoolItem orderItem = item.getValue(PoolItem.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item_open_chat:
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(order.getPoolPushID()).toString());
                intent.putExtra("type", "forums");
                intent.putExtra("name", "Chat with seller");
                intent.putExtra("tab", Pool.POOL_FORUM_TAB_ID);
                intent.putExtra("key", order.getOrderID());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(order==null)
        {
            menu.findItem(R.id.item_open_chat).setVisible(false);
        }
        else
        {
            if(!order.getPaymentStatus().equals(Order.KEY_PAYMENT_SUCCESS))
            {
                //payment is successful but the orderStatus node has not been pushed into the database by firebase funciton
                menu.findItem(R.id.item_open_chat).setVisible(false);
            }
            else if(order.getOrderStatus().equals(Order.KEY_ORDER_OUT_FOR_DELIVERY))
            {
                menu.findItem(R.id.item_open_chat).setVisible(true);
            }
            else
            {
                menu.findItem(R.id.item_open_chat).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
