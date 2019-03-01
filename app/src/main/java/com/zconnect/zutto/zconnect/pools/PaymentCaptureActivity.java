package com.zconnect.zutto.zconnect.pools;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.pools.models.Order;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;

public class PaymentCaptureActivity extends BaseActivity {

    public static final String TAG = "PaymentCaptureActivity";

    private TextView amountTV;

    private LinearLayout ll_progressBar, nextStepsLL;
    private TextView loading_text, paymentStatusText;
    private Button nextButton;
    private DatabaseReference orderRef;
    private String orderID;
    private Order order;
    private ImageView paymentStatusImage;
    private ProgressBar progressBar;

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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        attachID();
        Bundle b = getIntent().getExtras();
        orderID = b.getString("orderID");
        amountTV.setText(b.getString("amount"));
        orderRef = FirebaseDatabase.getInstance().getReference(String.format(Order.URL_MY_PARTICULAR_ORDER, communityReference, FirebaseAuth.getInstance().getUid(), orderID));
        progressBar.setVisibility(View.VISIBLE);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("paymentStatus").getValue(String.class).equals(Order.KEY_PAYMENT_SUCCESS))
                {
                    progressBar.setVisibility(View.GONE);
                    nextButton.setVisibility(View.VISIBLE);
                    nextStepsLL.setVisibility(View.VISIBLE);
                    amountTV.setVisibility(View.VISIBLE);
                    order = dataSnapshot.getValue(Order.class);
                    paymentStatusImage.setBackground(getApplicationContext().getDrawable(R.drawable.ic_check_green_120dp));
                    paymentStatusText.setText("Payment Successful");
                }
                else if(dataSnapshot.child("paymentStatus").getValue(String.class).equals(Order.KEY_PAYMENT_FAIL))
                {
                    progressBar.setVisibility(View.GONE);
                    nextButton.setVisibility(View.GONE);
                    nextStepsLL.setVisibility(View.GONE);
                    paymentStatusImage.setBackground(getApplicationContext().getDrawable(R.drawable.ic_error_outline_red500_120dp));
                    paymentStatusText.setText("Payment Failed");
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
                intent.putExtra("orderID", orderID);
                startActivity(intent);
            }
        });
    }


    private void attachID() {
        amountTV = findViewById(R.id.total_pay_amount);
        ll_progressBar = findViewById(R.id.ll_progressBar);
        loading_text = findViewById(R.id.loading_text);
        nextButton = findViewById(R.id.next_btn);
        nextStepsLL = findViewById(R.id.next_steps_layout);
        paymentStatusImage = findViewById(R.id.paymentstatus_image);
        paymentStatusText = findViewById(R.id.paymentstatus_text);
        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PoolActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }
}
