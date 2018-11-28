package com.zconnect.zutto.zconnect.pools;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private ValueEventListener poolItemListener;

    private ActivePool pool;
    private String community_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_details);
        //base case
        Bundle b = getIntent().getExtras();
        if(b != null) {
            if(b.containsKey("newPool")){

                pool = ActivePool.getPool(b.getBundle("newPool"));
                //TODO set proper datat from the preference
                community_name = "testCollege";

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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolDish.URL_POOL_DISH,
                            community_name,pool.getShopID(),pool.getID()));
        Log.d(TAG,"loadItemView : ref "+ref.toString());
        ref.addListenerForSingleValueEvent(poolItemListener);
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

        defineListener();


    }

    private void defineListener() {
        poolItemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clearDataset();
                for(DataSnapshot items : dataSnapshot.getChildren()){
                    PoolDish dish = items.getValue(PoolDish.class);
                    dish.setID(items.getKey());
                    adapter.insertAtEnd(dish);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO on cancel
            }
        };
    }
}
