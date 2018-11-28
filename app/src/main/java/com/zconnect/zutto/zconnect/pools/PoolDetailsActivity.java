package com.zconnect.zutto.zconnect.pools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolDishAdapter;
import com.zconnect.zutto.zconnect.pools.models.ActivePool;
import com.zconnect.zutto.zconnect.pools.models.PoolDish;

public class PoolDetailsActivity extends AppCompatActivity {

    public static final String TAG = "PoolDetailsActivity";
    private int DUMMYS_NUMBER = 5;

    private Button btn_payment;
    private RecyclerView recyclerView;
    private TextView offers, joined_peoples;

    private PoolDishAdapter adapter;
    private ActivePool pool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_details);
        //base case
        Bundle b = getIntent().getExtras();
        if(b != null) {
            if(b.containsKey("newPool")){
                pool = ActivePool.getPool(b.getBundle("newPool"));
                //activity main block with all valid parameters

                attachID();
                setPoolInfo();
                loadItemView();



            }else {
                Log.d(TAG,"onCreate : bundle does not contain newPool key finishing activity");
                finish();
            }
        }else{
            Log.d(TAG,"onCreate : null bundle finishing activity");
            finish();
        }

    }

    private void loadItemView() {

    }

    private void setPoolInfo() {
        getSupportActionBar().setTitle(pool.getName());
        offers.setText(pool.getOffer());
        joined_peoples.setText(pool.getJoined());
    }

    private void attachID() {
        btn_payment = findViewById(R.id.payment_btn);
        recyclerView = findViewById(R.id.pool_item_rv);
        offers = findViewById(R.id.pool_offers);
        joined_peoples = findViewById(R.id.joined_peoples);

        //TODO remove dummy value

        //setup adapter
        adapter = new PoolDishAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        for (int i = 0; i < DUMMYS_NUMBER; i++) {
            adapter.insertAtEnd(PoolDish.dummyValues());
        }


    }
}
