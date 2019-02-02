package com.zconnect.zutto.zconnect.pools;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.zconnect.zutto.zconnect.LoginActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.pools.adapters.PoolItemCartAdapter;
import com.zconnect.zutto.zconnect.pools.models.DiscountOffer;
import com.zconnect.zutto.zconnect.pools.models.Order;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.pools.models.PoolInfo;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_PAYMENT_DONE;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_PAYMENT_FAILED;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_PAYMENT_PENDING;

public class PoolBillActivity extends BaseActivity implements PaymentResultListener {

    public static final String TAG = "PoolBillActivity";

    private RecyclerView recyclerView;
    private PoolItemCartAdapter adapter;

    private TextView sub_total, discount, total;
    private Integer total_quantity;
    private Button btn_pay;
    private float subTotal_amount, total_amount;
    private double discount_amount;
    private String shopID, poolPushID, poolID, communityID, userUID, userName, poolName;
    private long deliveryTime;
    private ProgressBar progressBar;
    private LinearLayout billLinearLayout;
    private String orderID;
    private HashMap<String,PoolItem> orderList;
    private Vector<PoolItem> orderListVector = new Vector<>();
    private Pool currentPool;
    private ValueEventListener poolOfferListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_bill);
        Bundle b = getIntent().getExtras();
        if (b != null) {

            Toast.makeText(this, "Bundle Null", Toast.LENGTH_SHORT).show();

            if (b.containsKey("orderList") && b.containsKey("pool")) {

                Toast.makeText(this, "orderList not present", Toast.LENGTH_SHORT).show();

                orderList  = (HashMap<String,PoolItem>) getIntent().getSerializableExtra("orderList");
                currentPool = (Pool) getIntent().getSerializableExtra("pool");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {

                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    setToolbar();
                    attachID();
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

                    loadCartData();
                    btn_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pushOrderData();
                        }
                    });
                    showToast("payment is possibly showing");

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

    private void pushOrderData(){

        DatabaseReference usersOrdersRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("shops").child("orders").child("current").child(FirebaseAuth.getInstance().getUid());
        orderID = usersOrdersRef.push().toString();

        HashMap<String, Object> orderObject = new HashMap<>();
        orderObject.put(Order.ORDER_ID,orderID);
        orderObject.put(Order.POOL_PUSH_ID,currentPool.getPoolPushID());
        orderObject.put(Order.STATUS,OtherKeyUtilities.KEY_PAYMENT_PENDING);
        orderObject.put(Order.TIMESTAMP_PAYMENT_STARTED,ServerValue.TIMESTAMP);
        orderObject.put(Order.TOTAL_AMOUNT,total_amount);
        orderObject.put(Order.POOL_INFO,currentPool.getPoolInfo());
        orderObject.put(Order.ITEMS,orderList);
        orderObject.put(Order.DELIVERY_TIME, currentPool.getDeliveryTime());

        usersOrdersRef.child(orderID).setValue(orderObject).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startPayment();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO Error in saving order : amount will be refunded within 3-5 days
            }
        });

    }

    private void startPayment() {
        Checkout checkout = new Checkout();

        checkout.setImage(R.drawable.logo);

        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();

            options.put("name", "ZConnect");

            options.put("description", "Order #123456");

            options.put("currency", "INR");

            /**
             * Amount is always passed in PAISE
             * Eg: "500" = Rs 5.00
             */
            options.put("amount", String.valueOf(total_amount * 100));

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
            showToast("Error in starting Razorpay Checkout");
        }
    }

    private void loadCartData() {

        total_quantity = 0;
        subTotal_amount = 0;

        orderListVector.clear();
        for (HashMap.Entry<String, PoolItem> entry : orderList.entrySet()) {
            PoolItem item;
            item = entry.getValue();
            adapter.insertAtEnd(item);
            orderListVector.add(item);

            subTotal_amount += item.getQuantity() * item.getPrice();
            total_quantity += item.getQuantity();
        }

        poolID = currentPool.getPoolInfo().getPoolID();
        poolPushID = currentPool.getPoolPushID();
        shopID = currentPool.getPoolInfo().getShopID();

        poolName = currentPool.getPoolInfo().getName();
        deliveryTime = currentPool.getDeliveryTime();

        poolOfferListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    DiscountOffer discountOffer = dataSnapshot.getValue(DiscountOffer.class);

                    //set amount view
                    //calculate discount
                    int discPer = discountOffer.getDiscountPercentage();
                    int max_discount = discountOffer.getMaxDiscount();
                    int min_items = discountOffer.getMinQuantity();

                    if (min_items == 0) {
                        min_items = Integer.MAX_VALUE;
                    }
                    if (total_quantity < min_items) {
                        discount_amount = 0;
                    } else {
                        discount_amount = ((float) (subTotal_amount * discPer)) / 100.00f;
                        discount_amount = Math.min(discount_amount, max_discount);
                    }
                    Log.d(TAG, "loadCartData : discount Amount " + String.valueOf(discount_amount) + " total quantity :" + String.valueOf(total_quantity) +
                            " min item : " + String.valueOf(min_items) + " Discount percentage : " + String.valueOf(discPer) + "Max discount : " + String.valueOf(max_discount));
                    total_amount = subTotal_amount - (int) discount_amount;
                    sub_total.setText(String.format("%s%s", getResources().getString(R.string.Rs), String.valueOf(subTotal_amount)));
                    discount.setText(String.format("-%s%s", getResources().getString(R.string.Rs), String.valueOf(discount_amount)));
                    total.setText(String.format("%s%s", getResources().getString(R.string.Rs), String.valueOf(total_amount)));

                    progressBar.setVisibility(View.GONE);
                    billLinearLayout.setVisibility(View.VISIBLE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolInfo.URL_POOL_OFFER, currentPool.getPoolInfo().getShopID(), currentPool.getPoolInfo().getPoolID()));
        Log.d(TAG, "setPoolView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(poolOfferListener);

    }

    private void attachID() {
        toolbar.setTitle("Bill");
        recyclerView = findViewById(R.id.recycleView);
        sub_total = findViewById(R.id.tv_subTotal);
        discount = findViewById(R.id.tv_discount);
        total = findViewById(R.id.tv_total_amount);
        btn_pay = findViewById(R.id.btn_pay);
        progressBar = findViewById(R.id.progress_bar);
        billLinearLayout = findViewById(R.id.bill_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PoolItemCartAdapter();
        recyclerView.setAdapter(adapter);
        Checkout.preload(getApplicationContext());
        progressBar.setVisibility(View.VISIBLE);
        billLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPaymentSuccess(String s) {
        paymentSuccess(s);
    }


    @Override
    public void onPaymentError(int i, String s) {
        DatabaseReference usersOrdersRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("shops").child("orders").child("current").child(FirebaseAuth.getInstance().getUid());

        usersOrdersRef.child(orderID).child("orderStatus").setValue(KEY_PAYMENT_FAILED);

        showToast("Payment Error : " + s + "  i = " + String.valueOf(i));
    }

    private void paymentSuccess(final String paymentID) {

        DatabaseReference usersOrdersRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("shops").child("orders").child("current").child(FirebaseAuth.getInstance().getUid());

        HashMap<String, Object> paymentDetails = new HashMap<>();

        paymentDetails.put("orderStatus",KEY_PAYMENT_DONE);
        paymentDetails.put("paymentGatewayID",paymentID);

        usersOrdersRef.child(orderID).updateChildren(paymentDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(PoolBillActivity.this, PaymentCaptureActivity.class);
                intent.putExtra("paymentID", paymentID);
                intent.putExtra("amount", total_amount);
                startActivity(intent);
                finish();
            }
        });
//        HashMap<String, Object> mp = new HashMap<>();
//        //order id is same as payment id that is razor pay id
//
//        //hashing info on owners end
//        String shop_base = "shopOwner/" + shopID + "/orders/" + poolPushID + "/" + paymentID + "/items/";
//        ArrayList<PoolItem> items = adapter.getPoolsList();
//        for (PoolItem item : items) {
//            mp.put(shop_base + item.getPoolPushID(), item.getQuantity());
//        }
//        shop_base = "shopOwner/" + shopID + "/orders/" + poolPushID + "/" + paymentID + "/paymentInfo/";
//        mp.put(shop_base + "amount", total_amount);
//        mp.put(shop_base + "razorPayID", paymentID);
//        mp.put(shop_base + "userUID", userUID);
//        mp.put(shop_base + "userName", userName);
//        mp.put(shop_base + "status", "processing");
//
//        //hashing info on user end
//        shop_base = "features/shops/orders/" + userUID + "/" + paymentID + "/";
//        mp.put(shop_base + "amount", total_amount);
//        mp.put(shop_base + "razorPayID", paymentID);
//        mp.put(shop_base + Pool.SHOP_ID, shopID);
//        mp.put(shop_base + Pool.POOL_ID, poolID);
//        mp.put(shop_base + Pool.POOL_PUSH_ID, poolPushID);
//        mp.put(shop_base + "poolName", poolName);
//        mp.put(shop_base + "orderStatus", "Waiting payment confirmation");
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//        ref.child("communities").child(communityReference).child("features").child("shops").child("orders").updateChildren(mp).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                //TODO Error in saving order : amount will be refunded within 3-5 days
//            }
//        });

    }

}
