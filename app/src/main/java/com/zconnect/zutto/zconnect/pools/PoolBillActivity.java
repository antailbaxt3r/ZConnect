package com.zconnect.zutto.zconnect.pools;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.LoginActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.pools.adapters.PoolItemCartAdapter;
import com.zconnect.zutto.zconnect.pools.models.DiscountOffer;
import com.zconnect.zutto.zconnect.pools.models.Order;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.pools.models.PoolInfo;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;
import com.zconnect.zutto.zconnect.utilities.UIUtilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_PAYMENT_FAIL;

public class PoolBillActivity extends BaseActivity implements PaymentResultListener {

    public static final String TAG = "PoolBillActivity";

    private RecyclerView recyclerView;
    private PoolItemCartAdapter adapter;

    private TextView sub_total, discount, total;
    private Integer total_quantity;
    private Button btn_pay;
    private float total_amount, discounted_amount;
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
    private MaterialEditText phoneNumberET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_bill);
        Bundle b = getIntent().getExtras();
        if (b != null) {

            if (b.containsKey("orderList") && b.containsKey("pool")) {

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
                    loadPhoneNumber();
                    btn_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pushOrderData();
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

    private void loadPhoneNumber() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(userUID);
        userRef.child("mobileNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                    phoneNumberET.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void pushOrderData(){
        UIUtilities ui = new UIUtilities();
        if(phoneNumberET.getText().length()<1)
            ui.getSnackbar(phoneNumberET, "Please enter your mobile number", Snackbar.LENGTH_SHORT, getApplicationContext()).show();
        else if(phoneNumberET.getText().length()<10)
            ui.getSnackbar(phoneNumberET, "Please enter a valid mobile number", Snackbar.LENGTH_SHORT, getApplicationContext()).show();
        else
        {
            DatabaseReference usersOrdersRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("shops").child("orders").child("current").child(FirebaseAuth.getInstance().getUid());
            orderID = usersOrdersRef.push().getKey();

            HashMap<String, Object> orderObject = new HashMap<>();
            orderObject.put(Order.ORDER_ID,orderID);
            orderObject.put(Order.POOL_PUSH_ID,currentPool.getPoolPushID());
            Log.i(TAG, currentPool.getPoolPushID() + "1234");
            orderObject.put(Order.PAYMENT_STATUS,OtherKeyUtilities.KEY_PAYMENT_PENDING);
            orderObject.put(Order.TIMESTAMP_PAYMENT_BEFORE,ServerValue.TIMESTAMP);
            orderObject.put(Order.TOTAL_AMOUNT, total_amount);
            orderObject.put(Order.DISCOUNTED_AMOUNT, discounted_amount);
            orderObject.put(Order.POOL_INFO,currentPool.getPoolInfo());
            orderObject.put(Order.ITEMS,orderList);
            orderObject.put(Order.DELIVERY_TIME, currentPool.getDeliveryTime());
            orderObject.put(Order.PHONE_NUMBER, phoneNumberET.getText());

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
    }

    private void startPayment() {
        Checkout checkout = new Checkout();

        checkout.setImage(R.drawable.logo);

        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();

            options.put("name", "ZConnect");

            options.put("description", currentPool.getPoolInfo().getName());

            options.put("currency", "INR");

            /**
             * Amount is always passed in PAISE
             * Eg: "500" = Rs 5.00
             */
            options.put("amount", String.valueOf(discounted_amount * 100));

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
            showToast("Error in starting Razorpay Checkout");
        }
    }

    private void loadCartData() {

        total_quantity = 0;
        total_amount = 0;

        orderListVector.clear();
        for (HashMap.Entry<String, PoolItem> entry : orderList.entrySet()) {
            PoolItem item;
            item = entry.getValue();
            adapter.insertAtEnd(item);
            orderListVector.add(item);

            total_amount += item.getQuantity() * item.getPrice();
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
                        discount_amount = ((float) (total_amount * discPer)) / 100.00f;
                        discount_amount = Math.min(discount_amount, max_discount);
                    }
                    Log.d(TAG, "loadCartData : discount Amount " + String.valueOf(discount_amount) + " total quantity :" + String.valueOf(total_quantity) +
                            " min item : " + String.valueOf(min_items) + " Discount percentage : " + String.valueOf(discPer) + "Max discount : " + String.valueOf(max_discount));
                    discounted_amount = total_amount - (int) discount_amount;
                    sub_total.setText(String.format("%s%s", getResources().getString(R.string.Rs), String.valueOf(total_amount)));
                    discount.setText(String.format("-%s%s", getResources().getString(R.string.Rs), String.valueOf(discount_amount)));
                    total.setText(String.format("%s%s", getResources().getString(R.string.Rs), String.valueOf(discounted_amount)));

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
        phoneNumberET = findViewById(R.id.phoneNumber);
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        paymentSuccess(razorpayPaymentId);
    }


    @Override
    public void onPaymentError(int code, String response) {
        switch (code) {
            case Checkout.PAYMENT_CANCELED:
                showToast("Payment Cancelled.", Toast.LENGTH_LONG);
                break;
            case Checkout.NETWORK_ERROR:
                Snackbar snackbar = (new UIUtilities()).getSnackbar(btn_pay, "Payment Failed. Please check connectivity.", Snackbar.LENGTH_LONG, getApplicationContext());
                snackbar.show();
                break;
            case Checkout.INVALID_OPTIONS:
            case Checkout.TLS_ERROR:
            case Checkout.INCOMPATIBLE_PLUGIN:
                showToast("Payment cannot be processed");
                break;
        }
        DatabaseReference usersOrdersRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("shops").child("orders").child("current").child(FirebaseAuth.getInstance().getUid());
        usersOrdersRef.child(orderID).removeValue();
    }

    private void paymentSuccess(final String paymentID) {

        DatabaseReference usersOrdersRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("shops").child("orders").child("current").child(FirebaseAuth.getInstance().getUid());

        HashMap<String, Object> paymentDetails = new HashMap<>();

        paymentDetails.put("paymentGatewayID",paymentID);

        usersOrdersRef.child(orderID).updateChildren(paymentDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(PoolBillActivity.this, PaymentCaptureActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("amount", discounted_amount);
                startActivity(intent);
                finish();
            }
        });
    }

}
