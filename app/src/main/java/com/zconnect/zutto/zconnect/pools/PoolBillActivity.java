package com.zconnect.zutto.zconnect.pools;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolItemCartAdapter;
import com.zconnect.zutto.zconnect.pools.models.PoolInfo;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

import org.json.JSONObject;

public class PoolBillActivity extends AppCompatActivity implements PaymentResultListener {

    public static final String TAG = "PoolBillActivity";

    private RecyclerView recyclerView;
    private PoolItemCartAdapter adapter;

    private TextView sub_totoal,discount,total;
    private Button btn_pay;
    private int subTotal_amount,totol_amount;
    private float discount_amount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_bill);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("orderList")) {

                Bundle list = b.getBundle("orderList");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    //TODO start login acitvity
                    finish();
                } else {
                    attachID();
                    loadCartData(list);
                    btn_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startPayment();
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
            options.put("amount", String.valueOf(totol_amount*100));

            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
            showToast("Error in starting Razorpay Checkout");
        }
    }
    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private void loadCartData(Bundle list) {
        String[] ids = list.getStringArray(PoolItem.ITEM_ID);
        String[] imageURL = list.getStringArray(PoolItem.IMAGE_URL);
        String[] name = list.getStringArray(PoolItem.NAME);
        int[] quantities = list.getIntArray(PoolItem.QUANTITY);
        int[] prices = list.getIntArray(PoolItem.PRICE);
        int total_quantity = 0;
        subTotal_amount = 0;
        for(int i = 0 ; i  < ids.length ; i++){
            PoolItem item = new PoolItem();
            item.setID(ids[i]);
            item.setImageURL(imageURL[i]);
            item.setName(name[i]);
            item.setQuantity(quantities[i]);
            item.setPrice(prices[i]);
            adapter.insertAtEnd(item);
            subTotal_amount += item.getQuantity()*item.getPrice();
            total_quantity += item.getQuantity();
        }

        //set amount view
        //calculate discount
        int discPer = list.getInt(PoolInfo.DISCOUNT_PERCENTAGE);
        float max_discount = (float)list.getInt(PoolInfo.MAX_DISCOUNT);
        int min_items = list.getInt(PoolInfo.MIN_QUANTITY);
        if(min_items==0){
            min_items = Integer.MAX_VALUE;
        }
        if(total_quantity < min_items){
            discount_amount = 0;
        }else {
            discount_amount = ((float) (subTotal_amount* discPer))/100.00f;
            discount_amount = Math.min(discount_amount,max_discount);

        }
        Log.d(TAG,"loadCartData : discount Amount "+String.valueOf(discount_amount)+" total quantity :"+String.valueOf(total_quantity)+
                            " min item : "+String.valueOf(min_items)+  " Discount percentage : "+String.valueOf(discPer)+  "Max discount : "+String.valueOf(max_discount));
        totol_amount = subTotal_amount-(int)discount_amount;
        sub_totoal.setText(String.format("%s%d",getResources().getString(R.string.Rs),subTotal_amount));
        discount.setText(String.format("-%s%.2f",getResources().getString(R.string.Rs),discount_amount));
        total.setText(String.format("%s%d",getResources().getString(R.string.Rs),totol_amount));
    }

    private void attachID() {
        //TODO set proper title
        getSupportActionBar().setTitle("Pool Cart");
        recyclerView = findViewById(R.id.recycleView);
        sub_totoal = findViewById(R.id.tv_subTotal);
        discount = findViewById(R.id.tv_discount);
        total = findViewById(R.id.tv_total_amount);
        btn_pay = findViewById(R.id.btn_pay);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PoolItemCartAdapter();
        recyclerView.setAdapter(adapter);
        Checkout.preload(getApplicationContext());
    }

    @Override
    public void onPaymentSuccess(String s) {
        showToast("Payment Successufull");
    }

    @Override
    public void onPaymentError(int i, String s) {
        showToast("Payment Error : "+s+ "  i = "+String.valueOf(i));
    }
}
