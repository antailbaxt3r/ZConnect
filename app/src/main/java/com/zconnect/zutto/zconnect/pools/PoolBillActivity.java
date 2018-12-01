package com.zconnect.zutto.zconnect.pools;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolItemCartAdapter;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

public class PoolBillActivity extends AppCompatActivity {

    public static final String TAG = "PoolBillActivity";

    private RecyclerView recyclerView;
    private PoolItemCartAdapter adapter;

    private TextView sub_totoal,discount,total;
    private Button btn_pay;
    private int subTotal_amount,discount_amount,totol_amount;

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

    private void loadCartData(Bundle list) {
        String[] ids = list.getStringArray(PoolItem.ITEM_ID);
        String[] imageURL = list.getStringArray(PoolItem.IMAGE_URL);
        String[] name = list.getStringArray(PoolItem.NAME);
        int[] quantities = list.getIntArray(PoolItem.QUANTITY);
        int[] prices = list.getIntArray(PoolItem.PRICE);
        for(int i = 0 ; i  < ids.length ; i++){
            PoolItem item = new PoolItem();
            item.setID(ids[i]);
            item.setImageURL(imageURL[i]);
            item.setName(name[i]);
            item.setQuantity(quantities[i]);
            item.setPrice(prices[i]);
            adapter.insertAtEnd(item);
            subTotal_amount += item.getQuantity()*item.getPrice();
        }

        //set amount view
        sub_totoal.setText(String.format("%s%d",getResources().getString(R.string.Rs),subTotal_amount));
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
    }
}
