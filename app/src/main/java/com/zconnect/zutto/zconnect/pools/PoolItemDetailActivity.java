package com.zconnect.zutto.zconnect.pools;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolItemDetailAdapter;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.pools.models.PoolInfo;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

public class PoolItemDetailActivity extends AppCompatActivity {

    public static final String TAG = "PoolItemDetailActivity";

    private RecyclerView recyclerView;
    private TextView offers, description, joined_peoples;
    private LinearLayout ll_progressBar;
    private TextView loading_text;
    private Button btn_activate;

    private PoolItemDetailAdapter adapter;
    private ValueEventListener poolItemListener, poolOfferListener;

    private Pool pool;
    private String communityID, userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_item_detail);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("newPool")) {

                pool = Pool.getPool(b.getBundle("newPool"));
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    //TODO start login acitvity
                    finish();
                } else {
                    userUID = user.getUid();
                    //TODO set proper data from the preference
                    communityID = "testCollege";

                    //activity main block with all valid parameters

                    attachID();
                    setPoolInfo();
                    loadItemView();
                    setAdminView();

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

    private void setAdminView() {
        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activatePool();
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("communities/"+communityID+"/Users1/"+userUID+"/userType");
        Log.d(TAG,"setAdminView : ref "+ref.toString());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                if(type==null){
                    btn_activate.setVisibility(View.GONE);
                    btn_activate.setEnabled(false);
                }else if (type.compareToIgnoreCase("admin")==0){
                    btn_activate.setVisibility(View.VISIBLE);
                    btn_activate.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void activatePool() {
        btn_activate.setEnabled(false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(Pool.URL_POOL,communityID)).child(pool.getID());
        ref.child(Pool.STATUS).setValue(Pool.STATUS_ACTIVE).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                     showToast("Failed to change the state.");
            }
        });
    }
    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private void loadItemView() {
        setProgressBarView(View.VISIBLE, "Loading list\nplease wait..");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolItem.URL_POOL_ITEM,
                communityID, pool.getShopID(), pool.getPoolID()));
        Log.d(TAG, "loadItemView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(poolItemListener);
    }

    private void setPoolInfo() {
        getSupportActionBar().setTitle(pool.getName());
        description.setText(pool.getDescription());
        joined_peoples.setText("Votes : " + String.valueOf(pool.getUpVote()));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolInfo.URL_POOL_OFFER,
                communityID, pool.getShopID(), pool.getPoolID()));
        Log.d(TAG, "setPoolView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(poolOfferListener);
    }

    private void attachID() {

        recyclerView = findViewById(R.id.pool_item_rv);
        offers = findViewById(R.id.pool_offers);
        joined_peoples = findViewById(R.id.joined_peoples);
        description = findViewById(R.id.pool_description);
        ll_progressBar = findViewById(R.id.ll_progressBar);
        loading_text = findViewById(R.id.loading_text);
        btn_activate = findViewById(R.id.btn_activate);

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
                for (DataSnapshot items : dataSnapshot.getChildren()) {
                    PoolItem dish = items.getValue(PoolItem.class);
                    dish.setID(items.getKey());
                    adapter.insertAtEnd(dish);
                }
                setProgressBarView(View.GONE, "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO on cancel
                setProgressBarView(View.GONE, "");
            }
        };
        poolOfferListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {

                    int disPer = dataSnapshot.child(PoolInfo.DISCOUNT_PERCENTAGE).getValue(Integer.class);
                    int maxDiscount = dataSnapshot.child(PoolInfo.MAX_DISCOUNT).getValue(Integer.class);
                    int minQuantity = dataSnapshot.child(PoolInfo.MIN_QUANTITY).getValue(Integer.class);
                    // if(disPer != 0 && maxDiscount != 0 && minQuantity !=0)
                    offers.setVisibility(View.VISIBLE);
                    offers.setText(String.format("Discount Percentage : %d\nMax Discount %d\nMin Quantity : %d", disPer, maxDiscount, minQuantity));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void setProgressBarView(int visibility, String message) {
        ll_progressBar.setVisibility(visibility);
        loading_text.setText(message);

    }
}
