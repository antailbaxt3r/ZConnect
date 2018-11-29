package com.zconnect.zutto.zconnect.pools;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolAddItemAdapter;
import com.zconnect.zutto.zconnect.pools.adapters.PoolItemDetailAdapter;
import com.zconnect.zutto.zconnect.pools.models.ActivePool;
import com.zconnect.zutto.zconnect.pools.models.PoolDish;
import com.zconnect.zutto.zconnect.pools.models.UpcomingPool;

public class PoolItemDetailActivity extends AppCompatActivity {

    public static final String TAG = "PoolItemDetailActivity";

    private RecyclerView recyclerView;
    private TextView offers, joined_peoples;
    private LinearLayout ll_progressBar;
    private TextView loading_text;

    private PoolItemDetailAdapter adapter;
    private ValueEventListener poolItemListener;

    private UpcomingPool pool;
    private String community_name,userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_item_detail);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            if(b.containsKey("newPool")){

                pool = UpcomingPool.getPool(b.getBundle("newPool"));
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user==null){
                    //TODO start login acitvity
                    finish();
                }else {
                    userUID = user.getUid();
                    //TODO set proper data from the preference
                    community_name = "testCollege";

                    //activity main block with all valid parameters

                    attachID();
                    setPoolInfo();
                    loadItemView();

                }




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
        setProgressBarView(View.VISIBLE,"Loading list\nplease wait..");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolDish.URL_POOL_DISH,
                community_name,pool.getShopID(),pool.getID()));
        Log.d(TAG,"loadItemView : ref "+ref.toString());
        ref.addListenerForSingleValueEvent(poolItemListener);
    }

    private void setPoolInfo() {
        getSupportActionBar().setTitle(pool.getName());
        offers.setText(pool.getOffer());
        joined_peoples.setText(pool.getUpVote());
    }

    private void attachID() {

        recyclerView = findViewById(R.id.pool_item_rv);
        offers = findViewById(R.id.pool_offers);
        joined_peoples = findViewById(R.id.joined_peoples);
        ll_progressBar = findViewById(R.id.ll_progressBar);
        loading_text = findViewById(R.id.loading_text);

        //setup adapter
        adapter = new PoolItemDetailAdapter();
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
                setProgressBarView(View.GONE,"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO on cancel
                setProgressBarView(View.GONE,"");
            }
        };
    }

    private void setProgressBarView(int visibility,String message){
        ll_progressBar.setVisibility(visibility);
        loading_text.setText(message);

    }
}
