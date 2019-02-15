package com.zconnect.zutto.zconnect.pools;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.pools.models.Order;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;

public class PaymentCaptureActivity extends BaseActivity {

    public static final String TAG = "PaymentCaptureActivity";
    private static final String RAZOR_PAY_KEY = "rzp_test_KXsdrhWl1Mp45s";
    private static final String RAZOR_PAY_SECRET = "RTnQaWurA8LsntSyWHPTrE4t";

    private LinearLayout into_view;
    private TextView amountTV;

    private LinearLayout ll_progressBar;
    private TextView loading_text;
    private Button nextButton;
    private DatabaseReference orderRef;
    private String orderID;
    private ProgressBar nextBtnProgressBar;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_capture);

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
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        attachID();
        nextButton.setVisibility(View.GONE);
        nextBtnProgressBar.setVisibility(View.VISIBLE);
        Bundle b = getIntent().getExtras();
        orderID = b.getString("orderID");
        amountTV.setText(b.getString("amount"));
        orderRef = FirebaseDatabase.getInstance().getReference(String.format(Order.URL_MY_PARTICULAR_ORDER, communityReference, FirebaseAuth.getInstance().getUid(), orderID));
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("paymentStatus").getValue(String.class).equals(OtherKeyUtilities.KEY_PAYMENT_SUCCESS))
                {
                    ll_progressBar.setVisibility(View.GONE);
                    into_view.setVisibility(View.VISIBLE);
                }
                else
                {
                    setProgressBarVisible("Please do not press back. Confirming payment...");
                    into_view.setVisibility(View.GONE);
                }
                if(dataSnapshot.hasChild("orderStatus")
                        && dataSnapshot.hasChild("timestampPaymentAfter")
                        && dataSnapshot.hasChild("timestampPaymentBefore")
                        && dataSnapshot.hasChild("userBillID"))
                {
                    order = dataSnapshot.getValue(Order.class);
                    try {
                        Log.i("SLEEPING", "AF");
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    nextBtnProgressBar.setVisibility(View.GONE);
                    nextButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),OrderDetailActivity.class);
                intent.putExtra("order",order);
                startActivity(intent);
            }
        });

    }


    private void attachID() {
        into_view = findViewById(R.id.ll_info);
        amountTV = findViewById(R.id.total_pay_amount);
        ll_progressBar = findViewById(R.id.ll_progressBar);
        loading_text = findViewById(R.id.loading_text);
        nextBtnProgressBar = findViewById(R.id.next_btn_progress_bar);
        nextButton = findViewById(R.id.next_btn);
    }

    private void setProgressBarVisible(String message) {
        ll_progressBar.setVisibility(View.VISIBLE);
        loading_text.setText(message);

    }
}
